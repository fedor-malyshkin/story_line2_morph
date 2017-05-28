package ru.nlp_project.story_line2.morph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import ru.nlp_project.story_line2.morph.EndingModel.Ending;

/**
 * Morph database converter.
 * 
 * Политика обработки тэгов такова:
 * <ol>
 * <li>каждый метод обрабатывает свой тэг вплоть до перехода на закрывающий тэг самого себя --
 * дальнейшая ответственность за вызывающим кодом</li>
 * <li>Каждый метод в начале проверяет, что фрагмент для которого он был вызван -- его, в обратном
 * случае ничего не делает.</li>
 * <li>После проверки и чтения всех аттрибутов -- переходим к следующему тэг</li>
 * <li>После проведения всех работ и чтения внутренних тэгов -- переходим к закрывающему тэгу</li>
 * </ol>
 * 
 * 
 * @author fedor
 *
 */
@SuppressWarnings("unused")
public class MorphDBConverter {

	class Paradigm {
		List<Collection<String>> grammemas = new ArrayList<Collection<String>>();
		String opencorporaId = null;
		String lemma;
		List<String> wordforms = new ArrayList<String>();

		public void addWordform(String wordforms, Collection<String> tags) {
			this.wordforms.add(wordforms);
			this.grammemas.add(new ArrayList<String>(tags));
		}

		@Override
		public String toString() {
			return "Paradigm [lemma=" + lemma + ", grammemas=" + grammemas + ", wordforms="
					+ wordforms + "]";
		}

	}

	public static void main(String[] args) throws FileNotFoundException {
		MorphDBConverter converter = new MorphDBConverter();
		converter.initialize();
		File zippedMorphBD = converter.downloadZippedMorphBD();
		converter.readZippedMorphDB(zippedMorphBD);
	}

	private Logger log;
	private Map<Integer, List<EndingModel>> emCache;
	private int endingModelNumber = 0;
	private FileOutputStream fos;
	private JsonFactory jsonFactory;
	private JsonGenerator jg;

	public MorphDBConverter() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	void addParadigm(Paradigm paradigm) throws IOException {
		long start = System.nanoTime();
		int prefixLen = calculateLongestCommonPrefix(paradigm);

		long start1 = System.nanoTime();
		long duration = start1 - start;
		// log.info(String.format("> 'calculateLongestCommonPrefix' duration: %d
		// ms",TimeUnit.NANOSECONDS.toMillis(duration)));

		/*
		 * В случае наличия сравнительной степени "по-" выполняем разделение на 2 части одной леммы
		 * (для уменьшения кол-ва моделей окончаний)
		 */
		if (prefixLen == 0) {
			Iterator<Collection<String>> iteratorG = paradigm.grammemas.iterator();
			Iterator<String> iteratorB = paradigm.wordforms.iterator();
			Paradigm next = new Paradigm();
			next.opencorporaId = paradigm.opencorporaId;
			while (iteratorB.hasNext()) {
				String wordform = iteratorB.next();
				Collection<String> grammemas = iteratorG.next();
				if (grammemas.contains("Cmp2")) {
					next.addWordform(wordform, grammemas);
					// не забываем удалять обнаруженные слово формы и граммемы
					iteratorB.remove();
					iteratorG.remove();
				}
			}
			// если что-то выскали....
			if (next.wordforms.size() > 0) {
				addParadigm(next);
				prefixLen = calculateLongestCommonPrefix(paradigm);
			}
		}

		EndingModel endingModel = createEndingModel(prefixLen, paradigm);
		long start2 = System.nanoTime();
		duration = start2 - start1;
		// log.info(String.format("> 'createEndingModel' duration: %d
		// ms",TimeUnit.NANOSECONDS.toMillis(duration)));

		int emn = 0;
		emn = processEndingModel(paradigm, endingModel);


		long start3 = System.nanoTime();
		duration = start3 - start2;
		// log.info(String.format("> 'processEndingModel' duration: %d
		// ms",TimeUnit.NANOSECONDS.toMillis(duration)));

		addWordforms(prefixLen, emn, paradigm);
		duration = System.nanoTime() - start;
		// log.info(String.format("> 'addParadigm' duration: %d ms",
		// TimeUnit.NANOSECONDS.toMillis(duration)));
	}

	void addWordforms(int prefixLen, int emn, Paradigm paradigm) throws IOException {
		if (paradigm.wordforms.size() > 0)
			jsonWriteP(paradigm.lemma, paradigm.opencorporaId,
					paradigm.wordforms.get(0).substring(0, prefixLen), emn);
	}

	int calculateLongestCommonPrefix(Paradigm paradigm) {
		int res = 0;
		if (paradigm.wordforms.size() == 0)
			return res;
		res = paradigm.wordforms.get(0).length();
		if (paradigm.wordforms.size() < 2)
			return res;

		boolean notMatch = true;
		String str = null;
		while (notMatch) {
			str = paradigm.wordforms.get(0).substring(0, res);
			notMatch = false;
			for (int i = 1; i < paradigm.wordforms.size(); i++) {
				if (!paradigm.wordforms.get(i).startsWith(str))
					notMatch = true;
			}
			notMatch = notMatch || false;
			if (notMatch) {
				res--;
				if (res == 0)
					return res;
			}
		}
		return res;
	}

	EndingModel createEndingModel(int prefixLen, Paradigm paradigm) {
		EndingModel result = new EndingModel();
		for (int i = 0; i < paradigm.wordforms.size(); i++) {
			Grammemes grammemes = new Grammemes();
			GrammemeUtils.fillGrammemesOpencorporaTags(paradigm.grammemas.get(i), grammemes);
			result.addEnding(paradigm.wordforms.get(i).substring(prefixLen), grammemes);
		}
		result.calculateHashCode();
		return result;
	}

	Paradigm createParadigm() {
		return new Paradigm();
	}

	private void gotoCloseTag(XMLStreamReader parser, String tag) throws XMLStreamException {
		while (true) {
			if (parser.getEventType() == XMLStreamConstants.END_ELEMENT
					&& parser.getLocalName().equals(tag))
				break;
			parser.next();
		}
	}

	void initialize() {
		emCache = new HashMap<Integer, List<EndingModel>>(200);
	}

	private boolean isStartTag(XMLStreamReader parser, String tag) throws XMLStreamException {
		return parser.getEventType() == XMLStreamConstants.START_ELEMENT
				&& parser.getLocalName().equals(tag);
	}

	private String joinGrammems(Collection<GrammemeEnum> grammemas) {
		StringBuffer sb = new StringBuffer();
		int counter = 0;
		for (GrammemeEnum g : grammemas) {
			if (counter > 0)
				sb.append(",");
			sb.append(g.toString());
			counter++;
		}
		return sb.toString();

	}

	void jsonEnd() throws Exception {
		jg.close();
		IOUtils.closeQuietly(fos);
	}

	private void jsonEndPS() throws IOException {
		jg.writeEndArray();
	}

	void jsonStart() throws Exception {
		fos = new FileOutputStream("out.json");
		jsonFactory = new JsonFactory(); // or, for data binding,
		jg = jsonFactory.createGenerator(fos);
		jg.useDefaultPrettyPrinter();
	}

	private void jsonStartPS() throws IOException {
		jg.writeStartObject();
		jg.writeFieldName("PS");
		jg.writeStartArray();
	}

	void jsonWriteEndingModels() throws IOException {

		jg.writeFieldName("EMS");
		jg.writeStartArray();

		for (Entry<Integer, List<EndingModel>> entrySet : emCache.entrySet()) {
			for (EndingModel em : entrySet.getValue()) {

				jg.writeStartObject();

				jg.writeFieldName("n");
				jg.writeNumber(em.modelNum);
				jg.writeFieldName("es");

				jg.writeStartArray();

				for (Ending e : em.endings) {
					jg.writeStartObject();

					jg.writeFieldName("e");
					jg.writeString(e.value);
					/*
					 * jg.writeFieldName("mg"); String gs =
					 * joinGrammems(e.grammemes.mainGrammems.values()); jg.writeString(gs);
					 */
					jg.writeFieldName("ag");
					String gs = joinGrammems(e.grammemes.grammSet);
					jg.writeString(gs);

					jg.writeEndObject();
				}

				jg.writeEndArray();
				jg.writeEndObject();

			}
		}
		jg.writeEndArray();
		jg.writeEndObject();
	}

	void jsonWriteP(String lemma, String opencorporaId, String base, int emn) throws IOException {
		jg.writeStartObject();
		jg.writeFieldName("l");
		jg.writeString(lemma);
		jg.writeFieldName("id");
		jg.writeString(opencorporaId);
		jg.writeFieldName("b");
		jg.writeString(base);
		jg.writeFieldName("emn");
		jg.writeNumber(emn);
		jg.writeEndObject();
	}

	private boolean proccessGrammeme(XMLStreamReader parser, Collection<String> grammemes)
			throws XMLStreamException {
		if (!isStartTag(parser, "g"))
			return false;
		grammemes.add(parser.getAttributeValue(null, "v"));
		gotoCloseTag(parser, "g");
		return true;
	}

	private int processEndingModel(Paradigm paradigm, EndingModel endingModel) {
		int result = 0;
		List<EndingModel> list = emCache.get(endingModel.hashCode);
		if (list != null) {
			if (list.contains(endingModel)) {
				int indexOf = list.indexOf(endingModel);
				EndingModel endingModel2 = list.get(indexOf);
				return endingModel2.modelNum;
			} else {
				// << GENERATE NEW modelNum
				result = endingModelNumber++;
				endingModel.modelNum = result;

				list.add(endingModel);
				emCache.put(endingModel.hashCode, list);
			}
		} else {
			// << GENERATE NEW modelNum
			result = endingModelNumber++;
			endingModel.modelNum = result;

			list = new ArrayList<EndingModel>();
			list.add(endingModel);
			emCache.put(endingModel.hashCode, list);
		}

		return result;
	}

	private boolean processLemms(XMLStreamReader parser, Paradigm paradigm,
			Collection<String> constGrammemes) throws XMLStreamException {
		if (!isStartTag(parser, "l"))
			return false;
		paradigm.lemma = parser.getAttributeValue(null, "t");
		parser.nextTag();
		while (proccessGrammeme(parser, constGrammemes))
			parser.nextTag();
		gotoCloseTag(parser, "l");
		return true;
	}

	private boolean processParadigm(XMLStreamReader parser, Paradigm paradigm)
			throws XMLStreamException {
		if (!isStartTag(parser, "lemma"))
			return false;

		String id = parser.getAttributeValue(null, "id");
		paradigm.opencorporaId = id;
		parser.nextTag();

		List<String> constGrammemes = new ArrayList<String>();

		// collect base data

		processLemms(parser, paradigm, constGrammemes);
		parser.nextTag();

		// collect forms data
		while (processWordform(parser, paradigm, constGrammemes))
			parser.nextTag();

		gotoCloseTag(parser, "lemma");
		return true;
	}

	private boolean processWordform(XMLStreamReader parser, Paradigm paradigm,
			Collection<String> constGrammemes) throws XMLStreamException {
		if (!isStartTag(parser, "f"))
			return false;

		List<String> variableGrammemes = new ArrayList<String>();

		String formValue = parser.getAttributeValue(null, "t");

		parser.nextTag();
		while (proccessGrammeme(parser, variableGrammemes))
			parser.nextTag();

		variableGrammemes.addAll(constGrammemes);
		paradigm.addWordform(formValue, variableGrammemes);

		gotoCloseTag(parser, "f");
		return true;
	}

	private void convertMorphDBInternal(InputStream is) throws XMLStreamException, IOException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory.createXMLStreamReader(is);

		while (true) {
			if (parser.getEventType() == XMLStreamConstants.START_ELEMENT
					&& parser.getLocalName().equals("lemmata"))
				break;
			parser.next();
		}
		parser.nextTag();

		jsonStartPS();
		Paradigm paradigm = new Paradigm();
		int counter = 0;
		while (processParadigm(parser, paradigm)) {
			long start = System.nanoTime();

			addParadigm(paradigm);
			counter++;
			parser.nextTag();
			paradigm = new Paradigm();

			if (counter > 100) {
				counter = 0;
			}
		}
		jsonEndPS();
		jsonWriteEndingModels();
	}

	/**
	 * Прочитать запакованую морфологическую БД.
	 * 
	 * @param zippedMorphBD
	 */
	public void readZippedMorphDB(File zippedMorphBD) {
		try {
			File uncompressZip = uncompressZip(zippedMorphBD, "dict.opcorpora.xml");
			FileInputStream fis = new FileInputStream(uncompressZip);
			log.info("Start converting XML->JSON morph db.");
			jsonStart();
			convertMorphDBInternal(fis);
			jsonEnd();
			log.info("End converting XML->JSON morph db.");
			IOUtils.closeQuietly(fis);
		} catch (Exception ex) {
			throw new IllegalStateException(ex.getMessage(), ex);
		}
	}

	File uncompressZip(File zippedMorphBD, String entry) throws IOException {
		ZipFile zipFile = new ZipFile(zippedMorphBD);
		InputStream zipIS = zipFile.getInputStream(zipFile.getEntry(entry));

		File xmlFile = File.createTempFile("morph-db", ".xml");
		fos = new FileOutputStream(xmlFile);
		IOUtils.copy(zipIS, fos);
		IOUtils.closeQuietly(fos);
		zipFile.close();
		xmlFile.deleteOnExit();
		return xmlFile;
	}

	File downloadZippedMorphBD() {
		return downloadZippedMorphBD(
				"http://opencorpora.org/files/export/dict/dict.opcorpora.xml.zip");
	}

	File downloadZippedMorphBD(String url) {
		try {
			URL url2 = new URL(url);
			URLConnection connection = url2.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			File zipFile = File.createTempFile("morph-db-download", ".zip");
			FileOutputStream fos2 = new FileOutputStream(zipFile);
			IOUtils.copy(inputStream, fos2);
			return zipFile;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

}

package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.nlp_project.story_line2.morph.GrammemeEnum;
import ru.nlp_project.story_line2.morph.GrammemeUtils;
import ru.nlp_project.story_line2.morph.Grammemes;

/**
 * Чтение разметки opencorpora (http://opencorpora.org) со снятой оноимией.
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
public class OpencorporaMarkupReader {
	class SentenceToken {
		String word;
		String lemm;
		List<String> grammemes;

		public SentenceToken(String word) {
			super();
			this.word = word;
			this.grammemes = new ArrayList<>();
		}

		public void addGrammeme(String grammeme) {
			this.grammemes.add(grammeme);
		}

		@Override
		public String toString() {
			return "SentenceToken [word=" + word + ", grammemes=" + grammemes + "]";
		}

	}

	private IHMMPOSTaggerDBBuilder builder;
	private Logger log;

	private List<SentenceToken> tokens = new ArrayList<>();

	public OpencorporaMarkupReader(IHMMPOSTaggerDBBuilder builder) {
		log = LoggerFactory.getLogger(this.getClass());
		this.builder = builder;
	}



	File downloadZippedMarkupFile() {
		return downloadZippedMarkupFile(
				"http://opencorpora.org/files/export/annot/annot.opcorpora.no_ambig.xml.zip");
	}

	File downloadZippedMarkupFile(String url) {
		try {
			URL url2 = new URL(url);
			URLConnection connection = url2.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			File zipFile = File.createTempFile("markup-download", ".zip");
			FileOutputStream fos = new FileOutputStream(zipFile);
			IOUtils.copy(inputStream, fos);
			return zipFile;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new IllegalStateException(e.getMessage(), e);
		}
	}



	private void flushSentenceTokens(List<SentenceToken> tokens2) {
		builder.startSentence();
		for (SentenceToken token : tokens2) {
			// знаки пунктуации/UNKN/неизвестные слока пропускаем
			if (token.grammemes.size() == 1)
				continue;
			Grammemes grammemes = new Grammemes();
			GrammemeUtils.fillGrammemesOpencorporaTags(token.grammemes, grammemes);
			GrammemeEnum pos = grammemes.getPOS();
			if (pos != null)
				builder.addTokent(token.word, pos);

		}
		builder.endSentence();
	}

	private void gotoCloseTag(XMLStreamReader parser, String tag) throws XMLStreamException {
		while (true) {
			if (parser.getEventType() == XMLStreamConstants.END_ELEMENT
					&& parser.getLocalName().equals(tag))
				break;
			parser.next();
		}
	}

	private boolean isStartTag(XMLStreamReader parser, String tag) throws XMLStreamException {
		return parser.getEventType() == XMLStreamConstants.START_ELEMENT
				&& parser.getLocalName().equals(tag);
	}

	private boolean proccessGrammeme(XMLStreamReader parser, SentenceToken token)
			throws XMLStreamException {
		if (!isStartTag(parser, "g"))
			return false;
		token.addGrammeme(parser.getAttributeValue(null, "v"));
		gotoCloseTag(parser, "g");
		return true;
	}

	private boolean processLemms(XMLStreamReader parser, SentenceToken token)
			throws XMLStreamException {
		if (!isStartTag(parser, "l"))
			return false;
		token.lemm = parser.getAttributeValue(null, "t");
		parser.nextTag();
		while (proccessGrammeme(parser, token))
			parser.nextTag();
		gotoCloseTag(parser, "l");
		return true;
	}


	private boolean processSentenceToken(XMLStreamReader parser, List<SentenceToken> tokens)
			throws XMLStreamException {
		if (!isStartTag(parser, "token"))
			return false;

		String text = parser.getAttributeValue(null, "text");
		SentenceToken token = new SentenceToken(text);



		parser.nextTag(); // tfr
		parser.nextTag(); // v
		parser.nextTag(); // l

		processLemms(parser, token);
		parser.nextTag();
		tokens.add(token);

		gotoCloseTag(parser, "token");
		return true;
	}

	boolean processText(XMLStreamReader parser) throws XMLStreamException {
		if (!isStartTag(parser, "text"))
			return false;
		// идём до "tokens"
		while (true) {

			while (true) {
				if (parser.getEventType() == XMLStreamConstants.START_ELEMENT
						&& parser.getLocalName().equals("tokens"))
					break;
				if (parser.getEventType() == XMLStreamConstants.END_ELEMENT
						&& parser.getLocalName().equals("text"))
					return true;
				parser.next();
			}
			parser.nextTag();
			// обрабатываем "tokens"
			tokens = new ArrayList<>();
			while (processSentenceToken(parser, tokens)) {
				parser.nextTag();
			}
			flushSentenceTokens(tokens);
		}
	}

	void readMarkupFile(InputStream is) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader parser = factory.createXMLStreamReader(is);

		while (true) {
			if (parser.getEventType() == XMLStreamConstants.START_ELEMENT
					&& parser.getLocalName().equals("annotation"))
				break;
			parser.next();
		}
		parser.nextTag();

		while (processText(parser)) {
			parser.nextTag();
		}

	}

	/**
	 * Прочитать запакованую морфологическую БД.
	 * 
	 * @param inputStream
	 */
	void readZippedMarkupFile(File filemarkup) {
		try {
			File uncompressZip = uncompressZip(filemarkup, "annot.opcorpora.no_ambig.xml");
			FileInputStream fis = new FileInputStream(uncompressZip);
			builder.startLearning();
			readMarkupFile(fis);
			builder.endLearning();
			IOUtils.closeQuietly(fis);
		} catch (Exception ex) {
			throw new IllegalStateException(ex.getMessage(), ex);
		}
	}

	File uncompressZip(File filemarkup, String entry) throws IOException {
		ZipFile zipFile = new ZipFile(filemarkup);
		InputStream zipIS = zipFile.getInputStream(zipFile.getEntry(entry));

		File xmlFile = File.createTempFile("markup-file", ".xml");
		FileOutputStream fos = new FileOutputStream(xmlFile);
		IOUtils.copy(zipIS, fos);
		IOUtils.closeQuietly(fos);
		zipFile.close();
		xmlFile.deleteOnExit();
		return xmlFile;
	}

}

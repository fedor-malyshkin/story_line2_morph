package ru.nlp_project.story_line2.morph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Класс чтения БД (текстового формата) с данными для морфологического анализа.
 * 
 * @author fedor
 * 
 * 
 */
class MorphDBReader {

	private JsonFactory jsonFactory;
	private JsonParser jp;
	private TrieBuilder trieBuilder;
	private InputStream zipDBIS;
	private LexemeManager lxManager;
	private boolean initLookup;

	public static MorphDBReader newInstance(TrieBuilder trieBuilder, LexemeManager lxManager,
			InputStream zipDBIS, boolean initLookup) throws IOException {
		MorphDBReader result = new MorphDBReader(trieBuilder, lxManager, zipDBIS);
		result.initLookup = initLookup;
		result.initialize();
		return result;
	}

	private MorphDBReader(TrieBuilder trieBuilder, LexemeManager lxManager, InputStream zipDBIS) {
		this.trieBuilder = trieBuilder;
		this.lxManager = lxManager;
		this.zipDBIS = zipDBIS;

	}

	@Deprecated
	public void setTrieBuilder(TrieBuilder trieBuilder) {
		this.trieBuilder = trieBuilder;
	}

	private void initialize() throws IOException {
		jsonFactory = new JsonFactory(); // or, for data binding,
	}

	@SuppressWarnings("unused")
	public void readCompressedMorphDB() {
		try {
			InputStream is = null;
			File uncompressZip = uncompressZip();
			is = new FileInputStream(uncompressZip);
			if (is == null)
				throw new IllegalStateException("");
			jp = jsonFactory.createParser(is);
			readDB();
			jp.close();
			IOUtils.closeQuietly(is);
		} catch (Exception ex) {
			throw new IllegalStateException(ex.getMessage(), ex);
		}
	}

	protected void readClasspathUncompressedMorphDB(String dictResourceName) {
		try {
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(dictResourceName);
			if (is == null)
				throw new IllegalStateException("Wrong classpath for morphDB:" + dictResourceName);
			jp = jsonFactory.createParser(is);
			readDB();
			jp.close();
			IOUtils.closeQuietly(is);
		} catch (Exception ex) {
			throw new IllegalStateException(ex.getMessage(), ex);
		}
	}

	void readDB() throws IOException {
		readBases();
		readEndingModels();
	}

	void readEndingModels() throws IOException {
		while (jp.nextToken() != JsonToken.START_ARRAY);
		jp.nextToken();
		while (jp.getCurrentToken() != JsonToken.END_ARRAY) {
			// jg.writeString(lemma);
			jp.nextValue();
			Number numberValue = jp.getNumberValue();
			int emn = numberValue.intValue();
			EndingModel em = new EndingModel();
			em.modelNum = emn;

			// field("es"),
			while (jp.nextToken() != JsonToken.START_ARRAY);
			jp.nextToken();
			while (jp.getCurrentToken() != JsonToken.END_ARRAY) {
				// e
				jp.nextValue();
				String e = jp.getText();

				// mg
				jp.nextValue();
				String ag = jp.getText();

				Grammemes grammemas = new Grammemes();
				GrammemeUtils.fillGrammemesByCSVMyTags(ag, grammemas);
				em.addEnding(e, grammemas);

				while (jp.nextToken() != JsonToken.END_OBJECT);
				jp.nextToken();

			}
			trieBuilder.addEndingModel(emn, em);
			if (initLookup)
				lxManager.addEndingModel(emn, em);
			while (jp.nextToken() != JsonToken.END_OBJECT);
			jp.nextToken();

		}
	}

	void readBases() throws IOException {
		while (jp.nextToken() != JsonToken.START_ARRAY);
		jp.nextToken();
		while (jp.getCurrentToken() != JsonToken.END_ARRAY) {
			// jg.writeString(lemma);
			jp.nextValue();
			String lemma = jp.getText();
			// jg.writeString(opencorporaId);
			jp.nextValue();
			String opencorporaId = jp.getText();

			// jg.writeString(base);
			jp.nextValue();
			String base = jp.getText();

			// jg.writeNumber(emn);
			jp.nextValue();
			Number numberValue = jp.getNumberValue();
			int emn = numberValue.intValue();
			trieBuilder.addWordformBase(opencorporaId, lemma, base, emn);
			if (initLookup)
				lxManager.addWordformBase(opencorporaId, lemma, base, emn);
			while (jp.nextToken() != JsonToken.END_OBJECT);
			jp.nextToken();
		}

	}

	File uncompressZip() throws IOException {
		File zipFileCmp = File.createTempFile("morph-dict", ".zip");
		FileOutputStream fos = new FileOutputStream(zipFileCmp);
		zipFileCmp.deleteOnExit();

		IOUtils.copy(zipDBIS, fos);
		IOUtils.closeQuietly(fos);

		ZipFile zipFile = new ZipFile(zipFileCmp);
		InputStream zipIS = zipFile.getInputStream(zipFile.getEntry("out.json"));

		File xmlFile = File.createTempFile("morph-dict", ".json");
		fos = new FileOutputStream(xmlFile);
		IOUtils.copy(zipIS, fos);
		IOUtils.closeQuietly(fos);
		zipFile.close();

		xmlFile.deleteOnExit();

		return xmlFile;
	}

}

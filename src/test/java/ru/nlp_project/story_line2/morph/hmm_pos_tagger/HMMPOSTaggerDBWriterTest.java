package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import ru.nlp_project.story_line2.morph.GrammemeEnum;


public class HMMPOSTaggerDBWriterTest {

	private HMMPOSTaggerDBBuilderImpl dbBuilder;
	private OpencorporaMarkupReader markupReader;

	@Before
	public void setUp() {
		dbBuilder = new HMMPOSTaggerDBBuilderImpl();
		markupReader = new OpencorporaMarkupReader(dbBuilder);

	}

	@Test
	public void testReadWrite() throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
				"ru/nlp_project/story_line2/morph/hmm_pos_tagger/annot.opcorpora.no_ambig.xml.zip");
		File tempFile = File.createTempFile("test", "zip");
		FileOutputStream fos = new FileOutputStream(tempFile);
		IOUtils.copy(stream, fos);
		markupReader.readZippedMarkupFile(tempFile);
		HMMPOSTaggerDB taggerDB = dbBuilder.getHMMPOSTaggerDB();
		// write
		HMMPOSTaggerDBWriter testable = new HMMPOSTaggerDBWriter(taggerDB);
		tempFile = File.createTempFile("test-tagger-db", ".json");
		testable.write(tempFile);
		// file grather than 1M
		assertThat(tempFile.length()).isGreaterThan(1_000_000);
		// read
		HMMPOSTaggerDBReader testable2 = new HMMPOSTaggerDBReader();
		FileInputStream fis = new FileInputStream(tempFile);
		HMMPOSTaggerDB db = testable2.read(fis);
		IOUtils.closeQuietly(fis);
		// propability more than 0.01
		assertThat(db.getBiGrammPropability(GrammemeEnum.noun, GrammemeEnum.verb))
				.isGreaterThan(0.01f);
	}

}

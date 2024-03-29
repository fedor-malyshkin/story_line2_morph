package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import ru.nlp_project.story_line2.morph.GrammemeEnum;

public class OpencorporaMarkupReaderTest {

	private OpencorporaMarkupReader testable;
	private IHMMPOSTaggerDBBuilder dbBuilder;


	@Before
	public void setUp() {
		dbBuilder = mock(IHMMPOSTaggerDBBuilder.class);
		testable = new OpencorporaMarkupReader(dbBuilder);
	}

	@Test
	public void testReadZippedMarkupFile() throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
				"ru/nlp_project/story_line2/morph/hmm_pos_tagger/annot.opcorpora.no_ambig.xml.zip");
		File tempFile = File.createTempFile("test", "zip");
		FileOutputStream fos = new FileOutputStream(tempFile);
		IOUtils.copy(stream, fos);
		testable.readZippedMarkupFile(tempFile);

		verify(dbBuilder, times(1)).startLearning();
		verify(dbBuilder, times(1)).endLearning();
		
		verify(dbBuilder, atLeast(100)).startSentence();
		verify(dbBuilder, atLeast(100)).endSentence();
		
		verify(dbBuilder, atLeast(100)).addTokent(anyString(), any(GrammemeEnum.class));
		
	}

}

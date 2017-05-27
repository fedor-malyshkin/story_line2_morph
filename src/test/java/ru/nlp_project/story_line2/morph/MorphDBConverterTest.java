package ru.nlp_project.story_line2.morph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import ru.nlp_project.story_line2.morph.MorphDBConverter.Paradigm;

public class MorphDBConverterTest {

	private MorphDBConverter testable;

	@BeforeClass
	static public void setUpClass() throws Exception {
		LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
	}

	@Before
	public void setUp() {
		testable = new MorphDBConverter();
		testable.initialize();
	}

	@Test
	public void testCacheInitialization() {
		assertNotNull(testable);
	}

	@Test
	public void testCalculateLongestCommonPrefix() {
		Paradigm lemma = testable.createParadigm();
		lemma.wordforms.add("someText");
		lemma.wordforms.add("someTextus");
		lemma.wordforms.add("some");
		assertEquals(4, testable.calculateLongestCommonPrefix(lemma));
	}

	@Test
	public void testCalculateLongestCommonPrefix_NoCommonParts() {
		Paradigm lemma = testable.createParadigm();
		lemma.wordforms.add("sameText");
		lemma.wordforms.add("someTextus");
		assertEquals(1, testable.calculateLongestCommonPrefix(lemma));
	}

	@Test
	public void testReadZippedMorphDB() throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("ru/nlp_project/story_line2/morph/test2.zip");
		testable = spy(testable);
		testable.readZippedMorphDB(stream);
		ArgumentCaptor<Paradigm> argument = ArgumentCaptor.forClass(Paradigm.class);
		verify(testable, atLeast(1)).addParadigm(argument.capture());
		assertEquals(
				"[Paradigm [lemma=ёж, grammemas=[[sing, nomn, NOUN, anim, masc], [sing, gent, NOUN, anim, masc], "
						+ "[sing, datv, NOUN, anim, masc], " + "[sing, accs, NOUN, anim, masc], "
						+ "[sing, ablt, NOUN, anim, masc], " + "[sing, loct, NOUN, anim, masc], "
						+ "[plur, nomn, NOUN, anim, masc], " + "[plur, gent, NOUN, anim, masc], "
						+ "[plur, datv, NOUN, anim, masc], " + "[plur, accs, NOUN, anim, masc], "
						+ "[plur, ablt, NOUN, anim, masc], "
						+ "[plur, loct, NOUN, anim, masc]], wordforms=[ёж, ежа, ежу, ежа, ежом, еже, ежи, ежей, ежам, ежей, ежами, ежах]], "
						+ "Paradigm [lemma=ёж, grammemas=[[sing, nomn, NOUN, inan, masc], "
						+ "[sing, gent, NOUN, inan, masc], " + "[sing, datv, NOUN, inan, masc], "
						+ "[sing, accs, NOUN, inan, masc], " + "[sing, ablt, NOUN, inan, masc], "
						+ "[sing, loct, NOUN, inan, masc], " + "[plur, nomn, NOUN, inan, masc], "
						+ "[plur, gent, NOUN, inan, masc], " + "[plur, datv, NOUN, inan, masc], "
						+ "[plur, accs, NOUN, inan, masc], " + "[plur, ablt, NOUN, inan, masc], "
						+ "[plur, loct, NOUN, inan, masc]], wordforms=[ёж, ежа, ежу, ёж, ежом, еже, ежи, ежей, ежам, ежи, ежами, ежах]], "
						+ "Paradigm [lemma=дк, grammemas=[[sing, nomn, NOUN, inan, masc, Fixd, Abbr], "
						+ "[sing, gent, NOUN, inan, masc, Fixd, Abbr], "
						+ "[sing, datv, NOUN, inan, masc, Fixd, Abbr], "
						+ "[sing, accs, NOUN, inan, masc, Fixd, Abbr], "
						+ "[sing, ablt, NOUN, inan, masc, Fixd, Abbr], "
						+ "[sing, loct, NOUN, inan, masc, Fixd, Abbr], "
						+ "[plur, nomn, NOUN, inan, masc, Fixd, Abbr], "
						+ "[plur, gent, NOUN, inan, masc, Fixd, Abbr], "
						+ "[plur, datv, NOUN, inan, masc, Fixd, Abbr], "
						+ "[plur, accs, NOUN, inan, masc, Fixd, Abbr], "
						+ "[plur, ablt, NOUN, inan, masc, Fixd, Abbr], "
						+ "[plur, loct, NOUN, inan, masc, Fixd, Abbr]], wordforms=[дк, дк, дк, дк, дк, дк, дк, дк, дк, дк, дк, дк]], "
						+ "Paradigm [lemma=тока, grammemas=[[PRCL, Infr]], wordforms=[тока]], "
						+ "Paradigm [lemma=нет, grammemas=[[PRED, pres]], wordforms=[нет]]]",
				argument.getAllValues().toString());
	}

	@Test
	public void testAddLemmaForms() throws IOException {
		Paradigm paradigm = testable.createParadigm();
		paradigm.wordforms.add("someText");
		paradigm.wordforms.add("someTextus");
		paradigm.wordforms.add("some");
		paradigm.lemma = "some F Text";
		paradigm.opencorporaId = "oId";

		testable = spy(testable);

		doNothing().when(testable).jsonWriteP(anyString(), anyString(), anyString(), anyInt());
		testable.addWordforms(2, 1, paradigm);
		verify(testable, times(1)).jsonWriteP(eq("some F Text"), eq("oId"), eq("so"), eq(1));
	}

	@Test
	public void testUncompressZip() throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("ru/nlp_project/story_line2/morph/test.zip");
		File file = testable.uncompressZip(stream, "test.json");
		assertTrue(file.exists());
		assertEquals(2099, file.length());
	}

}

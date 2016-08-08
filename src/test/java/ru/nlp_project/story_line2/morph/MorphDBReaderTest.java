package ru.nlp_project.story_line2.morph;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

// Let's import Mockito statically so that the code looks clearer

public class MorphDBReaderTest {

  private MorphDBReader testable;
  private LexemeManager lxManager;

  @Before
  public void setUp() throws Exception {
    lxManager = LexemeManager.newInstance();
    testable = MorphDBReader.newInstance(null, lxManager,
        new FileInputStream("data/dict.story_line2.zip"), true);
  }

  @Test
  // @Ignore
  @SuppressWarnings("deprecation")
  public void testReadDB_Real() throws IOException {
    TrieBuilder trieBuilder = mock(TrieBuilder.class);
    testable.setTrieBuilder(trieBuilder);
    testable.readCompressedMorphDB();

    WordformInfo wordformIndoById = lxManager.getWordformInfoById("37265");
    assertEquals("WordformInfo [base=, lemm=есть, endingModelNumber=655]",
        wordformIndoById.toString());

  }

  @Test
  @SuppressWarnings("deprecation")
  public void testReadDB() throws IOException {
    TrieBuilder trieBuilder = mock(TrieBuilder.class);
    testable.setTrieBuilder(trieBuilder);
    testable.readClasspathUncompressedMorphDB(
        "ru/nlp_project/story_line2/morph/dict.story_line2.json");
    verify(trieBuilder, times(4)).addWordformBase(anyString(), anyString(),
        anyString(), anyInt());
    verify(trieBuilder, times(3)).addEndingModel(anyInt(),
        any(EndingModel.class));

  }

}

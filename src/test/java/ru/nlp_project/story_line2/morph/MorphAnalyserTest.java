package ru.nlp_project.story_line2.morph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class MorphAnalyserTest {

  private static MorphAnalyser testable;

  @BeforeClass
  static public void setUpClass() throws Exception {
    InputStream inputStream = new FileInputStream("data/dict.story_line2.zip");
    testable = MorphAnalyser.newInstance(inputStream, false);
  }

  @Test
  public void testAnalyse_Premier() {
    List<WordformAnalysisResult> analyse = testable.analyse("премьер");
    assertNotNull(analyse);
    assertEquals(2, analyse.size());
    assertEquals("премьер-noun [noun, nomn, masc, anim, sing]",
        String.format("%s-%s", analyse.get(0).lemm,
            analyse.get(0).grammemes.toString()));
    assertEquals("премьера-noun [noun, gent, femn, inan, plur]",
        String.format("%s-%s", analyse.get(1).lemm,
            analyse.get(1).grammemes.toString()));
  }

  @Test
  public void testAnalyse() {
    List<WordformAnalysisResult> analyse = testable.analyse("тока");
    assertNotNull(analyse);
    assertEquals(4, analyse.size());
    assertEquals("тока", analyse.get(0).lemm);
    assertEquals("ток", analyse.get(1).lemm);
    assertEquals("prcl [prcl, infr]", analyse.get(0).grammemes.toString());
    assertEquals("noun [noun, gen1, masc, inan, sing]",
        analyse.get(1).grammemes.toString());
    assertTrue(analyse.get(0).exactMatch);
  }

  @Test
  public void testAnalyseEz() {
    List<WordformAnalysisResult> analyse = testable.analyse("ежи");
    assertNotNull(analyse);

    assertEquals(9, analyse.size());
    assertEquals("ежи", analyse.get(0).lemm);
    assertEquals("ежи", analyse.get(1).lemm);
    assertEquals("ежи", analyse.get(2).lemm);
    assertEquals("noun [noun, nomn, masc, anim, sing, fixd, sgtm, name]",
        analyse.get(0).grammemes.toString());
    assertTrue(analyse.get(0).exactMatch);
  }

  @Test
  public void testAnalyseEz_Hypothesis() {
    List<WordformAnalysisResult> analyse = testable.analyse("морж");
    assertNotNull(analyse);
    assertEquals(1, analyse.size());
  }

  @Test
  public void testPredictSurame() {
    List<SurnameAnalysisResult> analyse = testable.predictAsSurname("балуевский");
    assertNotNull(analyse);
  }

}

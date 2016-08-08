package ru.nlp_project.story_line2.morph;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class GrammemeUtilsTest {

  @Test
  public void testFillGrammemesOpencorporaTags() {
    Grammemes gr = new Grammemes();
    List<String> list = Arrays.asList("NOUN", "gent", "sing");
    GrammemeUtils.fillGrammemesOpencorporaTags(list, gr);
    assertEquals("{0=noun, 1=gent, 5=sing}", gr.mainGrammems.toString());
    assertEquals("noun [noun, gent, sing]", gr.toString());
  }

  @Test
  public void testFillGrammemesByCSVMyTags() {
    Grammemes gr = new Grammemes();
    GrammemeUtils.fillGrammemesByCSVMyTags("advb,plur", gr, true);
    assertEquals("{0=advb, 5=plur}", gr.mainGrammems.toString());
    assertEquals("advb [advb, plur]", gr.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFillGrammemesByCSVMyTags_TwiceSameProperty() {
    Grammemes gr = new Grammemes();
    GrammemeUtils.fillGrammemesByCSVMyTags("advb,plur,sing", gr, true);
  }

}

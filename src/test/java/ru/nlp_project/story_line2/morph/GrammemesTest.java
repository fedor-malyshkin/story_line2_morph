package ru.nlp_project.story_line2.morph;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GrammemesTest {
/*
  @Test
  public void testMatchSetOfGrammemeEnum() {
    Grammemes testable = new Grammemes();
    // существующие и совпадающие признаки
    GrammemeUtils.fillGrammemesByCSVMyTags("noun,nomn,sing", testable);
    Set<GrammemeEnum> guSet = GrammemeUtils
        .createGrammemesSetByCSVMyTags("gent,accs,nomn,sing");
    assertTrue(testable.match(guSet));
    // несовпадающие признаки
    guSet = GrammemeUtils.createGrammemesSetByCSVMyTags("adj,verb,nomn,sing");
    assertFalse(testable.match(guSet));

    // несуществующие признаки
    guSet = GrammemeUtils.createGrammemesSetByCSVMyTags("past,futr");
    assertFalse(testable.match(guSet));
  }
*/
	

	  @Test
	  public void testGrammemes() {
	    Grammemes gr = new Grammemes();
	    GrammemeUtils.fillGrammemesByCSVMyTags("noun, gent, sing", gr);
	    assertEquals("noun", gr.getByGrammemeGroupIndex(GrammemeUtils.POS_NDX).toString());
	    assertEquals("gent", gr.getByGrammemeGroupIndex(GrammemeUtils.CASE_NDX).toString());
	  }
}

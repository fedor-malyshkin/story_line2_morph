package ru.nlp_project.story_line2.morph;

import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;

import ru.nlp_project.story_line2.morph.GrammemeUtils.GrammemeEnum;

public class GrammemesTest {

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

}

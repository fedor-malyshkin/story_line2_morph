package ru.nlp_project.story_line2.morph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import ru.nlp_project.story_line2.morph.GrammemeUtils.GrammemeEnum;

public class TrieBuilderTest {

  private TrieBuilder testable;

  @Before
  public void setUp() throws Exception {
    testable = new TrieBuilder();
  }

  private Grammemes createGrammemes(GrammemeEnum grammeme) {
    Grammemes grammemes = new Grammemes();
    grammemes.grammSet.add(grammeme);
    return grammemes;
  }

  @Test
  public void testAddEndingModel() {
    EndingModel endingModel = new EndingModel();
    endingModel.addEnding("ать", null);
    // 2nd
    endingModel.addEnding("уть", null);
    // 3rd
    endingModel.addEnding("тут", null);
    // 4th (несколько одинаковых окончаний для одной модели)
    endingModel.addEnding("муть", createGrammemes(GrammemeEnum.noun));
    endingModel.addEnding("путь", null);

    testable.addEndingModel(0, endingModel);

    Map<Integer, TrieNodeEnding> ending_models =
        testable.getEndingModelRootNodes();
    assertNotNull(ending_models);

    TrieNodeEnding m = (TrieNodeEnding) ending_models.get(0).l.m;

    assertEquals("ать", m.endingModel.endings.get(0).value);
    assertEquals("уть", m.endingModel.endings.get(1).value);
    assertEquals("тут", m.endingModel.endings.get(2).value);
    assertEquals("муть", m.endingModel.endings.get(3).value);
    assertEquals("noun",
        m.endingModel.endings.get(3).grammemes.toString());
  }

  @Test
  public void testAddGrammatemModelTree() {
    EndingModel endingModel = new EndingModel();
    endingModel.addEnding("ать", createGrammemes(GrammemeEnum.nomn));
    // 2nd
    endingModel.addEnding("", createGrammemes(GrammemeEnum.nomn));
    // 3rd
    endingModel.addEnding("", createGrammemes(GrammemeEnum.nomn));
    // 4th (не настоящий - слово "спать" не заканчивается на "ют")
    endingModel.addEnding("ют", createGrammemes(GrammemeEnum.nomn));
    // 4th (настоящий)
    endingModel.addEnding("ь", createGrammemes(GrammemeEnum.nomn));

    testable.addEndingModel(0, endingModel);

    TrieReader trieExplorer = new TrieReader(testable.getTrie());

    class ProcessingResultDebug extends TrieReader.ProcessingResult {
    }
    ProcessingResultDebug pr = new ProcessingResultDebug();

    trieExplorer.analyseEndings("спать", pr);

    assertEquals(3, pr.foundEndingsCount);
    assertEquals(0, pr.endingLengths[0]);
    assertEquals(1, pr.endingLengths[1]);
    assertEquals(3, pr.endingLengths[2]);
  }

  /*
   * Проверка построения дерева при добавлении одного слова.
   */
  @Test
  public void testAddWordformBase() {
    testable.addWordformBase("id", "слово", "слов", 1);
    List<TrieNodeBase> root_nodes = testable.getTrie().perBaseRootTrieNodeBase;
    assertNotNull(root_nodes);
    assertNotNull(root_nodes.get('в'));
    TrieNodeBase root_node = root_nodes.get('в');
    assertEquals('о', root_node.kov);
    assertEquals("слов", root_node.m.key);
    assertEquals(1, root_node.m.wordformInfos.size());
    assertFalse(root_node.m.internal());
  }

  @Test
  public void testAddWordformWithTheSameBaseWord() {
    testable.addWordformBase("id", "слово", "слов", 1);
    testable.addWordformBase("id", "слово", "слов", 1);
    List<TrieNodeBase> root_nodes = testable.getTrie().perBaseRootTrieNodeBase;
    assertNotNull(root_nodes);
    TrieNodeBase root_node = root_nodes.get('в');
    assertEquals("слов", root_node.m.key);
    assertEquals(2, root_node.m.wordformInfos.size());
    assertEquals("слово", root_node.m.wordformInfos.get(0).lemm);
  }

  /*
   * Проверка построения дерева при добавлении двух слов с общими окончаниями.
   */
  @Test
  public void test_add_wordform_base_w_split() {
    testable.addWordformBase("id", "слово", "слов", 1);
    testable.addWordformBase("id", "богослов", "богослов", 1);
    List<TrieNodeBase> root_nodes = testable.getTrie().perBaseRootTrieNodeBase;
    assertNotNull(root_nodes);
    TrieNodeBase root_node = root_nodes.get('в');
    assertEquals("богослов", root_node.m.m.m.r.m.key);
  }

  /*
   * Проверка построения дерева при добавлении двух слов с общими окончаниями,
   * но в обратном порядке - сначала длинное слово, потом короткое.
   */
  @Test
  public void test_add_wordform_base_w_split_long_word_first() {
    testable.addWordformBase("id", "богослов", "богослов", 1);
    testable.addWordformBase("id", "слово", "слов", 1);
    testable.addWordformBase("id", "бугислов", "бугислов", 1);
    List<TrieNodeBase> root_nodes = testable.getTrie().perBaseRootTrieNodeBase;
    assertNotNull(root_nodes);
    TrieNodeBase root_node = root_nodes.get('в');
    assertEquals("слов", root_node.m.m.m.l.m.key);
    assertEquals("богослов", root_node.m.m.m.m.key);
    assertEquals("бугислов", root_node.m.m.m.l.r.m.key);
  }

}

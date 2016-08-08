package ru.nlp_project.story_line2.morph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ru.nlp_project.story_line2.morph.GrammemeUtils.GrammemeEnum;

public class TrieReaderTest {

  private TrieBuilder builder;

  private Grammemes createGrammemes(GrammemeEnum grammeme) {
    Grammemes grammemes = new Grammemes();
    grammemes.mainGrammems.put(GrammemeUtils.POS_NDX, grammeme);
    grammemes.allGrammems.add(grammeme);
    return grammemes;
  }

  @Before
  public void setUp() throws Exception {
    builder = new TrieBuilder();
  }

  @Test
  public void test_analyze_hypothesis() {
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

    builder.addEndingModel(0, endingModel);

    builder.addWordformBase("id", "делать", "дел", 0);
    builder.addWordformBase("id", "богослов", "богослов", 0);
    builder.addWordformBase("id", "слово", "слов", 0);
    builder.addWordformBase("id", "бугислов", "бугислов", 0);

    TrieReader trieExplorer = new TrieReader(builder.getTrie());
    List<WordformAnalysisResult> results = trieExplorer.analyse("переделать");

    assertEquals(1, results.size());
    assertEquals("делать", results.get(0).lemm);
    assertEquals("nomn [nomn]", results.get(0).grammemes.toString());
  }

  @Test
  public void test_analyze_hypothesis_no_results() {
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

    builder.addEndingModel(0, endingModel);

    builder.addWordformBase("id", "дел", "делать", 1);
    builder.addWordformBase("id", "богослов", "богослов", 1);
    builder.addWordformBase("id", "слов", "слово", 1);
    builder.addWordformBase("id", "бугислов", "бугислов", 1);

    TrieReader trieExplorer = new TrieReader(builder.getTrie());
    List<WordformAnalysisResult> results = trieExplorer.analyse("переделать");
    assertEquals(0, results.size());
  }

  @Test
  public void test_search_complete_match() {
    WordformInfo wf_info = new WordformInfo("id", "1", "", 0);
    TrieNodeBase null_node = new TrieNodeBase("слов", wf_info, '\0');
    null_node.internal = false;
    TrieNodeBase first_node = new TrieNodeBase(null_node, 'о');
    List<TrieNodeBase> root_nodes = new ArrayList<TrieNodeBase>(65536);
    for (int i = 0; i < 65536; i++)
      root_nodes.add(null);
    root_nodes.set('в', first_node);

    Trie trie = new Trie();
    TrieReader trieExplorer = new TrieReader(trie);
    trie.perBaseRootTrieNodeBase = root_nodes;

    TrieNodeBase result = trieExplorer.searchTrieNodeBase("слов");
    assertNotNull(result);
    assertEquals("слов", result.key);
  }

  /*
   * Проверка поиска в дереве при добавлении двух слов с общими окончаниями, но
   * в обратном порядке - сначала длинное слово, потом короткое.
   */
  @Test
  public void test_search_complete_match_long_word_first() {
    // начинаем построение
    builder.addWordformBase("id", "богослов", "богослов", 1);
    builder.addWordformBase("id", "слово", "слов", 1);
    TrieReader trieReader = new TrieReader(builder.getTrie());

    TrieNodeBase result = trieReader.searchTrieNodeBase("слов");
    assertNotNull(result);
    assertEquals("слов", result.key);
    result = trieReader.searchTrieNodeBase("богослов");
    assertNotNull(result);
    assertEquals("богослов", result.key);
  }

  @Test
  public void test_search_partial_match() {
    WordformInfo wf_info = new WordformInfo("id", "1", "", 0);
    TrieNodeBase null_node = new TrieNodeBase("богослов", wf_info, '\0');
    null_node.internal = false;
    null_node.internal = false;
    TrieNodeBase first_node = new TrieNodeBase(null_node, 'о');
    List<TrieNodeBase> root_nodes = new ArrayList<TrieNodeBase>(65536);
    for (int i = 0; i < 65536; i++)
      root_nodes.add(null);
    root_nodes.set('в', first_node);

    Trie trie = new Trie();
    TrieReader trieExplorer = new TrieReader(trie);
    trie.perBaseRootTrieNodeBase = root_nodes;

    TrieNodeBase result = trieExplorer.searchTrieNodeBase("богослов");
    assertNotNull(result);
    assertEquals("богослов", result.key);
  }

}

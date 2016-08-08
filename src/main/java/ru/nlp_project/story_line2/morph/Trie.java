package ru.nlp_project.story_line2.morph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Trie {
  // Map<Integer, EndingModel> endingModelMap;

  /**
   * Все имеющиеся окончания во всех моделях.
   */
  List<TrieNodeEnding> commonRootTrieNodeEnding;

  /**
   * Деревья окончаний для каждой модели грамматем 
   * (конкретный узел берётся по индексу массива, совпадающим с номером модели).
   */
  Map<Integer, TrieNodeEnding> perEndingModelRootTrieNodeEnding;

  /**
   * Массив элементов, позволяющий по последнему имени окончания 
   * определить - имеет ли словоформа однобуквенное окончание.
   */
  boolean[] hasOneLetterEndings;

  /**
   * Деревья основ для для каждой последней буквы основы.
   */
  List<TrieNodeBase> perBaseRootTrieNodeBase;

  public Trie() {
    // endingModelMap = new HashMap<Integer, EndingModel>();
    
    hasOneLetterEndings = new boolean[65536];

    perBaseRootTrieNodeBase = new ArrayList<TrieNodeBase>(65536);
    for (int i = 0; i < 65536; i++)
      perBaseRootTrieNodeBase.add(null);

    commonRootTrieNodeEnding = new ArrayList<TrieNodeEnding>(65536);
    for (int i = 0; i < 65536; i++)
      commonRootTrieNodeEnding.add(null);

    perEndingModelRootTrieNodeEnding = new HashMap<>();

    for (int i = 0; i < 65536; i++)
      hasOneLetterEndings[i] = false;
  }

}

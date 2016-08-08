package ru.nlp_project.story_line2.morph;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * Класс для чтения trie с данными морфологии.
 * @author fedor
 *
 */
public class TrieReader {

  /**
   * 
   * Объект с с данными результатов анализа (включая промежуточные).
   * @author fedor
   *
   */
  static class ProcessingResult {
    int foundEndingsCount = 0;
    int[] endingLengths = new int[100];

    public ProcessingResult() {
      for (int i = 0; i < 100; i++)
        endingLengths[i] = 0;
    }

  }

  private Trie trie;

  public TrieReader(Trie trie) {
    this.trie = trie;
  }

  /**
   * Произвести анализ окончаний слов - определить под какие окончания оно попадает.
   * Результаты будут записаны в массив {@link #endingLengths_} и {@link #foundEndingsCount_}. 
   * 
   * @param wordform
   * @param pr 
   */
  void analyseEndings(String wordform, ProcessingResult pr) {

    String rwordform = reverseString(wordform);
    TrieNodeEnding root_node = trie.commonRootTrieNodeEnding
        .get(rwordform.length() > 0 ? rwordform.charAt(0) : '\0');
    // добавляем пустое окончание
    pr.endingLengths[pr.foundEndingsCount++] = 0;
    if (true == trie.hasOneLetterEndings[rwordform.length() > 0
        ? rwordform.charAt(0) : '\0']) {
      pr.endingLengths[pr.foundEndingsCount++] = 1;
    }
    int rwordform_len = rwordform.length();
    for (int i = 2; i <= rwordform_len; i++) {
      String rending_search = rwordform.substring(0, i);
      TrieNodeEnding res = searchTrieNodeEnding(root_node, rending_search, 1);
      // если найденное окончание неравно NULL - добавляем в список
      // окончаний
      if (res != null)
        pr.endingLengths[pr.foundEndingsCount++] = i;
    }
  }

  // выполнить поиск в дереве окончаний
  TrieNodeEnding searchTrieNodeEnding(TrieNodeEnding h, String rkey,
      int key_order) {
    // если дошли до недостроенного дерева...
    if (null == h) {
      // printf("[search_ending_node] NULL == h\n");
      return null;
    }

    // если находимся во внешнем узле...
    if (h.internal) {
      char search_kov =
          rkey.length() > key_order ? rkey.charAt(key_order) : '\0';
      char existing_kov = h.kov;
      // printf("[search_ending_node] search_kov: '%c'(%u); existing_kov:
      // '%c'(%u); key_order: '%d'\n",
      // search_kov, static_cast<unsigned char>(search_kov), existing_kov,
      // static_cast<unsigned char>(existing_kov), key_order);
      if (search_kov < existing_kov) {
        // printf("[search_ending_node] search_kov < existing_kov\n");
        return searchTrieNodeEnding(h.l, rkey, key_order);
      }
      if (search_kov == existing_kov) {
        // printf("[search_ending_node] search_kov == existing_kov\n");
        return searchTrieNodeEnding(h.m, rkey, key_order + 1);
      }
      if (search_kov > existing_kov) {
        // printf("[search_ending_node] search_kov > existing_kov\n");
        return searchTrieNodeEnding(h.r, rkey, key_order);
      }
    }

    // сравниваем
    // printf("[search_ending_node] pre strcmp(h->rkey_, rkey) == 0\n");
    if (h.rkey.equals(rkey)) {
      // printf("[search_ending_node] strcmp(h->rkey_, rkey) == 0 (%s == %s)\n",
      // h->rkey_, rkey);
      return h;
    }

    // во всех остальных случаях - ничего нет
    // printf("[search_ending_node] return NULL\n");
    return null;
  }

  String reverseString(String input) {
    return (new StringBuffer(input)).reverse().toString();
  }

  TrieNodeBase searchTrieNodeBase(String key) {
    return searchTrieNodeBase(key, false);
  }

  /**
   * Начало прохода по структуре.
   * 
   * @param key
   * @param returnExternalNodeWithUnmatchedKey возвращеть ли несоответствующие 
   * части (когда в 
   * результатае прохода приходим во внешний узел в котором ключевое значение не
   * совпадает с искомым (даже частично). Используется в предсказании фамилий.)
   * @return
   */
  TrieNodeBase searchTrieNodeBase(String key,
      boolean returnExternalNodeWithUnmatchedKey) {
    String rkey = reverseString(key);
    TrieNodeBase root_node = trie.perBaseRootTrieNodeBase
        .get(rkey.length() > 0 ? rkey.charAt(0) : '\0');
    // printf("[search] root_node number = '%u'\n", static_cast<unsigned
    // char>(rkey[0]));
    // printf("[search] root_node: %p\n", root_node);
    TrieNodeBase result = searchTrieNodeBase(root_node, key, rkey, 1,
        returnExternalNodeWithUnmatchedKey);
    /*
     * if (NULL != result ) { std::vector<WordformInfo>::const_iterator citer =
     * result->wordform_infos_.begin(); while(citer !=
     * result->wordform_infos_.end()) { printf("[search] found '%s' ('%s')\n",
     * result->key_, citer->lemm); citer++; } }
     */
    return result;
  }

  // protected:
  // выполнить поиск в дереве основ
  TrieNodeBase searchTrieNodeBase(TrieNodeBase h, String key, String rkey,
      int key_order, boolean returnExternalNodeWithUnmatchedKey) {
    // если дошли до недостроенного дерева...
    if (null == h) {
      // printf("[search_base_node] NULL == h\n");
      return null;
    }

    // если находимся во внутреннем узле...
    if (h.internal()) {
      char search_kov =
          rkey.length() > key_order ? rkey.charAt(key_order) : '\0';
      char existing_kov = h.kov;
      // printf("[search_base_node] search_kov: '%c'(%u); existing_kov:
      // '%c'(%u); key_order: '%d'\n",
      // search_kov, static_cast<unsigned char>(search_kov), existing_kov,
      // static_cast<unsigned char>(existing_kov), key_order);
      if (search_kov < existing_kov) {
        // printf("[search_base_node] search_kov < existing_kov\n");
        return searchTrieNodeBase(h.l, key, rkey, key_order,
            returnExternalNodeWithUnmatchedKey);
      }
      if (search_kov == existing_kov) {
        // printf("[search_base_node] search_kov == existing_kov\n");
        return searchTrieNodeBase(h.m, key, rkey, key_order + 1,
            returnExternalNodeWithUnmatchedKey);
      }
      if (search_kov > existing_kov) {
        // printf("[search_base_node] search_kov > existing_kov\n");
        return searchTrieNodeBase(h.r, key, rkey, key_order,
            returnExternalNodeWithUnmatchedKey);
      }
    } else {
      // если пришли во внешний узел...
      // сравниваем по полному совпадению...
      // printf("[search_base_node] pre strcmp(h->key_, key) == 0\n");
      if (h.key == key) {
        // printf("[search_base_node] strcmp(h->key_, key) == 0 ('%s':'%s')\n",
        // h->key_, key);
        return h;
      }
      // рассматриваем вариант совпадения основ
      // printf("[search_base_node] pre strstr(rkey, k->rkey_) == rkey\n");
      if (rkey.startsWith(h.rkey)) {
        // printf("[search_base_node] strstr(rkey, k->rkey_) == rkey
        // ('%s':'%s')\n",
        // rkey, h->rkey_);
        return h;
      }
      // во всех остальных случаях - в зависимости от значения
      // "returnExternalNodeWithUnmatchedKey"
      // printf("[search_base_node] return NULL\n");
      if (returnExternalNodeWithUnmatchedKey)
        return h;
      else
        return null;
    }
    return null;
  }

  // выполнить анализ словоформы
  public List<WordformAnalysisResult> analyse(String wordform) {
    ProcessingResult pr = new ProcessingResult();
    // printf("START processing: '%s'\n", wordform);
    List<WordformAnalysisResult> result =
        new LinkedList<WordformAnalysisResult>();
    analyseEndings(wordform, pr);
    String wordform_base_copy, ending_copy;
    int wordform_length = wordform.length();

    // для каждого окончания отрабатываем вариант
    for (int i = 0; i < pr.foundEndingsCount; i++) {
      wordform_base_copy =
          wordform.substring(0, wordform_length - pr.endingLengths[i]);
      // получаем информацию о основе
      TrieNodeBase baseNode = searchTrieNodeBase(wordform_base_copy);
      if (null != baseNode) {
        ending_copy = wordform.substring(wordform_base_copy.length(),
            wordform_base_copy.length() + pr.endingLengths[i]);
        List<WordformInfo> wordformInfos_ = baseNode.wordformInfos;
        for (WordformInfo wordformInfo : wordformInfos_) {
          EndingModel endingModel =
              getEndingModel(wordformInfo.endingModelNumber, ending_copy);
          if (null != endingModel) {
            boolean exactMatch = true;
            /*
             * WordformAnalysisResult wf = new WordformAnalysisResult(
             * wordformInfo.endingModelNumber, wordformInfo.lemm, endingModel,
             * null);
             */
            // определяем факт точного/неточного совпадения
            if (baseNode.keyLength + pr.endingLengths[i] != wordform_length)
              exactMatch = false;

            // ищем настоящее окончание
            for (int j = 0; j < endingModel.endings.size(); j++) {
              if (ending_copy.equals(endingModel.endings.get(j).value))
                result.add(new WordformAnalysisResult(
                    wordformInfo.opencorporaId, wordformInfo.lemm,
                    wordformInfo.base + endingModel.endings.get(j).value,
                    wordformInfo.endingModelNumber, exactMatch,
                    endingModel.endings.get(j).grammemes));
            }

          }
        } // for (WordformInfo wordformInfo : wordformInfos_)
      }
    }
    return result;
  }

  // получить модели грамматем по номеру модели и самому окончанию
  EndingModel getEndingModel(int endingModelNumber, String ending) {
    // printf
    // ("[get_grammatems] grammatem_model_number: '%d', ending: '%s'\n",
    // grammatem_model_number, ending);
    String rending = reverseString(ending);
    if (trie.perEndingModelRootTrieNodeEnding.size() <= endingModelNumber)
      return null;
    TrieNodeEnding root_node =
        trie.perEndingModelRootTrieNodeEnding.get(endingModelNumber);
    TrieNodeEnding result = searchTrieNodeEnding(root_node, rending, 0);
    if (null == result)
      return null;
    TrieNodeEnding endingModelResult = (TrieNodeEnding) result;
    return endingModelResult.endingModel;
  }

  // получить деревья основ
  List<TrieNodeBase> getRootTrieNodesBase() {
    return trie.perBaseRootTrieNodeBase;
  }

  public List<SurnameAnalysisResult> predictAsSurname(String wordform) {
    ProcessingResult pr = new ProcessingResult();
    // printf("START processing: '%s'\n", wordform);
    List<SurnameAnalysisResult> result =
        new LinkedList<SurnameAnalysisResult>();
    analyseEndings(wordform, pr);
    String wordform_base_copy, ending_copy;
    int wordform_length = wordform.length();

    // для каждого окончания отрабатываем вариант
    for (int i = 0; i < pr.foundEndingsCount; i++) {
      wordform_base_copy =
          wordform.substring(0, wordform_length - pr.endingLengths[i]);
      // получаем информацию о основе
      TrieNodeBase baseNode = searchTrieNodeBase(wordform_base_copy, true);
      if (null != baseNode) {
        ending_copy = wordform.substring(wordform_base_copy.length(),
            wordform_base_copy.length() + pr.endingLengths[i]);
        List<WordformInfo> wordformInfos_ = baseNode.wordformInfos;
        for (WordformInfo wordformInfo : wordformInfos_) {
          EndingModel endingModel =
              getEndingModel(wordformInfo.endingModelNumber, ending_copy);
          if (null != endingModel) {
            boolean exactMatch = true;

            // определяем факт точного/неточного совпадения
            if (baseNode.keyLength + pr.endingLengths[i] != wordform_length)
              exactMatch = false;

            // определяем степень совпадения баз
            int commonPartLength = StringUtils
                .getCommonPrefix(StringUtils.reverse(wordform_base_copy),
                    StringUtils.reverse(wordformInfo.base))
                .length();

            // ищем настоящее окончание
            for (int j = 0; j < endingModel.endings.size(); j++)
              if (ending_copy.equals(endingModel.endings.get(j).value))
                result.add(new SurnameAnalysisResult(
                    wordformInfo.opencorporaId, wordform_base_copy,
                    wordform_base_copy + endingModel.endings.get(j).value,
                    wordformInfo.endingModelNumber, exactMatch, commonPartLength,
                    endingModel.endings.get(j).grammemes));
          } // if (null != endingModel) {
        } // for (WordformInfo wordformInfo : wordformInfos_)
      } // if (null != endingModel) {
    } // for (int i = 0; i < pr.foundEndingsCount; i++) {
    return result;
  }

}

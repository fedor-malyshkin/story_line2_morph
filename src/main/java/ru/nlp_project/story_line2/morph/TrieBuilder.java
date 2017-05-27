package ru.nlp_project.story_line2.morph;

import java.util.List;
import java.util.Map;

import ru.nlp_project.story_line2.morph.EndingModel.Ending;

/**
 * Класс для построения trie.
 * 
 * 
 * @author fedor
 *
 */
class TrieBuilder {

  private Trie trie = new Trie();

  // добавить очередную модель словоизменения
  void addEndingModel(int emn, EndingModel em) {
    // получае узел, специфичный для каждой модели окончаний
    TrieNodeEnding perEndingModelRootNode =
        trie.perEndingModelRootTrieNodeEnding.get(emn);

    // для каждой записи модели грамматем делаем одно и то же...
    for (Ending ending : em.endings) {
      String rkey = reverseString(ending.value);

      // добавить в общее дерево окончаний (для всех моделей)
      TrieNodeEnding root_node = trie.commonRootTrieNodeEnding
          .get(rkey.length() > 0 ? rkey.charAt(0) : '\0');
      if (1 == rkey.length()) {
        trie.hasOneLetterEndings[rkey.length() > 0 ? rkey.charAt(0) : '\0'] =
            true;
      } else {
        if (null == root_node) {
          root_node =
              createExternalTrieNodeEndingContainer(ending.value, rkey, 1, em);
          trie.commonRootTrieNodeEnding
              .set(rkey.length() > 0 ? rkey.charAt(0) : '\0', root_node);
        } else {
          insertTrieNodeEnding(root_node, ending.value, rkey, 1, null);
        } // if (null == root_node) {
      } // if (1 == rkey.length()) {

      // добавить в дерево окончаний, специфичных для модели (включая
      // информацию о грамматемах)
      // добавить в общее дерево окончаний (для конкретной модели)
      if (null == perEndingModelRootNode) {
        perEndingModelRootNode =
            createExternalTrieNodeEndingContainer(ending.value, rkey, 0, em);
        trie.perEndingModelRootTrieNodeEnding.put(emn, perEndingModelRootNode);
      } else {
        insertTrieNodeEnding(perEndingModelRootNode, ending.value, rkey, 0, em);
      }
    }
  }

  void combineDataForTheSameNodes(TrieNodeEnding existing_node,
      TrieNodeEnding new_node) {
    TrieNodeEnding ending_new_node = new_node;
    TrieNodeEnding ending_existing_node = existing_node;
    if (null != ending_new_node.endingModel)
      ending_existing_node.endingModel = ending_new_node.endingModel;
  }

  TrieNodeEnding createExternalTrieNodeEndingContainer(String key, String rkey,
      int key_order, EndingModel endingModel) {
    char i = rkey.length() > key_order ? rkey.charAt(key_order) : '\0';
    // создать новый "внешний" узел в дереве окончаний
    TrieNodeEnding proxy = new TrieNodeEnding(key, '\0');
    proxy.internal = false;
    proxy.endingModel = endingModel;
    return new TrieNodeEnding(proxy, i);
  }

  TrieNodeEnding createNewTrieNodeEnding(String key, EndingModel endingModel) {
    // создать новый "внешний" узел в дереве окончаний
    TrieNodeEnding result = new TrieNodeEnding(key, '\0');
    result.internal = false;
    if (null != endingModel)
      result.endingModel = endingModel;
    return result;
  }

  // получить деревья окончаний для каждой модели грамматем
  Map<Integer, TrieNodeEnding> getEndingModelRootNodes() {
    return trie.perEndingModelRootTrieNodeEnding;
  }

  Trie getTrie() {
    return trie;
  }

  // добавить новую запись в дерево окончаний
  void insertTrieNodeEnding(TrieNodeEnding h, String key, String rkey,
      int key_order, EndingModel endingModel) {

    if (!h.internal) {
      TrieNodeEnding other_h = h.cloneSelf();

      TrieNodeEnding temp = splitExistingTrieNodeEnding(
          createNewTrieNodeEnding(key, endingModel), other_h, key_order);
      h.assignSelfWith(temp);
      return;
    }

    char ui = rkey.length() > key_order ? rkey.charAt(key_order) : '\0';
    char h_kov = h.kov;
    // printf("[insert_ending_node] i = '%c'(%u)\n", rkey[key_order],
    // static_cast<unsigned char>(rkey[key_order]));
    // printf("[insert_ending_node] h->kov_ = '%c'(%u)\n", h->kov_,
    // static_cast<unsigned char>(h->kov_));

    if (ui < h_kov) {
      if (null == h.l)
        h.l = createExternalTrieNodeEndingContainer(key, rkey, key_order,
            endingModel);
      else
        insertTrieNodeEnding(h.l, key, rkey, key_order, endingModel);
      return;
    }
    if (ui == h_kov) {
      if (null == h.m)
        h.m = createExternalTrieNodeEndingContainer(key, rkey, key_order,
            endingModel);
      else
        insertTrieNodeEnding(h.m, key, rkey, key_order + 1, endingModel);
      return;
    }
    if (ui > h_kov) {
      if (null == h.r)
        h.r = createExternalTrieNodeEndingContainer(key, rkey, key_order,
            endingModel);
      else
        insertTrieNodeEnding(h.r, key, rkey, key_order, endingModel);
      return;
    }
    throw new IllegalStateException();
  }

  TrieNodeEnding newTrieNodeEnding(TrieNodeEnding other_node, char kov) {
    return new TrieNodeEnding(other_node, kov);
  }

  String reverseString(String input) {
    return (new StringBuffer(input)).reverse().toString();
  }

  // ending tree
  // разделить существующую запись в дереве окончаний
  TrieNodeEnding splitExistingTrieNodeEnding(TrieNodeEnding new_node,
      TrieNodeEnding existing_node, int key_order) {
    // в случае если основы слов в узлах дерева равны - объединить
    // информацию узлом (соендинить узлы)
    if (new_node.key.equals(existing_node.key)) {
      combineDataForTheSameNodes(existing_node, new_node);
      // existing_node.endingModelNodes_.addAll(new_node.endingModelNodes_);
      return existing_node;
    } // if (strcmp(new_node->key_, existing_node->key_) == 0 ) {
    char new_kov =
        new_node.keyLength > key_order ? new_node.rkey.charAt(key_order) : '\0';
    char existing_kov = existing_node.keyLength > key_order
        ? existing_node.rkey.charAt(key_order) : '\0';
    // printf("[split_existing_ending_node] new_kov: '%c'; existing_kov: '%c',
    // key_order: '%d'\n",
    // new_kov, existing_kov, key_order);

    TrieNodeEnding t = newTrieNodeEnding(null, existing_kov);
    if (new_kov < existing_kov) {
      // printf("[split_existing_ending_node] result: 'new_kov <
      // existing_kov'\n");
      t.m = existing_node;
      t.l = newTrieNodeEnding(new_node, new_kov);
    }
    if (new_kov == existing_kov) {
      // printf("[split_existing_ending_node] result: 'new_kov ==
      // existing_kov'\n");
      t.m = splitExistingTrieNodeEnding(new_node, existing_node, key_order + 1);
    }
    if (new_kov > existing_kov) {
      // printf("[split_existing_ending_node] result: 'new_kov >
      // existing_kov'\n");
      t.m = existing_node;
      t.r = newTrieNodeEnding(new_node, new_kov);
      // printf("[split_existing_ending_node] t: '%p'\n", t);
      // printf("[split_existing_ending_node] t->m_: '%p'\n", t->m_);
      // printf("[split_existing_ending_node] t->r_: '%p'\n", t->r_);
    }
    return t;
  }

  // добавить очередную основу словоформы
  void addWordformBase(String opencorporaId, String lemm, String base,
      int ending_model_number) {
    WordformInfo wf_info = new WordformInfo(opencorporaId, new String(lemm),
        base, ending_model_number);
    String rbase = reverseString(base);
    // printf("[add_wordform_base] root_node number = '%u'\n",
    // static_cast<unsigned char>(rbase[0]));
    TrieNodeBase root_node = trie.perBaseRootTrieNodeBase
        .get(rbase.length() > 0 ? rbase.charAt(0) : '\0');

    if (null == root_node) {
      root_node = createExternalTrieNodeBaseContainer(wf_info, base, rbase, 1);
      trie.perBaseRootTrieNodeBase
          .set(rbase.length() > 0 ? rbase.charAt(0) : '\0', root_node);
    } else {
      insertTrieNodeBase(root_node, wf_info, base, rbase, 1);
    }

  }

  TrieNodeBase createExternalTrieNodeBaseContainer(WordformInfo wfi, String key,
      String rkey, int key_order) {
    char i = rkey.length() > key_order ? rkey.charAt(key_order) : '\0';
    TrieNodeBase result =
        new TrieNodeBase(newExternalTrieNodeBase(key, wfi), i);
    return result;
  }

  // добавить новую запись в дерево основ
  void insertTrieNodeBase(TrieNodeBase h, WordformInfo wfi, String key,
      String rkey, int key_order) {
    if (!h.internal()) {
      // printf("[insert_base_node] '!h->internal()' is 'true'\n",
      // !h->internal());
      TrieNodeBase other_h = h.clone();

      TrieNodeBase temp = splitExistingTrieNodeBase(
          newExternalTrieNodeBase(key, wfi), other_h, key_order);
      h.assign(temp);
      return;
    }

    // printf("[insert_base_node] key: '%s'\n",key);
    // printf("[insert_base_node] h->l_:'%p'<h->m_:'%p'>h->r_:'%p'\n",
    // h->l_, h->m_, h->r_);
    char ui = rkey.length() > key_order ? rkey.charAt(key_order) : '\0';
    char h_kov = h.kov;
    // printf("[insert_base_node] i = '%c'(%u)\n", rkey[key_order],
    // static_cast<unsigned char>(rkey[key_order]));
    // printf("[insert_base_node] h->kov_ = '%c'(%u)\n", h->kov_,
    // static_cast<unsigned char>(h->kov_));

    if (ui < h_kov) {
      // printf("i < h->kov_\n");
      if (null == h.l)
        h.l = createExternalTrieNodeBaseContainer(wfi, key, rkey, key_order);
      else
        insertTrieNodeBase(h.l, wfi, key, rkey, key_order);
      return;
    }
    if (ui == h_kov) {
      // printf("i == h->kov_\n");
      if (null == h.m)
        h.m =
            createExternalTrieNodeBaseContainer(wfi, key, rkey, key_order + 1);
      else
        insertTrieNodeBase(h.m, wfi, key, rkey, key_order + 1);
      return;
    }
    if (ui > h_kov) {
      // printf("i > h->kov_\n");
      if (null == h.r)
        h.r = createExternalTrieNodeBaseContainer(wfi, key, rkey, key_order);
      else
        insertTrieNodeBase(h.r, wfi, key, rkey, key_order);
      return;
    }

    throw new IllegalStateException();
  }

  // создать новый "внешний" узел в дереве основ
  TrieNodeBase newExternalTrieNodeBase(String base, WordformInfo wfi) {
    TrieNodeBase result = new TrieNodeBase(base, wfi, '\0');
    result.internal = false;
    return result;
  }

  // base tree
  // разделить существующую запись в дереве основ
  TrieNodeBase splitExistingTrieNodeBase(TrieNodeBase new_node,
      TrieNodeBase existing_node, int key_order) {
    // в случае если основы слов в узлах дерева равны - в существующий,
    // добавить информацию о словоформе
    if (new_node.key.equals(existing_node.key)) {
      // printf("[split_existing_base_node] result: 'strcmp(new_node->key_,
      // existing_node->key_) == 0'\n");
      List<WordformInfo> wfs = new_node.wordformInfos;
      existing_node.wordformInfos.addAll(wfs);
      return existing_node;
    }

    char new_kov =
        new_node.keyLength > key_order ? new_node.rkey.charAt(key_order) : '\0';
    char existing_kov = existing_node.keyLength > key_order
        ? existing_node.rkey.charAt(key_order) : '\0';
    // printf("[split_existing_base_node] new_kov: '%c'; existing_kov: '%c',
    // key_order: '%d'\n",
    // new_kov, existing_kov, key_order);

    TrieNodeBase t = new TrieNodeBase(null, existing_kov);
    if (new_kov < existing_kov) {
      // printf("[split_existing_base_node] result: 'new_kov <
      // existing_kov'\n");
      t.m = existing_node;
      t.l = new TrieNodeBase(new_node, new_kov);
    }
    if (new_kov == existing_kov) {
      // printf("[split_existing_base_node] result: 'new_kov ==
      // existing_kov'\n");
      t.m = splitExistingTrieNodeBase(new_node, existing_node, key_order + 1);
    }
    if (new_kov > existing_kov) {
      // printf("[split_existing_base_node] result: 'new_kov >
      // existing_kov'\n");
      t.m = existing_node;
      t.r = new TrieNodeBase(new_node, new_kov);
      // printf("[split_existing_base_node] t: '%p'\n", t);
      // printf("[split_existing_base_node] t->m_: '%p'\n", t->m_);
      // printf("[split_existing_base_node] t->r_: '%p'\n", t->r_);
    }
    return t;

  }

}

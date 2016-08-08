package ru.nlp_project.story_line2.morph;

import java.util.LinkedList;
import java.util.List;

class TrieNodeBase {
  List<WordformInfo> wordformInfos;
  boolean internal;
  String rkey;
  String key;
  char kov;
  TrieNodeBase l;
  TrieNodeBase m;
  TrieNodeBase r;
  int keyLength;

  TrieNodeBase(String key, WordformInfo wordformInfo, char kov) {
    m = l = r = null;
    this.internal = true;

    if (null != wordformInfo) {
      if (null == wordformInfos) {
        this.wordformInfos = new LinkedList<WordformInfo>();
      }
      this.wordformInfos.add(wordformInfo);
    }

    this.kov = kov;

    if (null != key) {
      this.keyLength = key.length();
      this.key = new String(key);
      this.rkey = new StringBuffer(key).reverse().toString();
    } else {
      this.key = null;
      this.rkey = null;
    }
  }

  TrieNodeBase(TrieNodeBase node, char kov) {
    m = l = r = null;
    this.internal = true;
    this.key = null;
    this.rkey = null;
    this.kov = kov;
    this.m = node;
  }

  boolean internal() {
    return internal;
  }

  private TrieNodeBase() {
  }

  protected TrieNodeBase clone() {
    TrieNodeBase result = new TrieNodeBase();
    result.wordformInfos = this.wordformInfos;
    result.internal = this.internal;
    result.rkey = this.rkey;
    result.key = this.key;
    result.kov = this.kov;
    result.l = this.l;
    result.m = this.m;
    result.r = this.r;
    result.keyLength = this.keyLength;
    return result;
  }

  void assign(TrieNodeBase other) {
    wordformInfos = other.wordformInfos;
    this.internal = other.internal;
    this.rkey = other.rkey;
    this.key = other.key;
    this.kov = other.kov;
    this.l = other.l;
    this.m = other.m;
    this.r = other.r;
    this.keyLength = other.keyLength;
  }

  @Override
  public String toString() {
    return "[internal=" + internal + ", key=" + key + ", keyLength="
        + keyLength + ", kov=" + kov + "]";
  }
}

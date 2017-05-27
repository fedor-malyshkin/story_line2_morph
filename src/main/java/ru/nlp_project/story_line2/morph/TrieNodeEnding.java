package ru.nlp_project.story_line2.morph;

/**
 * Лист в trie
 * 
 * @author fedor
 *
 */
class TrieNodeEnding {
  EndingModel endingModel = null;
  boolean internal;
  String key;
  int keyLength;
  char kov;
  TrieNodeEnding l;
  TrieNodeEnding m;
  TrieNodeEnding r;
  String rkey;

  protected TrieNodeEnding() {
  }

  protected TrieNodeEnding(TrieNodeEnding node, char kov) {
    m = l = r = null;
    this.internal = true;
    this.key = null;
    this.rkey = null;
    this.kov = kov;
    this.m = node;
  }

  protected TrieNodeEnding(String key, char kov) {
    m = l = r = null;
    this.internal = true;
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

  public void assignSelfWith(TrieNodeEnding other) {
    copyFromTo(other, this);
    this.endingModel = other.endingModel;
  }

  public TrieNodeEnding cloneSelf() {
    TrieNodeEnding result = new TrieNodeEnding();
    copyFromTo(this, result);
    result.endingModel = this.endingModel;
    return result;
  }

  protected void copyFromTo(TrieNodeEnding from, TrieNodeEnding to) {
    to.internal = from.internal;
    to.rkey = from.rkey;
    to.key = from.key;
    to.kov = from.kov;
    to.l = from.l;
    to.m = from.m;
    to.r = from.r;
    to.keyLength = from.keyLength;
  }

  @Override
  public String toString() {
    return "[internal=" + internal + ", key=" + key + ", keyLength="
        + keyLength + ", kov=" + kov + "]";
  }

}
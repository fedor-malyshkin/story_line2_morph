package ru.nlp_project.story_line2.morph;

import java.io.Serializable;

class WordformInfo implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  String base;
  String lemm;
  int endingModelNumber;
  String opencorporaId;

  public WordformInfo(String id, String lemm, String base,
      int endingModelNumber) {
    super();
    this.opencorporaId = id;
    this.lemm = lemm;
    this.base = base;
    this.endingModelNumber = endingModelNumber;
  }

  @Override
  public String toString() {
    return "WordformInfo [base=" + base + ", lemm=" + lemm
        + ", endingModelNumber=" + endingModelNumber + "]";
  }

}

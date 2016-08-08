package ru.nlp_project.story_line2.morph;

public class WordformAnalysisResult {
  public boolean exactMatch;
  public String lemm;
  public Grammemes grammemes;
  public String value;
  public String opencorporaId;
  public int endingModelNumber;

  public WordformAnalysisResult(String opencorporaId, String lemm, String value,
      int endingModelNumber, boolean exactMatch, Grammemes grammemes) {
    this.opencorporaId = opencorporaId;
    this.exactMatch = exactMatch;
    this.endingModelNumber = endingModelNumber;
    this.lemm = lemm;
    this.grammemes = grammemes;
    this.value = value;
  }

  @Override
  public String toString() {
    return "WordformAnalysisResult [endingModelNumber=" + endingModelNumber
        + ", exactMatch=" + exactMatch + ", lemm=" + lemm + ", grammemes="
        + grammemes + "]";
  }

}

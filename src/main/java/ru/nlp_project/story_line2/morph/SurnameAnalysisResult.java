package ru.nlp_project.story_line2.morph;

public class SurnameAnalysisResult {

  public int commonPartLength;
  public boolean exactMatch;
  public Grammemes grammemes;
  public String opencorporaId;
  public int endingModelNumber;
  public String value;
  public String base;

  public SurnameAnalysisResult(String opencorporaId, String base, String value,
      int endingModelNumber, boolean exactMatch, int commonPartLength,
      Grammemes grammemes) {
    this.opencorporaId = opencorporaId;
    this.exactMatch = exactMatch;
    this.endingModelNumber = endingModelNumber;
    this.commonPartLength = commonPartLength;
    this.base = base;
    this.grammemes = grammemes;
    this.value = value;
  }
}

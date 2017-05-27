package ru.nlp_project.story_line2.morph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ru.nlp_project.story_line2.morph.EndingModel.Ending;

/**
 * 
 * Основной класс для морфологического анализа.
 * 
 * @author fedor
 *
 */
public class MorphAnalyser {

  class Wordform {
    Grammemes grammemes;
    String wordform;
    int score;

    public Wordform(String wordform, Grammemes grammemes) {
      super();
      this.wordform = wordform;
      this.grammemes = grammemes;
    }

    @Override
    public String toString() {
      return "[" + wordform + " (" + grammemes + ")]";
    }

  }

  public static MorphAnalyser newInstance(InputStream zipDBIS,
      boolean initLookup) throws IOException {
    MorphAnalyser result = new MorphAnalyser(zipDBIS);
    result.initialize(initLookup);
    return result;
  }

  private MorphDBReader dbReader;
  private TrieBuilder trieBuilder;
  private TrieReader trieReader;
  private InputStream zipDBIS = null;
  private LexemeManager lxManager;

  private MorphAnalyser(InputStream inputStream) {
    this.zipDBIS = inputStream;
  }

  public List<WordformAnalysisResult> analyse(String wordform) {
    return trieReader.analyse(wordform);
  }

  private void initialize(boolean initLookup) throws IOException {
    this.trieBuilder = new TrieBuilder();
    this.lxManager = LexemeManager.newInstance();
    dbReader =
        MorphDBReader.newInstance(trieBuilder, lxManager, zipDBIS, initLookup);
    dbReader.readCompressedMorphDB();
    trieReader = new TrieReader(trieBuilder.getTrie());
  }

  public Collection<WordformAnalysisResult>
      getWordformAnalysisResultsById(String opencorporaId) {
    List<WordformAnalysisResult> result = new LinkedList<>();
    WordformInfo wordformInfo = lxManager.getWordformInfoById(opencorporaId);
    EndingModel endingModel =
        lxManager.getEndingModelByNumber(wordformInfo.endingModelNumber);
    for (Ending e : endingModel.getEndings()) {
      result.add(new WordformAnalysisResult(wordformInfo.opencorporaId,
          wordformInfo.lemm, wordformInfo.base + e.getValue(),
          wordformInfo.endingModelNumber, true, e.getGrammemes()));
    }
    return result;
  }

  public EndingModel getEndingModelByNumber(int emn) {
    return lxManager.getEndingModelByNumber(emn);
  }

  public List<SurnameAnalysisResult> predictAsSurname(String wordform) {
    return trieReader.predictAsSurname(wordform);
  }

  public String getPredictedSurnameForGrammeme(String base,
      int endingModelNumber, Grammemes grammemes2) {
    EndingModel endingModel = getEndingModelByNumber(endingModelNumber);
    for (Ending ending : endingModel.endings) {
      if (ending.grammemes.equals(grammemes2))
        return base + ending.value;
    }
    return "";
  }

  public void shutdown() {
    lxManager.shutdown();
  }
  
}

package ru.nlp_project.story_line2.morph;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * КЛасс для работы с лексемами - их кэширования и возврата по идентификатору.
 * 
 * @author fedor
 *
 */
public class LexemeManager {
  public static LexemeManager newInstance() {
    LexemeManager result = new LexemeManager();
    result.initialize();
    return result;
  }

  private Cache lexemsCache;

  private Cache endingModelsCache;

  private LexemeManager() {
  }

  public void addEndingModel(int emn, EndingModel em) {
    putEM(emn, em);
  }

  public void addWordformBase(String opencorporaId, String lemma, String base,
      int emn) {
    WordformInfo wordformInfo =
        new WordformInfo(opencorporaId, lemma, base, emn);
    putWF(opencorporaId, wordformInfo);
  }

  private EndingModel getEM(int emn) {
    Element element = endingModelsCache.get(emn);
    return (EndingModel) element.getObjectValue();
  }

  public EndingModel getEndingModelByNumber(int emn) {
    return getEM(emn);
  }

  private WordformInfo getWF(String opencorporaId) {
    Element element = lexemsCache.get(opencorporaId);
    return (WordformInfo) element.getObjectValue();
  }

  public WordformInfo getWordformInfoById(String opencorporaId) {
    return getWF(opencorporaId);
  }

  private void initialize() {
    CacheManager cacheManager = CacheManager.getInstance();
    lexemsCache = cacheManager.getCache("lexems");
    lexemsCache.removeAll();
    endingModelsCache = cacheManager.getCache("ending_models");
    endingModelsCache.removeAll();
  }

  private void putEM(int emn, EndingModel em) {
    Element element = new Element(emn, em);
    endingModelsCache.put(element);

  }

  private void putWF(String opencorporaId, WordformInfo wordformInfo) {
    Element element = new Element(opencorporaId, wordformInfo);
    lexemsCache.put(element);
  }

  public void shutdown() {
    CacheManager.getInstance().shutdown();
  }

}
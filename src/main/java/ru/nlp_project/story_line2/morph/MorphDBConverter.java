package ru.nlp_project.story_line2.morph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;

import ru.nlp_project.story_line2.morph.EndingModel.Ending;
import ru.nlp_project.story_line2.morph.GrammemeUtils.GrammemeEnum;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Morph database converter.
 * 
 * Политика обработки тэгов такова:
 * 1) каждый метод обрабатывает свой тэг вплоть до перехода на закрывающий тэг самого себя -- дальнейшая ответственность за вызывающим кодом.
 * 2) Каждый метод в начале проверяет, что фрагмент для которого он был вызван -- его, в обратном случае ничего не делает. 
 * 3) После проверки и чтения всех аттрибутов -- переходим к следующему тэгу
 * 4) После проведения всех работ и чтения внутренних тэгов -- переходим к закрывающему тэгу
 *  * 
 * @author fedor
 *
 */
@SuppressWarnings("unused")
public class MorphDBConverter {
  class Paradigm {
    List<Collection<String>> grammemas = new ArrayList<Collection<String>>();
    String opencorporaId = null;
    String lemma;
    List<String> wordforms = new ArrayList<String>();

    public void addWordform(String wordforms, Collection<String> tags) {
      this.wordforms.add(wordforms);
      this.grammemas.add(new ArrayList<String>(tags));
    }

    @Override
    public String toString() {
      return "Paradigm [lemma=" + lemma + ", grammemas=" + grammemas
          + ", wordforms=" + wordforms + "]";
    }

  }

  public static void main(String[] args) {
    MorphDBConverter converter = new MorphDBConverter();
    converter.initialize();
    converter.readZipMorphDB();
  }

  // private Cache cache;
  // private CacheManager cacheManager;
  private Logger log;
  private Map<Integer, List<EndingModel>> emCache;

  void addParadigm(Paradigm paradigm) throws IOException {
    long start = System.nanoTime();
    int prefixLen = calculateLongestCommonPrefix(paradigm);

    long start1 = System.nanoTime();
    long duration = start1 - start;
    // log.info(String.format("> 'calculateLongestCommonPrefix' duration: %d
    // ms",TimeUnit.NANOSECONDS.toMillis(duration)));

    /*
     * В случае наличия сравнительной степени "по-" выполняем разделение на 2
     * части одной леммы (для уменьшения кол-ва моделей окончаний)
     */
    if (prefixLen == 0) {
      Iterator<Collection<String>> iteratorG = paradigm.grammemas.iterator();
      Iterator<String> iteratorB = paradigm.wordforms.iterator();
      Paradigm next = new Paradigm();
      next.opencorporaId = paradigm.opencorporaId;
      while (iteratorB.hasNext()) {
        String wordform = iteratorB.next();
        Collection<String> grammemas = iteratorG.next();
        if (grammemas.contains("Cmp2")) {
          next.addWordform(wordform, grammemas);
          // не забываем удалять обнаруженные слово формы и граммемы
          iteratorB.remove();
          iteratorG.remove();
        }
      }
      // если что-то выскали....
      if (next.wordforms.size() > 0) {
        addParadigm(next);
        prefixLen = calculateLongestCommonPrefix(paradigm);
      }
    }

    SoftReference<EndingModel> endingModel =
        new SoftReference<EndingModel>(createEndingModel(prefixLen, paradigm));
    long start2 = System.nanoTime();
    duration = start2 - start1;
    // log.info(String.format("> 'createEndingModel' duration: %d
    // ms",TimeUnit.NANOSECONDS.toMillis(duration)));

    int emn = 0;
    emn = processEndingModel(paradigm, endingModel.get());
    endingModel.clear();

    long start3 = System.nanoTime();
    duration = start3 - start2;
    // log.info(String.format("> 'processEndingModel' duration: %d
    // ms",TimeUnit.NANOSECONDS.toMillis(duration)));

    addWordforms(prefixLen, emn, paradigm);
    duration = System.nanoTime() - start;
    // log.info(String.format("> 'addParadigm' duration: %d ms",
    // TimeUnit.NANOSECONDS.toMillis(duration)));
  }

  private int processEndingModel(Paradigm paradigm, EndingModel endingModel) {
    int result = 0;
    List<EndingModel> list = emCache.get(endingModel.hashCode);
    if (list != null) {
      if (list.contains(endingModel)) {
        int indexOf = list.indexOf(endingModel);
        EndingModel endingModel2 = list.get(indexOf);
        return endingModel2.modelNum;
      } else {
        // << GENERATE NEW modelNum
        result = endingModelNumber++;
        endingModel.modelNum = result;

        list.add(endingModel);
        emCache.put(endingModel.hashCode, list);
      }
    } else {
      // << GENERATE NEW modelNum
      result = endingModelNumber++;
      endingModel.modelNum = result;

      list = new ArrayList<EndingModel>();
      list.add(endingModel);
      emCache.put(endingModel.hashCode, list);
      System.err.println("add record to cache. size = " + emCache.size() + "["
          + paradigm.lemma + "] {" + list + "}");
    }

    return result;
  }

  void addWordforms(int prefixLen, int emn, Paradigm paradigm)
      throws IOException {
    if (paradigm.wordforms.size() > 0)
      jsonWriteP(paradigm.lemma, paradigm.opencorporaId,
          paradigm.wordforms.get(0).substring(0, prefixLen), emn);
  }

  void jsonWriteP(String lemma, String opencorporaId, String base, int emn)
      throws IOException {
    jg.writeStartObject();
    jg.writeFieldName("l");
    jg.writeString(lemma);
    jg.writeFieldName("id");
    jg.writeString(opencorporaId);
    jg.writeFieldName("b");
    jg.writeString(base);
    jg.writeFieldName("emn");
    jg.writeNumber(emn);
    jg.writeEndObject();
  }

  int calculateLongestCommonPrefix(Paradigm paradigm) {
    int res = 0;
    if (paradigm.wordforms.size() == 0)
      return res;
    res = paradigm.wordforms.get(0).length();
    if (paradigm.wordforms.size() < 2)
      return res;

    boolean notMatch = true;
    String str = null;
    while (notMatch) {
      str = paradigm.wordforms.get(0).substring(0, res);
      notMatch = false;
      for (int i = 1; i < paradigm.wordforms.size(); i++) {
        if (!paradigm.wordforms.get(i).startsWith(str))
          notMatch = true;
      }
      notMatch = notMatch || false;
      if (notMatch) {
        res--;
        if (res == 0)
          return res;
      }
    }
    return res;
  }

  EndingModel createEndingModel(int prefixLen, Paradigm paradigm) {
    EndingModel result = new EndingModel();
    for (int i = 0; i < paradigm.wordforms.size(); i++) {
      Grammemes grammemes = new Grammemes();
      GrammemeUtils.fillGrammemesOpencorporaTags(paradigm.grammemas.get(i),
          grammemes);
      result.addEnding(paradigm.wordforms.get(i).substring(prefixLen),
          grammemes);
    }
    result.calculateHashCode();
    return result;
  }

  private void gotoCloseTag(XMLStreamReader parser, String tag)
      throws XMLStreamException {
    while (true) {
      if (parser.getEventType() == XMLStreamConstants.END_ELEMENT
          && parser.getLocalName().equals(tag))
        break;
      parser.next();
    }
  }

  void initialize() {
    log = Logger.getLogger(this.getClass().getName());
    /*
     * cacheManager = CacheManager.newInstance(); cache =
     * cacheManager.getCache("convertOpencorporaDatabase");
     */
    emCache = new HashMap<Integer, List<EndingModel>>(200);
  }

  private boolean isStartTag(XMLStreamReader parser, String tag)
      throws XMLStreamException {
    return parser.getEventType() == XMLStreamConstants.START_ELEMENT
        && parser.getLocalName().equals(tag);
  }

  private boolean proccessGrammeme(XMLStreamReader parser,
      Collection<String> grammemes) throws XMLStreamException {
    if (!isStartTag(parser, "g"))
      return false;
    grammemes.add(parser.getAttributeValue(null, "v"));
    gotoCloseTag(parser, "g");
    return true;
  }

  private boolean processLemms(XMLStreamReader parser, Paradigm paradigm,
      Collection<String> constGrammemes) throws XMLStreamException {
    if (!isStartTag(parser, "l"))
      return false;
    paradigm.lemma = parser.getAttributeValue(null, "t");
    parser.nextTag();
    while (proccessGrammeme(parser, constGrammemes))
      parser.nextTag();
    gotoCloseTag(parser, "l");
    return true;
  }

  private boolean processParadigm(XMLStreamReader parser, Paradigm paradigm)
      throws XMLStreamException {
    if (!isStartTag(parser, "lemma"))
      return false;

    String id = parser.getAttributeValue(null, "id");
    paradigm.opencorporaId = id;
    parser.nextTag();

    List<String> constGrammemes = new ArrayList<String>();

    // collect base data

    processLemms(parser, paradigm, constGrammemes);
    parser.nextTag();

    // collect forms data
    while (processWordform(parser, paradigm, constGrammemes))
      parser.nextTag();

    gotoCloseTag(parser, "lemma");
    return true;
  }

  private boolean processWordform(XMLStreamReader parser, Paradigm paradigm,
      Collection<String> constGrammemes) throws XMLStreamException {
    if (!isStartTag(parser, "f"))
      return false;

    List<String> variableGrammemes = new ArrayList<String>();

    String formValue = parser.getAttributeValue(null, "t");

    parser.nextTag();
    while (proccessGrammeme(parser, variableGrammemes))
      parser.nextTag();

    variableGrammemes.addAll(constGrammemes);
    paradigm.addWordform(formValue, variableGrammemes);

    gotoCloseTag(parser, "f");
    return true;
  }

  private void readerMorphDBInternal(InputStream is)
      throws XMLStreamException, IOException {
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader parser = factory.createXMLStreamReader(is);

    while (true) {
      if (parser.getEventType() == XMLStreamConstants.START_ELEMENT
          && parser.getLocalName().equals("lemmata"))
        break;
      parser.next();
    }
    parser.nextTag();

    jsonStartPS();
    SoftReference<Paradigm> paradigm =
        new SoftReference<MorphDBConverter.Paradigm>(new Paradigm());
    int counter = 0;
    while (processParadigm(parser, paradigm.get())) {
      long start = System.nanoTime();

      addParadigm(paradigm.get());
      paradigm.clear();
      counter++;
      parser.nextTag();
      paradigm = new SoftReference<MorphDBConverter.Paradigm>(new Paradigm());

      if (counter > 100) {
        counter = 0;
      }
    }
    jsonEndPS();
    jsonWriteEndingModels();
  }

  void jsonWriteEndingModels() throws IOException {

    jg.writeFieldName("EMS");
    jg.writeStartArray();

    for (Entry<Integer, List<EndingModel>> entrySet : emCache.entrySet()) {
      for (EndingModel em : entrySet.getValue()) {

        jg.writeStartObject();

        jg.writeFieldName("n");
        jg.writeNumber(em.modelNum);
        jg.writeFieldName("es");

        jg.writeStartArray();

        for (Ending e : em.endings) {
          jg.writeStartObject();

          jg.writeFieldName("e");
          jg.writeString(e.value);
          /*
           * jg.writeFieldName("mg"); String gs =
           * joinGrammems(e.grammemes.mainGrammems.values());
           * jg.writeString(gs);
           */
          jg.writeFieldName("ag");
          String gs = joinGrammems(e.grammemes.grammSet);
          jg.writeString(gs);

          jg.writeEndObject();
        }

        jg.writeEndArray();
        jg.writeEndObject();

      }
    }
    jg.writeEndArray();
    jg.writeEndObject();
  }

  private String joinGrammems(Collection<GrammemeEnum> grammemas) {
    StringBuffer sb = new StringBuffer();
    int counter = 0;
    for (GrammemeEnum g : grammemas) {
      if (counter > 0)
        sb.append(",");
      sb.append(g.toString());
      counter++;
    }
    return sb.toString();

  }

  private void jsonStartPS() throws IOException {
    jg.writeStartObject();
    jg.writeFieldName("PS");
    jg.writeStartArray();
  }

  private void jsonEndPS() throws IOException {
    jg.writeEndArray();
  }

  private int endingModelNumber = 0;
  private FileOutputStream fos;
  private JsonFactory jsonFactory;
  private JsonGenerator jg;

  public void readMorphDB() {
    readMorphDB("ru/nlp_project/story_line2/morph/dict.opcorpora.xml");
  }

  public void readMorphDB(String dictResourceName) {
    log = Logger.getLogger(this.getClass().getName());
    try {
      InputStream is = null;
      if (dictResourceName.endsWith("zip")) {
        File uncompressZip = uncompressZip(dictResourceName,
            "corpus/files/export/dict/dict.opcorpora.xml");
        is = new FileInputStream(uncompressZip);
      } else {
        is = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(dictResourceName);
      }
      jsonStart();
      readerMorphDBInternal(is);
      jsonEnd();
      IOUtils.closeQuietly(is);
    } catch (Exception ex) {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }

  void jsonEnd() throws Exception {
    jg.close();
    IOUtils.closeQuietly(fos);
  }

  void jsonStart() throws Exception {
    fos = new FileOutputStream("out.json");
    jsonFactory = new JsonFactory(); // or, for data binding,
    jg = jsonFactory.createGenerator(fos);
  }

  public void readZipMorphDB() {
    readMorphDB("ru/nlp_project/story_line2/morph/dict.opcorpora.xml.zip");
  }

  File uncompressZip(String string, String entry) throws IOException {
    InputStream is = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(string);
    File zipFileCmp = File.createTempFile("morph-dict", ".zip");
    FileOutputStream fos = new FileOutputStream(zipFileCmp);
    zipFileCmp.deleteOnExit();

    IOUtils.copy(is, fos);
    IOUtils.closeQuietly(fos);

    ZipFile zipFile = new ZipFile(zipFileCmp);
    InputStream zipIS = zipFile.getInputStream(zipFile.getEntry(entry));

    File xmlFile = File.createTempFile("morph-dict", ".xml");
    fos = new FileOutputStream(xmlFile);
    IOUtils.copy(zipIS, fos);
    IOUtils.closeQuietly(fos);
    zipFile.close();

    xmlFile.deleteOnExit();

    return xmlFile;
  }

  Paradigm createParadigm() {
    return new Paradigm();
  }

}

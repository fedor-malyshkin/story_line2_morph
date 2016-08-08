package ru.nlp_project.story_line2.morph;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GrammemeUtils {

  public enum GrammemeEnum {
    // pos (0-99)
    adj(0, "прилагательное"), adjf(1, "прилагательное (полное)"), adjs(2,
        "прилагательное (краткое)"), advb(3, "наречие"), comp(4, "компаратив"), conj(
        5, "союз"), grnd(6, "деепричастие"), infn(7, "глагол (инфинитив)"), intj(
        8, "междометие"), noun(9, "существительное"), npro(10,
        "местоимение-существительное"), numr(11, "числительное"), prcl(12,
        "частица"), pred(13, "предикатив"), prep(14, "предлог"), pro(15,
        "местоимение"), prtf(16, "причастие (полное)"), prts(17,
        "причастие (краткое)"), verb(18, "глагол (личная форма)"),
    // case (100-199)
    ablt(100, "творительный падеж"), acc2(101, "второй винительный падеж"), accs(
        102, "винительный падеж"), datv(103, "дательный падеж"), gen1(104,
        "первый родительный падеж"), gen2(105,
        "второй родительный (частичный) падеж"), gent(106, "родительный падеж"), loc1(
        107, "первый предложный падеж"), loc2(108,
        "второй предложный (местный) падеж"), loct(109, "предложный падеж"), nomn(
        110, "именительный падеж"), voct(111, "звательный падеж"),
    // tense (200-299)
    futr(200, "будущее время"), past(201, "прошедшее время"), pres(202,
        "настоящее время"),
    // gender (300-399)
    femn(300, "женский род"), masc(301, "мужской род"), msf(302, "общий род"), neut(
        303, "средний род"),
    // animate (400-499)
    anim(400, "одушевлённое"), inan(401, "неодушевлённое"),
    // number (500-599)
    plur(500, "множественное число"), sing(501, "единственное число"),
    // number (misc) (600-699)
    fixd(600, "неизменяемое"), pltm(601, "pluralia tantum"), sgtm(602,
        "singularia tantum"),
    // aspect (700-799)
    impf(700, "несовершенный вид"), perf(701, "совершенный вид"),
    // transite (800-899)
    impe(800, "безличный"), intr(801, "непереходный"), mult(802, "многократный"), refl(
        803, "возвратный"), tran(804, "переходный"), uimp(805,
        "безличное употребление"),
    // person (900-999)
    per1(900, "1 лицо"), per2(901, "2 лицо"), per3(902, "3 лицо"),
    // mood (1000-1099)
    impr(1000, "повелительное наклонение"), indc(1001,
        "изъявительное наклонение"),
    // involvement (1100-1199)
    excl(1100, "говорящий не включён в действие (иди, идите)"), incl(1101,
        "говорящий включён (идем, идемте)"),
    // voice (1200-1299)
    actv(120, "действительный залог"), pssv(1201, "страдательный залог"),
    // misc (1300-1399)
    abbr(1300, "аббревиатура"), anph(1301, "Анафорическое (местоимение)"), anum(
        1302, "порядковое"), apro(1303, "местоименное"), arch(1304,
        "устаревшее"), coll(1305, "собирательное числительное"), coun(1306,
        "счётная форма"), dist(1307, "искажение"), dmns(1308, "указательное"), erro(
        1309, "опечатка"), fimp(1310,
        "деепричастие от глагола несовершенного вида"), geox(1311, "топоним"), infr(
        1312, "разговорное"), init(1313, "Инициал"), inmx(1314,
        "может использоваться как одуш. / неодуш."), litr(1315,
        "литературный вариант"), name(1316, "имя"), orgn(1317, "организация"), patr(
        1318, "отчество"), poss(1319, "притяжательное"), prdx(1320,
        "может выступать в роли предикатива"), prnt(1321, "вводное слово"), qual(
        1322, "качественное"), ques(1323, "вопросительное"), slng(1324,
        "жаргонное"), subx(7, "возможна субстантивация"), supr(8,
        "превосходная степень"), surn(1325, "фамилия"), trad(1326,
        "торговая марка"), vpre(1327, "Вариант предлога ( со, подо, ...)"), cmp2(
        1328, "Сравнительная степень на 'по-'");


    int index;
    String value;

    private GrammemeEnum(int index, String value) {
      this.index = index;
      this.value = value;
    }

    public int getIndex() {
      return index;
    }

    public String getValue() {
      return value;
    }

  }

  public final static int ANIM_NDX = 4;
  public final static int ASPC_NDX = 7;
  public final static int CASE_NDX = 1;
  public final static Map<GrammemeEnum, Integer> enumToInt =
      new EnumMap<GrammemeEnum, Integer>(GrammemeEnum.class);
  public final static int GNDR_NDX = 3;
  public final static int INVL_NDX = 11;
  public final static int MISC_NDX = 13;
  public final static int MOOD_NDX = 10;
  private static HashMap<String, GrammemeEnum> myTagHash;
  public final static int NMBR_MISC_NDX = 6;
  public final static int NMBR_NDX = 5;
  private static Map<String, GrammemeEnum> openCorporaTagHash = null;
  public final static int PERS_NDX = 9;
  public final static int POS_NDX = 0;
  public final static int TENS_NDX = 2;
  public final static int TRNS_NDX = 8;

  public final static int VOIC_NDX = 12;

  static {
    openCorporaTagHash = new HashMap<String, GrammemeEnum>();
    openCorporaTagHash.put("NOUN", GrammemeEnum.noun); // имя существительное
    openCorporaTagHash.put("ADJF", GrammemeEnum.adjf); // имя прилагательное
                                                       // (полное)
    openCorporaTagHash.put("ADJS", GrammemeEnum.adjs); // имя прилагательное
    // (краткое)
    openCorporaTagHash.put("COMP", GrammemeEnum.comp); // компаратив
    openCorporaTagHash.put("VERB", GrammemeEnum.verb); // глагол (личная форма)
    openCorporaTagHash.put("INFN", GrammemeEnum.infn); // глагол (инфинитив)
    openCorporaTagHash.put("PRTF", GrammemeEnum.prtf); // причастие (полное)
    openCorporaTagHash.put("PRTS", GrammemeEnum.prts); // причастие (краткое)
    openCorporaTagHash.put("GRND", GrammemeEnum.grnd); // деепричастие
    openCorporaTagHash.put("NUMR", GrammemeEnum.numr); // числительное
    openCorporaTagHash.put("ADVB", GrammemeEnum.advb); // наречие
    openCorporaTagHash.put("NPRO", GrammemeEnum.npro); // местоимение-существительное
    openCorporaTagHash.put("PRED", GrammemeEnum.pred); // предикатив
    openCorporaTagHash.put("PREP", GrammemeEnum.prep); // предлог
    openCorporaTagHash.put("CONJ", GrammemeEnum.conj); // союз
    openCorporaTagHash.put("PRCL", GrammemeEnum.prcl); // частица
    openCorporaTagHash.put("INTJ", GrammemeEnum.intj); // междометие

    openCorporaTagHash.put("anim", GrammemeEnum.anim); // одушевлённое
    openCorporaTagHash.put("inan", GrammemeEnum.inan); // неодушевлённое

    openCorporaTagHash.put("masc", GrammemeEnum.masc); // мужской род
    openCorporaTagHash.put("femn", GrammemeEnum.femn); // женский род
    openCorporaTagHash.put("neut", GrammemeEnum.neut); // средний род
    openCorporaTagHash.put("Ms-f", GrammemeEnum.msf); // общий род

    openCorporaTagHash.put("sing", GrammemeEnum.sing); // единственное число
    openCorporaTagHash.put("plur", GrammemeEnum.plur); // множественное число
    openCorporaTagHash.put("Sgtm", GrammemeEnum.sgtm); // singularia tantum
    openCorporaTagHash.put("Pltm", GrammemeEnum.pltm); // pluralia tantum
    openCorporaTagHash.put("Fixd", GrammemeEnum.fixd); // неизменяемое

    openCorporaTagHash.put("nomn", GrammemeEnum.nomn); // именительный падеж
    openCorporaTagHash.put("gent", GrammemeEnum.gent); // родительный падеж
    openCorporaTagHash.put("datv", GrammemeEnum.datv); // дательный падеж
    openCorporaTagHash.put("accs", GrammemeEnum.accs); // винительный падеж
    openCorporaTagHash.put("ablt", GrammemeEnum.ablt); // творительный падеж
    openCorporaTagHash.put("loct", GrammemeEnum.loct); // предложный падеж
    openCorporaTagHash.put("voct", GrammemeEnum.voct); // звательный падеж
    openCorporaTagHash.put("gen1", GrammemeEnum.gen1); // первый родительный
                                                       // падеж
    openCorporaTagHash.put("gen2", GrammemeEnum.gen2); // второй родительный
    // (частичный) падеж
    openCorporaTagHash.put("acc2", GrammemeEnum.acc2); // второй винительный
                                                       // падеж
    openCorporaTagHash.put("loc1", GrammemeEnum.loc1); // первый предложный
                                                       // падеж
    openCorporaTagHash.put("loc2", GrammemeEnum.loc2); // второй предложный
    // (местный) падеж
    openCorporaTagHash.put("Abbr", GrammemeEnum.abbr); // аббревиатура
    openCorporaTagHash.put("Name", GrammemeEnum.name); // имя
    openCorporaTagHash.put("Surn", GrammemeEnum.surn); // фамилия
    openCorporaTagHash.put("Patr", GrammemeEnum.patr); // отчество
    openCorporaTagHash.put("Geox", GrammemeEnum.geox); // топоним
    openCorporaTagHash.put("Orgn", GrammemeEnum.orgn); // организация
    openCorporaTagHash.put("Trad", GrammemeEnum.trad); // торговая марка
    openCorporaTagHash.put("Subx", GrammemeEnum.subx); // возможна
                                                       // субстантивация
    openCorporaTagHash.put("Supr", GrammemeEnum.supr); // превосходная степень
    openCorporaTagHash.put("Qual", GrammemeEnum.qual); // качественное
    openCorporaTagHash.put("Apro", GrammemeEnum.apro); // местоименное
    openCorporaTagHash.put("Anum", GrammemeEnum.anum); // порядковое
    openCorporaTagHash.put("Poss", GrammemeEnum.poss); // притяжательное

    openCorporaTagHash.put("perf", GrammemeEnum.perf); // совершенный вид
    openCorporaTagHash.put("impf", GrammemeEnum.impf); // несовершенный вид

    openCorporaTagHash.put("tran", GrammemeEnum.tran); // переходный
    openCorporaTagHash.put("intr", GrammemeEnum.intr); // непереходный

    openCorporaTagHash.put("Impe", GrammemeEnum.impe); // безличный
    openCorporaTagHash.put("Uimp", GrammemeEnum.uimp); // безличное употребление
    openCorporaTagHash.put("Mult", GrammemeEnum.mult); // многократный
    openCorporaTagHash.put("Refl", GrammemeEnum.refl); // возвратный

    openCorporaTagHash.put("1per", GrammemeEnum.per1); // 1 лицо
    openCorporaTagHash.put("2per", GrammemeEnum.per2); // 2 лицо
    openCorporaTagHash.put("3per", GrammemeEnum.per3); // 3 лицо

    openCorporaTagHash.put("pres", GrammemeEnum.pres); // настоящее время
    openCorporaTagHash.put("past", GrammemeEnum.past); // прошедшее время
    openCorporaTagHash.put("futr", GrammemeEnum.futr); // будущее время

    openCorporaTagHash.put("indc", GrammemeEnum.indc); // изъявительное
                                                       // наклонение
    openCorporaTagHash.put("impr", GrammemeEnum.impr); // повелительное
                                                       // наклонение

    openCorporaTagHash.put("incl", GrammemeEnum.incl); // говорящий включён
                                                       // (идем,
    // идемте)
    openCorporaTagHash.put("excl", GrammemeEnum.excl); // говорящий не включён в
    // действие (иди,
    // идите)
    openCorporaTagHash.put("actv", GrammemeEnum.actv); // действительный залог
    openCorporaTagHash.put("pssv", GrammemeEnum.pssv); // страдательный залог
    openCorporaTagHash.put("Infr", GrammemeEnum.infr); // разговорное
    openCorporaTagHash.put("Slng", GrammemeEnum.slng); // жаргонное
    openCorporaTagHash.put("Arch", GrammemeEnum.arch); // устаревшее
    openCorporaTagHash.put("Litr", GrammemeEnum.litr); // литературный вариант
    openCorporaTagHash.put("Erro", GrammemeEnum.erro); // опечатка
    openCorporaTagHash.put("Dist", GrammemeEnum.dist); // искажение
    openCorporaTagHash.put("Ques", GrammemeEnum.ques); // вопросительное
    openCorporaTagHash.put("Dmns", GrammemeEnum.dmns); // указательное
    openCorporaTagHash.put("Prnt", GrammemeEnum.prnt); // вводное слово
    openCorporaTagHash.put("Fimp", GrammemeEnum.fimp); // деепричастие от
                                                       // глагола
    // несовершенного
    // вида
    openCorporaTagHash.put("Prdx", GrammemeEnum.prdx); // может выступать в роли
    // предикатива
    openCorporaTagHash.put("Coun", GrammemeEnum.coun); // счётная форма
    openCorporaTagHash.put("Coll", GrammemeEnum.coll); // собирательное
                                                       // числительное
    openCorporaTagHash.put("Inmx", GrammemeEnum.inmx); // может использоваться
                                                       // как
    // одуш. / неодуш.
    openCorporaTagHash.put("Vpre", GrammemeEnum.vpre); // Вариант предлога ( со,
    // подо, ...)
    openCorporaTagHash.put("Anph", GrammemeEnum.anph); // Анафорическое
    // (местоимение)
    openCorporaTagHash.put("Init", GrammemeEnum.init); // Инициал
    openCorporaTagHash.put("Cmp2", GrammemeEnum.cmp2); // Сравнительная степень
                                                       // на "по-"
  }

  static {
    myTagHash = new HashMap<String, GrammemeEnum>();
    for (GrammemeEnum en : GrammemeEnum.values()) {
      myTagHash.put(en.getValue(), en);
    }
  }

  public static Set<GrammemeEnum>
      createGrammemesSetByCSVMyTags(String csvTags) {
    String[] split = csvTags.split(",");
    HashSet<GrammemeEnum> result = new HashSet<GrammemeEnum>();
    for (String tag : split) {
      GrammemeEnum enum1 = GrammemeEnum.valueOf(tag.trim());
      result.add(enum1);
    }
    return result;
  }

  public static void fillGrammemesByCSVMyTags(String csvTags,
      Grammemes grammemes) {
    fillGrammemesByCSVMyTags(csvTags, grammemes, false);
  }

  public static void fillGrammemesByCSVMyTags(String csvTags,
      Grammemes grammemes, boolean check) {
    if (check)
      fillGrammemesByCSVMyTags_Check(csvTags, grammemes);
    else
      fillGrammemesByCSVMyTags_NoCheck(csvTags, grammemes);
  }

  private static void fillGrammemesByCSVMyTags_Check(String csvTags,
      Grammemes grammemes) {
    HashSet<Integer> setted = new HashSet<Integer>();
    String[] split = csvTags.split(",");
    for (String tag : split) {
      GrammemeEnum enum1 = GrammemeEnum.valueOf(tag.trim());
      int index = enum1.getIndex() / 100;
      if (setted.contains(index))
        throw new IllegalArgumentException(String.format(
            "В строке '%s' содержится двойное описание свойства слова, второе дублирующееся - %s(%s)",
            csvTags, enum1.toString(), enum1.getValue()));
      setTag(enum1, grammemes);
      setted.add(index);
    }
  }

  private static void fillGrammemesByCSVMyTags_NoCheck(String csvTags,
      Grammemes grammemes) {
    String[] split = csvTags.split(",");
    for (String tag : split) {
      GrammemeEnum enum1 = GrammemeEnum.valueOf(tag.trim());
      setTag(enum1, grammemes);
    }
  }

  public static void fillGrammemesOpencorporaTag(String openCorporaTag,
      Grammemes grammemes) {
    GrammemeEnum myTag = openCorporaTagHash.get(openCorporaTag);
    if (null == myTag)
      return;
    setTag(myTag, grammemes);
  }

  public static void fillGrammemesOpencorporaTags(
      Collection<String> openCorporaTags, Grammemes grammemes) {
    for (String tag : openCorporaTags)
      fillGrammemesOpencorporaTag(tag, grammemes);
  }

  public static void setTag(GrammemeEnum myTag, Grammemes grammemes) {
    // public final static int POS_NDX = 0;
    if (myTag.getIndex() == 0 || (int) (myTag.getIndex() / 100) == POS_NDX) {
      grammemes.mainGrammems.put(POS_NDX, myTag);

      if (myTag == GrammemeEnum.adjf || myTag == GrammemeEnum.adjs)
        grammemes.mainGrammems.put(POS_NDX, GrammemeEnum.adj);

      if (myTag == GrammemeEnum.infn || myTag == GrammemeEnum.prtf
          || myTag == GrammemeEnum.prts || myTag == GrammemeEnum.grnd)
        grammemes.mainGrammems.put(POS_NDX, GrammemeEnum.verb);

      if (myTag == GrammemeEnum.npro)
        grammemes.mainGrammems.put(POS_NDX, GrammemeEnum.pro);

      grammemes.allGrammems.add(myTag);
      // public final static int CASE_NDX = 1;
    } else if ((int) (myTag.getIndex() / 100) == CASE_NDX) {
      grammemes.mainGrammems.put(CASE_NDX, myTag);

      if (myTag == GrammemeEnum.acc2)
        grammemes.mainGrammems.put(CASE_NDX, GrammemeEnum.accs);
      if (myTag == GrammemeEnum.gen1 || myTag == GrammemeEnum.gen2)
        grammemes.mainGrammems.put(CASE_NDX, GrammemeEnum.gent);
      if (myTag == GrammemeEnum.loc1 || myTag == GrammemeEnum.loc2)
        grammemes.mainGrammems.put(CASE_NDX, GrammemeEnum.loct);

      grammemes.allGrammems.add(myTag);
      // public final static int TENS_NDX = 2;
    } else if ((int) (myTag.getIndex() / 100) == TENS_NDX) {
      grammemes.mainGrammems.put(TENS_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int GNDR_NDX = 3;
    } else if ((int) (myTag.getIndex() / 100) == GNDR_NDX) {
      grammemes.mainGrammems.put(GNDR_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int ANIM_NDX = 4;
    } else if ((int) (myTag.getIndex() / 100) == ANIM_NDX) {
      grammemes.mainGrammems.put(ANIM_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int NMBR_NDX = 5;
    } else if ((int) (myTag.getIndex() / 100) == NMBR_NDX) {
      grammemes.mainGrammems.put(NMBR_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int NMBR_MISC_NDX = 6;
    } else if ((int) (myTag.getIndex() / 100) == NMBR_MISC_NDX) {
      grammemes.mainGrammems.put(NMBR_MISC_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int ASPC_NDX = 7;
    } else if ((int) (myTag.getIndex() / 100) == ASPC_NDX) {
      grammemes.mainGrammems.put(ASPC_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int TRNS_NDX = 8;
    } else if ((int) (myTag.getIndex() / 100) == TRNS_NDX) {
      grammemes.mainGrammems.put(TRNS_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int PERS_NDX = 9;
    } else if ((int) (myTag.getIndex() / 100) == PERS_NDX) {
      grammemes.mainGrammems.put(PERS_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int MOOD_NDX = 10;
    } else if ((int) (myTag.getIndex() / 100) == MOOD_NDX) {
      grammemes.mainGrammems.put(MOOD_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int INVL_NDX = 11;
    } else if ((int) (myTag.getIndex() / 100) == INVL_NDX) {
      grammemes.mainGrammems.put(INVL_NDX, myTag);
      grammemes.allGrammems.add(myTag);
      // public final static int VOIC_NDX = 12;
    } else if ((int) (myTag.getIndex() / 100) == VOIC_NDX) {
      grammemes.mainGrammems.put(VOIC_NDX, myTag);
      grammemes.allGrammems.add(myTag);
    } else if ((int) (myTag.getIndex() / 100) > VOIC_NDX) {

      if ((myTag == GrammemeEnum.apro || myTag == GrammemeEnum.anum)
          && grammemes.mainGrammems.get(POS_NDX) == null)
        grammemes.mainGrammems.put(POS_NDX, GrammemeEnum.adj);

      if (myTag == GrammemeEnum.fimp
          && grammemes.mainGrammems.get(POS_NDX) == null)
        grammemes.mainGrammems.put(POS_NDX, GrammemeEnum.verb);

      if (myTag == GrammemeEnum.coll
          && grammemes.mainGrammems.get(POS_NDX) == null)
        grammemes.mainGrammems.put(POS_NDX, GrammemeEnum.numr);

      grammemes.allGrammems.add(myTag);

    }

  }

  public static Grammemes intersect(Grammemes g1, Grammemes g2) {
    Set<GrammemeEnum> allGrammems = EnumSet.noneOf(GrammemeEnum.class);
    allGrammems.addAll(g1.allGrammems);
    allGrammems.retainAll(g2.allGrammems);
    Grammemes result = new Grammemes();
    allGrammems.forEach(g -> setTag(g, result));
    return result;
  }

}

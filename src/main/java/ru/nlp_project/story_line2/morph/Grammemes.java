package ru.nlp_project.story_line2.morph;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import ru.nlp_project.story_line2.morph.GrammemeUtils.GrammemeEnum;

/**
 * Граммемы.
 * 
 * TODO: Заменить 2 контейнера для граммем (allGrammemes, mainGrammemes) на один типа EnumSet<GrammemeEnum>, что позволит выполнять операции фильтрации и булевые опперации.  
 * 
 * 
 * @author fedor
 *
 */
public class Grammemes implements Serializable {

  public static final Collection<GrammemeEnum> MAJOR_CASES =
      Arrays.asList(GrammemeEnum.ablt, GrammemeEnum.accs, GrammemeEnum.datv,
          GrammemeEnum.gent, GrammemeEnum.loct, GrammemeEnum.nomn);
  private static final long serialVersionUID = 5839446194697250302L;
  public static final Collection<GrammemeEnum> ALL_GENDERS =
      Arrays.asList(GrammemeEnum.masc, GrammemeEnum.femn, GrammemeEnum.neut);
  public static final Collection<GrammemeEnum> ALL_NUMBERS =
      Arrays.asList(GrammemeEnum.plur, GrammemeEnum.sing);
  public static final Collection<GrammemeEnum> ALL_TENSES =
      Arrays.asList(GrammemeEnum.futr, GrammemeEnum.past, GrammemeEnum.pres);
  public static final Collection<GrammemeEnum> ALL_GNC =
      Arrays.asList(GrammemeEnum.masc, GrammemeEnum.femn, GrammemeEnum.neut,
          GrammemeEnum.plur, GrammemeEnum.sing, GrammemeEnum.ablt,
          GrammemeEnum.accs, GrammemeEnum.datv, GrammemeEnum.gent,
          GrammemeEnum.loct, GrammemeEnum.nomn);
  public static final Collection<GrammemeEnum> ALL_POS = Arrays.asList(
      GrammemeEnum.adj, GrammemeEnum.adjf, GrammemeEnum.adjs, GrammemeEnum.advb,
      GrammemeEnum.comp, GrammemeEnum.conj, GrammemeEnum.grnd,
      GrammemeEnum.infn, GrammemeEnum.intj, GrammemeEnum.noun,
      GrammemeEnum.npro, GrammemeEnum.numr, GrammemeEnum.prcl,
      GrammemeEnum.pred, GrammemeEnum.prep, GrammemeEnum.pro, GrammemeEnum.prtf,
      GrammemeEnum.prts, GrammemeEnum.verb);
  /**
   * Main grammemes putted in map with appropriate key {@link GrammemeUtils}
   */
  SortedMap<Integer, GrammemeEnum> mainGrammems =
      new TreeMap<Integer, GrammemeEnum>();
  Set<GrammemeEnum> allGrammems = EnumSet.noneOf(GrammemeEnum.class);

  public Grammemes() {
  }

  public Grammemes(Grammemes cloneFrom) {
    mainGrammems.putAll(cloneFrom.mainGrammems);
    allGrammems.addAll(cloneFrom.allGrammems);
  }

  public void addAll(Collection<GrammemeEnum> list) {
    list.forEach(f -> GrammemeUtils.setTag(f, this));
  }

  public Grammemes clone() {
    Grammemes result = new Grammemes();
    result.allGrammems.addAll(this.allGrammems);
    result.mainGrammems.putAll(this.mainGrammems);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Grammemes other = (Grammemes) obj;
    if (allGrammems == null) {
      if (other.allGrammems != null)
        return false;
    } else if (!allGrammems.equals(other.allGrammems))
      return false;
    if (mainGrammems == null) {
      if (other.mainGrammems != null)
        return false;
    } else if (!mainGrammems.equals(other.mainGrammems))
      return false;
    return true;
  }

  public Collection<GrammemeEnum> getAllExGrammems() {
    return allGrammems;
  }

  public GrammemeEnum getByIndex(int index) {
    return mainGrammems.get(index);
  }

  public Collection<GrammemeEnum> getMainGrammems() {
    return mainGrammems.values();
  }

  public GrammemeEnum getPOS() {
    return mainGrammems.get(GrammemeUtils.POS_NDX);
  }

  public boolean has(GrammemeEnum other) {
    return mainGrammems.get(other.getIndex() / 100) == other;
  }

  public boolean hasAll(Collection<GrammemeEnum> other) {
    Iterator<GrammemeEnum> iterator = other.iterator();
    while (iterator.hasNext()) {
      GrammemeEnum grammeme = iterator.next();
      if (mainGrammems.get(grammeme.getIndex() / 100) != grammeme)
        return false;
    }
    return true;
  }

  public boolean hasAll(Grammemes other) {
    Iterator<GrammemeEnum> iterator = other.mainGrammems.values().iterator();
    while (iterator.hasNext()) {
      GrammemeEnum grammeme = iterator.next();
      if (mainGrammems.get(grammeme.getIndex() / 100) != grammeme)
        return false;
    }
    return true;
  }

  public boolean hasAllEx(Collection<GrammemeEnum> other) {
    return allGrammems.containsAll(other);
  }

  public boolean hasAllEx(Grammemes other) {
    return allGrammems.containsAll(other.allGrammems);
  }

  public boolean hasAny(Collection<GrammemeEnum> other) {
    Iterator<GrammemeEnum> iterator = other.iterator();
    while (iterator.hasNext()) {
      GrammemeEnum grammeme = iterator.next();
      if (mainGrammems.get(grammeme.getIndex() / 100) == grammeme)
        return true;
    }
    return false;
  }

  public boolean hasAnyEx(Collection<GrammemeEnum> other) {
    return allGrammems.stream().anyMatch(g -> other.contains(g));
  }

  public boolean hasAnyEx(Grammemes other) {
    return allGrammems.stream().anyMatch(g -> other.allGrammems.contains(g));
  }

  public boolean hasEx(GrammemeEnum other) {
    return allGrammems.contains(other);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((allGrammems == null) ? 0 : allGrammems.hashCode());
    result =
        prime * result + ((mainGrammems == null) ? 0 : mainGrammems.hashCode());
    return result;
  }

  /**
   * 
   * Согласование по падежу: case.
   */
  public boolean isCAgree(Grammemes other) {
    return matchBy(other, GrammemeUtils.CASE_NDX);
  }

  public boolean isEmpty() {
    return allGrammems.isEmpty();
  }

  /**
   * Согласование по роду и падежу: gender case.
   */
  public boolean isGCAgree(Grammemes other) {
    return matchBy(other, GrammemeUtils.GNDR_NDX, GrammemeUtils.CASE_NDX);
  }

  /**
   * Согласование по роду и числу: gender number.
   */
  public boolean isGNAgree(Grammemes other) {
    return matchBy(other, GrammemeUtils.GNDR_NDX, GrammemeUtils.NMBR_NDX);
  }

  /**
   * Проверка согласования по роду, числу и падежу: gender number case.
   */
  public boolean isGNCAgree(Grammemes other) {
    return matchBy(other, GrammemeUtils.GNDR_NDX, GrammemeUtils.NMBR_NDX,
        GrammemeUtils.CASE_NDX);
  }

  /**
   * Согласование по числу и падежу: number case.
   */
  public boolean isNCAgree(Grammemes other) {
    return matchBy(other, GrammemeUtils.NMBR_NDX, GrammemeUtils.CASE_NDX);
  }

  public void leaveOnly(Collection<GrammemeEnum> filter) {
    List<GrammemeEnum> filtred = allGrammems.stream()
        .filter(g -> filter.contains(g)).collect(Collectors.toList());
    allGrammems.clear();
    mainGrammems.clear();
    filtred.forEach(f -> GrammemeUtils.setTag(f, this));
  }

  /**
   * Проверка, что данная граммем является менее специфичным (меньше данных о граммеме) чем параметр.
   */
  public boolean lessSpecificThan(Grammemes other) {
    return other.mainGrammems.values().containsAll(mainGrammems.values());
  }

  /**
   *  Метод потребуется для отработки ограничений типа "GU=&[sg,acc,nom]".
   *  
   *  В данном случае требуется провести анализ того какого рода признаки есть наборе, 
   *  сравнить их с соответствующими признакамии граммем и при совпадении их всех - считать
   *  проверку успешной. 
   * 
   */
  public boolean match(Set<GrammemeEnum> set) {
    boolean result = true;
    // создаем массив индексов для проверки
    HashSet<Integer> indexes = new HashSet<Integer>();
    for (GrammemeEnum e : set)
      indexes.add(e.getIndex() / 100);

    // проходим по индексам для проверки
    for (int i : indexes)
      result &= set.contains(mainGrammems.get(i));

    return result;
  }

  /**
   * 
   * Проверка совпадения с использованием индексов признаков из {@link GrammemeUtils#POS_NDX} и т.д..
   * 
   * Внимание: в случае если у одного из участников сравниваемый 
   * признак отсутствует (null) => считаем их совпадающими. 
   */
  public boolean matchBy(Grammemes other, int... indexes) {
    for (int index : indexes) {
      if (mainGrammems.get(index) == null)
        continue;
      if (other.mainGrammems.get(index) == null)
        continue;
      if (mainGrammems.get(index) != other.mainGrammems.get(index))
        return false;
    }
    return true;
  }

  /**
   * Проверка совпадения части речи граммем с конкретной представленной граммемой.
   */
  public boolean matchPOS(GrammemeEnum other) {
    return mainGrammems.get(GrammemeUtils.POS_NDX) == other;
  }

  /**
   * Проверка, что данная граммем является более специфичным (больше данных о граммеме) чем параметр.
   */
  public boolean moreSpecificThan(Grammemes other) {
    return mainGrammems.values().containsAll(other.mainGrammems.values());

  }

  public void remove(GrammemeEnum other) {
    allGrammems.remove(other);
    mainGrammems.remove(other.getIndex() / 100, other);
  }

  public void setCase(GrammemeEnum cas) {
    allGrammems.remove(mainGrammems.get(GrammemeUtils.CASE_NDX));
    mainGrammems.put(GrammemeUtils.CASE_NDX, cas);
    allGrammems.add(cas);
  }

  public void setNumber(GrammemeEnum nmbr) {
    allGrammems.remove(mainGrammems.get(GrammemeUtils.NMBR_NDX));
    mainGrammems.put(GrammemeUtils.NMBR_NDX, nmbr);
    allGrammems.add(nmbr);
  }

  @Override
  public String toString() {
    return String.format("%s %s",
        mainGrammems.get(GrammemeUtils.POS_NDX) != null
            ? mainGrammems.get(GrammemeUtils.POS_NDX).toString() : null,
        allGrammems != null ? allGrammems.toString() : null);
  }

  public void setPOS(GrammemeEnum pos) {
    mainGrammems.put(GrammemeUtils.POS_NDX, pos);
    allGrammems.removeAll(ALL_POS);
    allGrammems.add(pos);
  }

}

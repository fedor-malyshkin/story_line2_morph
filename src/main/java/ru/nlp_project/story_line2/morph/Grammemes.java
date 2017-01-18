package ru.nlp_project.story_line2.morph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Collectors;

import ru.nlp_project.story_line2.morph.GrammemeUtils.GrammemeEnum;

/**
 * Граммемы.
 * 
 * 
 * @author fedor
 *
 */
@SuppressWarnings("unchecked")
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
					GrammemeEnum.plur, GrammemeEnum.sing, GrammemeEnum.ablt, GrammemeEnum.accs,
					GrammemeEnum.datv, GrammemeEnum.gent, GrammemeEnum.loct, GrammemeEnum.nomn);
	public static final Collection<GrammemeEnum> ALL_POS =
			Arrays.asList(GrammemeEnum.adj, GrammemeEnum.adjf, GrammemeEnum.adjs, GrammemeEnum.advb,
					GrammemeEnum.comp, GrammemeEnum.conj, GrammemeEnum.grnd, GrammemeEnum.infn,
					GrammemeEnum.intj, GrammemeEnum.noun, GrammemeEnum.npro, GrammemeEnum.numr,
					GrammemeEnum.prcl, GrammemeEnum.pred, GrammemeEnum.prep, GrammemeEnum.pro,
					GrammemeEnum.prtf, GrammemeEnum.prts, GrammemeEnum.verb);
	/**
	 * Main grammemes putted in map with appropriate key {@link GrammemeUtils}
	 */
	EnumSet<GrammemeEnum> grammSet = EnumSet.noneOf(GrammemeEnum.class);

	public Grammemes() {}

	public Grammemes(EnumSet<GrammemeEnum> collection) {
		grammSet = EnumSet.copyOf(collection);
	}

	/**
	 * Clone constructor.
	 * 
	 * @param cloneFrom
	 */
	public Grammemes(Grammemes cloneFrom) {
		this(cloneFrom.grammSet);
	}

	public void addAll(Collection<GrammemeEnum> list) {
		list.forEach(f -> GrammemeUtils.setTag(f, this));
	}

	public Grammemes clone() {
		Grammemes result = new Grammemes(this);
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
		if (grammSet == null) {
			if (other.grammSet != null)
				return false;
		} else if (!grammSet.equals(other.grammSet))
			return false;
		return true;
	}

	public GrammemeEnum getByGrammemeGroupIndex(int index) {
		Optional<GrammemeEnum> findFirst =
				grammSet.stream().filter(g -> g.getIndex() / 100 == index).findFirst();
		return findFirst.orElse(null);
	}

	public Collection<GrammemeEnum> getGrammemes() {
		return grammSet;
	}

	public GrammemeEnum getPOS() {
		EnumSet<GrammemeEnum> temp = EnumSet.copyOf(grammSet);
		temp.retainAll(ALL_POS);
		ArrayList<GrammemeEnum> arrayList = new ArrayList<>(temp);
		return arrayList.isEmpty() ? null : arrayList.get(0);
	}



	public boolean has(GrammemeEnum other) {
		return grammSet.contains(other);
	}

	public boolean hasAll(Collection<GrammemeEnum> other) {
		return grammSet.containsAll(other);
	}

	public boolean hasAll(Grammemes other) {
		return hasAll(other.grammSet);
	}

	public boolean hasAny(Collection<GrammemeEnum> other) {
		EnumSet<GrammemeEnum> temp = EnumSet.copyOf(grammSet);
		temp.retainAll(other);
		return !temp.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grammSet == null) ? 0 : grammSet.hashCode());
		return result;
	}

	/**
	 * 
	 * Согласование по падежу: case.
	 */
	public boolean isCAgree(Grammemes other) {
		return matchBy(other, MAJOR_CASES);
	}

	public boolean isEmpty() {
		return grammSet.isEmpty();
	}

	/**
	 * Согласование по роду и падежу: gender case.
	 */
	public boolean isGCAgree(Grammemes other) {
		return matchBy(other, ALL_GENDERS, MAJOR_CASES);
	}

	/**
	 * Согласование по роду и числу: gender number.
	 */
	public boolean isGNAgree(Grammemes other) {
		return matchBy(other, ALL_GENDERS, ALL_NUMBERS);
	}

	/**
	 * Проверка согласования по роду, числу и падежу: gender number case.
	 */
	public boolean isGNCAgree(Grammemes other) {
		return matchBy(other, ALL_GENDERS, ALL_NUMBERS, MAJOR_CASES);
	}

	/**
	 * Согласование по числу и падежу: number case.
	 */
	public boolean isNCAgree(Grammemes other) {
		return matchBy(other, ALL_NUMBERS, MAJOR_CASES);
	}

	public void leaveOnly(Collection<GrammemeEnum> filter) {
		grammSet.retainAll(filter);
	}

	/**
	 * Проверка, что данная граммем является менее специфичным (меньше данных о граммеме) чем
	 * параметр.
	 */
	public boolean lessSpecificThan(Grammemes other) {
		return other.grammSet.containsAll(grammSet);
	}

	/**
	 * Метод потребуется для отработки ограничений типа "GU=&[sg,acc,nom]".
	 * 
	 * В данном случае требуется провести анализ того какого рода признаки есть наборе, сравнить их
	 * с соответствующими признакамии граммем и при совпадении их всех - считать проверку успешной.
	 * 
	 */
	/*
	 * public boolean match(Set<GrammemeEnum> set) { boolean result = true; // создаем массив
	 * индексов для проверки HashSet<Integer> indexes = new HashSet<Integer>(); for (GrammemeEnum e
	 * : set) indexes.add(e.getIndex() / 100);
	 * 
	 * // проходим по индексам для проверки for (int i : indexes) result &=
	 * set.contains(mainGrammems.get(i));
	 * 
	 * return result; }
	 */

	/**
	 * 
	 * Проверка совпадения с использованием индексов признаков из {@link GrammemeUtils#POS_NDX} и
	 * т.д..
	 * 
	 * Внимание: в случае если у одного из участников сравниваемый признак отсутствует (null) =>
	 * считаем их совпадающими (т.е. неуказание признака не является доказательством отличия).
	 */

	public boolean matchBy(Grammemes other, Collection<GrammemeEnum>... filters) {
		for (Collection<GrammemeEnum> filter : filters) {
			EnumSet<GrammemeEnum> otherFiltered = EnumSet.copyOf(other.grammSet);
			otherFiltered.retainAll(filter);
			EnumSet<GrammemeEnum> selfFiltered = EnumSet.copyOf(grammSet);
			selfFiltered.retainAll(filter);

			// Внимание: в случае если у одного из участников сравниваемый признак отсутствует
			// (null) => считаем их совпадающими (т.е. неуказание признака не является
			// доказательством отличия).
			if (selfFiltered.isEmpty() || otherFiltered.isEmpty())
				continue;
			if (!selfFiltered.containsAll(otherFiltered))
				return false;
		}
		return true;
	}

	/**
	 * Проверка совпадения части речи граммем с конкретной представленной граммемой.
	 */
	public boolean matchPOS(GrammemeEnum otherPos) {
		return grammSet.contains(otherPos);
	}

	/**
	 * Проверка, что данная граммем является более специфичным (больше данных о граммеме) чем
	 * параметр.
	 */
	public boolean moreSpecificThan(Grammemes other) {
		return grammSet.containsAll(other.grammSet);

	}

	public void remove(GrammemeEnum other) {
		grammSet.remove(other);
	}

	public void setCase(GrammemeEnum cas) {
		grammSet.removeAll(MAJOR_CASES);
		grammSet.add(cas);
	}

	public void setNumber(GrammemeEnum nmbr) {
		grammSet.removeAll(ALL_NUMBERS);
		grammSet.add(nmbr);
	}

	public void setPOS(GrammemeEnum pos) {
		grammSet.removeAll(ALL_POS);
		grammSet.add(pos);
	}



	@Override
	public String toString() {
		return grammSet.stream().sorted().map((f) -> f.toString())
				.collect(Collectors.joining(", "));
	}

}

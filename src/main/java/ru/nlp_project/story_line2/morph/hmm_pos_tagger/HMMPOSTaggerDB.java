package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import java.util.HashMap;
import java.util.Map;

import ru.nlp_project.story_line2.morph.GrammemeEnum;

/**
 * 
 * База данных "Hidden Markov Model POS-Tagger".
 * 
 * Простой контейнер для статистики, весь расчёт осуществляется в построителе
 * ({@link IHMMPOSTaggerDBBuilder}).
 * <p>
 * Единетсвенная неочевидная вещб -- это сглаживание Лапласа при запросе отсуствующих комбинаций
 * слов или тэгов.
 * 
 * @author fedor
 *
 */
public class HMMPOSTaggerDB {

	private Map<GrammemeEnum, Double> startStatePropability = new HashMap<>();
	private Map<GrammemePair, Double> biGrammPropability = new HashMap<>();
	private Map<WordGrammemePair, Double> observationStatePropability = new HashMap<>();
	private Map<GrammemeEnum, Integer> allStateStats = new HashMap<>();

	void addAllStateStats(Map<GrammemeEnum, Integer> allStateStats) {
		this.allStateStats = allStateStats;
	}

	void addBiGrammPropability(GrammemePair k, double prob) {
		biGrammPropability.put(k, prob);
	}

	void addObservationStatePropability(WordGrammemePair k, double prob) {
		observationStatePropability.put(k, prob);
	}

	void addStartStatePropability(GrammemeEnum k, double prob) {
		startStatePropability.put(k, prob);
	}

	 Map<GrammemeEnum, Integer> getAllStateStats() {
		return allStateStats;
	}

	Map<GrammemePair, Double> getBiGrammPropability() {
		return biGrammPropability;
	}

	 double getBiGrammPropability(GrammemeEnum curr, GrammemeEnum prev) {
		if (curr == null)
			throw new IllegalArgumentException("POS must be not null");
		if (prev == null)
			throw new IllegalArgumentException("POS must be not null");

		return biGrammPropability.get(new GrammemePair(curr, prev));
	}

	Map<WordGrammemePair, Double> getObservationStatePropability() {
		return observationStatePropability;
	}

	 double getObservationStatePropability(String word, GrammemeEnum pos) {
		if (word == null || word.isEmpty())
			throw new IllegalArgumentException("word must be not null/empty");
		if (pos == null)
			throw new IllegalArgumentException("POS must be not null");

		Double result =
				observationStatePropability.get(new WordGrammemePair(word.toLowerCase(), pos));
		if (null != result)
			return result;
		else
			return 1f / allStateStats.getOrDefault(pos, Integer.MAX_VALUE);

	}

	Map<GrammemeEnum, Double> getStartStatePropability() {
		return startStatePropability;
	}

	double getStartStatePropability(GrammemeEnum k) {
		if (k == null)
			throw new IllegalArgumentException("POS must be not null");
		return startStatePropability.get(k);
	}

	void setAllStateStats(Map<GrammemeEnum, Integer> allStateStats) {
		this.allStateStats = allStateStats;
	}

	void setBiGrammPropability(Map<GrammemePair, Double> biGrammPropability) {
		this.biGrammPropability = biGrammPropability;
	}

	void setObservationStatePropability(Map<WordGrammemePair, Double> observationStatePropability) {
		this.observationStatePropability = observationStatePropability;
	}

	void setStartStatePropability(Map<GrammemeEnum, Double> startStatePropability) {
		this.startStatePropability = startStatePropability;
	}

}

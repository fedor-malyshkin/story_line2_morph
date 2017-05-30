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

	private Map<GrammemeEnum, Float> startStatePropability = new HashMap<>();
	private Map<GrammemePair, Float> biGrammPropability = new HashMap<>();
	private Map<WordGrammemePair, Float> observationStatePropability = new HashMap<>();
	private Map<GrammemeEnum, Integer> allStateStats = new HashMap<>();

	void allStateStats(Map<GrammemeEnum, Integer> allStateStats) {
		this.allStateStats = allStateStats;
	}

	void biGrammPropability(GrammemePair k, float prob) {
		biGrammPropability.put(k, prob);
	}

	Map<GrammemeEnum, Integer> getAllStateStats() {
		return allStateStats;
	}

	Map<GrammemePair, Float> getBiGrammPropability() {
		return biGrammPropability;
	}

	public float getBiGrammPropability(GrammemeEnum curr, GrammemeEnum prev) {
		if (curr == null)
			throw new IllegalArgumentException("POS must be not null");
		if (prev == null)
			throw new IllegalArgumentException("POS must be not null");

		return biGrammPropability.get(new GrammemePair(curr, prev));
	}

	Map<WordGrammemePair, Float> getObservationStatePropability() {
		return observationStatePropability;
	}

	public float getObservationStatePropability(String word, GrammemeEnum pos) {
		if (word == null || word.isEmpty())
			throw new IllegalArgumentException("word must be not null/empty");
		if (pos == null)
			throw new IllegalArgumentException("POS must be not null");

		Float result =
				observationStatePropability.get(new WordGrammemePair(word.toLowerCase(), pos));
		if (null != result)
			return result;
		else
			return 1f / allStateStats.getOrDefault(pos, Integer.MAX_VALUE);

	}

	Map<GrammemeEnum, Float> getStartStatePropability() {
		return startStatePropability;
	}

	public float getStartStatePropability(GrammemeEnum k) {
		if (k == null)
			throw new IllegalArgumentException("POS must be not null");
		return startStatePropability.get(k);
	}

	void observationStatePropability(WordGrammemePair k, float prob) {
		observationStatePropability.put(k, prob);
	}

	void setAllStateStats(Map<GrammemeEnum, Integer> allStateStats) {
		this.allStateStats = allStateStats;
	}

	void setBiGrammPropability(Map<GrammemePair, Float> biGrammPropability) {
		this.biGrammPropability = biGrammPropability;
	}

	void setObservationStatePropability(Map<WordGrammemePair, Float> observationStatePropability) {
		this.observationStatePropability = observationStatePropability;
	}

	void setStartStatePropability(Map<GrammemeEnum, Float> startStatePropability) {
		this.startStatePropability = startStatePropability;
	}

	void startStatePropability(GrammemeEnum k, float prob) {
		startStatePropability.put(k, prob);
	}

}

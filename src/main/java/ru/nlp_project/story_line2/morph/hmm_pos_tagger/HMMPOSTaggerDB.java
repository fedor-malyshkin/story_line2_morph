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

	public void startStatePropability(GrammemeEnum k, float prob) {
		startStatePropability.put(k, prob);
	}

	public void biGrammPropability(GrammemePair k, float prob) {
		biGrammPropability.put(k, prob);
	}

	public void observationStatePropability(WordGrammemePair k, float prob) {
		observationStatePropability.put(k, prob);
	}

	public float getStartStatePropability(GrammemeEnum k) {
		if (k == null)
			throw new IllegalArgumentException("POS must be not null");
		return startStatePropability.get(k);
	}

	public float getBiGrammPropability(GrammemeEnum curr, GrammemeEnum prev) {
		if (curr == null)
			throw new IllegalArgumentException("POS must be not null");
		if (prev == null)
			throw new IllegalArgumentException("POS must be not null");

		return biGrammPropability.get(new GrammemePair(curr, prev));
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
		return 1f / allStateStats.getOrDefault(pos, Integer.MAX_VALUE);

	}

	public void allStateStats(Map<GrammemeEnum, Integer> allStateStats) {
		this.allStateStats = allStateStats;
	}


}

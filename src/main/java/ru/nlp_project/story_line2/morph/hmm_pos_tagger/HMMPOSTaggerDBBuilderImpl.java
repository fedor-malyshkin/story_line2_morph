package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.nlp_project.story_line2.morph.GrammemeEnum;
import ru.nlp_project.story_line2.morph.Grammemes;

public class HMMPOSTaggerDBBuilderImpl implements IHMMPOSTaggerDBBuilder {
	/**
	 * статистика по стартовым позициям
	 */
	private Map<GrammemeEnum, Integer> startStateStats = new HashMap<>();
	/**
	 * статистика по вхождениям частей речи в предложение (кроме последнего).
	 */
	private Map<GrammemeEnum, Integer> noLastStateStats = new HashMap<>();

	/**
	 * статистика по вхождениям биграмм частей речи в предложение.
	 */
	private Map<GrammemePair, Integer> biGrammStats = new HashMap<>();
	/**
	 * статистика по вхождениям биграмм частей речи в предложение.
	 */
	private Map<WordGrammemePair, Integer> observationStateStats = new HashMap<>();

	/**
	 * наблюдения (слова)
	 */
	private List<String> observations = new ArrayList<>();
	/**
	 * состояния (pos)
	 */
	private List<GrammemeEnum> states = new ArrayList<>();
	/**
	 * база данных таггера
	 */
	private HMMPOSTaggerDB db = new HMMPOSTaggerDB();
	/**
	 * статистика по вхождениям частей речи в предложение (все).
	 */
	private Map<GrammemeEnum, Integer> allStateStats = new HashMap<>();


	@Override
	public void addTokent(String word, GrammemeEnum pos) {
		if (word == null || word.isEmpty())
			throw new IllegalArgumentException("word must be not null/empty");
		if (pos == null)
			throw new IllegalArgumentException("POS must be not null");
		observations.add(word.toLowerCase());
		states.add(pos);
	}

	private void calculateStatistics() {
		for (int i = 0; i < observations.size(); i++) {
			String word = observations.get(i);
			GrammemeEnum pos = states.get(i);
			// статистика вообще по наблюдениям/состояниям
			incObseravtionStateStats(word, pos);
			// статистика вообще по вхождениям в предложение (кроме последнего)
			incAllStateStats(pos);
			if (i == 0)
				// статистика по стартовым сиволам
				incStartStateStats(pos);
			if (i > 0)
				// статистика по биграммам
				incBiGrammStats(pos, states.get(i - 1));
			if (i < states.size() - 1)
				// статистика вообще по вхождениям в предложение (кроме последнего)
				incNoLastStateStats(pos);
		}
	}

	private void incObseravtionStateStats(String word, GrammemeEnum pos) {
		WordGrammemePair pair = new WordGrammemePair(word, pos);
		inc(observationStateStats, pair);
	}


	private <T> void inc(Map<T, Integer> map, T key) {
		Integer counter = map.getOrDefault(key, 0);
		map.put(key, counter + 1);
	}

	private void incBiGrammStats(GrammemeEnum curr, GrammemeEnum prev) {
		GrammemePair pair = new GrammemePair(curr, prev);
		inc(biGrammStats, pair);
	}

	private void incNoLastStateStats(GrammemeEnum pos) {
		inc(noLastStateStats, pos);
	}


	private void incAllStateStats(GrammemeEnum pos) {
		inc(allStateStats, pos);
	}



	private void incStartStateStats(GrammemeEnum pos) {
		inc(startStateStats, pos);
	}

	@Override
	public void endLearning() {
		fillDbWithStatistics();
	}

	@Override
	public void endSentence() {
		calculateStatistics();
	}

	private void fillDbWithStatistics() {
		// P(St) if t=0 (перебором всех частей речи решаем ситуацию с пропущенными частями в
		// обучающей выборке)
		double denom = startStateStats.values().stream().reduce(0, Integer::sum);
		Grammemes.ALL_POS.forEach(pos -> {
			Integer count = startStateStats.get(pos);
			// в случае отсуствия статистики -- выставляем малую вероятность
			if (null != count)
				db.addStartStatePropability(pos, (double) count / denom);
			else
				db.addStartStatePropability(pos, (double) 1 / Integer.MAX_VALUE);
		});


		// P (St|St-1) (перебором всех частей речи решаем ситуацию с пропущенными частями в
		// обучающей выборке)
		Grammemes.ALL_POS.forEach(posCurr -> {
			Grammemes.ALL_POS.forEach(posPrev -> {
				GrammemePair pair = new GrammemePair(posCurr, posPrev);
				Integer count = biGrammStats.get(pair);
				// в случае отсуствия статистики -- выставляем малую вероятность
				if (null != count) {
					double den = noLastStateStats.getOrDefault(posPrev, 1);
					db.addBiGrammPropability(pair, (double) count / den);
				} else
					db.addBiGrammPropability(pair, (double) 1 / Integer.MAX_VALUE);
			});
		});

		// P(obs|state)
		observationStateStats.forEach((k, v) -> {
			double den = allStateStats.getOrDefault(k.getPos(), 1);
			db.addObservationStatePropability(k, (double) v / den);
		});
		db.addAllStateStats(allStateStats);


	}

	@Override
	public void startLearning() {
		db = new HMMPOSTaggerDB();
	}

	@Override
	public void startSentence() {
		observations.clear();
		states.clear();
	}

	public HMMPOSTaggerDB getHMMPOSTaggerDB() {
		return db;
	}



}

package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import ru.nlp_project.story_line2.morph.GrammemeEnum;

/**
 * Иньерфейс постороителя базы данных для "Hidden Markov Model POS-Tagger".
 * 
 * @author fedor
 *
 */
public interface IHMMPOSTaggerDBBuilder {
	void addTokent(String word, GrammemeEnum pos);

	void endLearning();

	/**
	 * Важный метод для вызова -- осуществляется накопление статистики (особенно стартовых
	 * состояний).
	 */
	void endSentence();

	void startLearning();

	void startSentence();
}

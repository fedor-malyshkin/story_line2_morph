package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import java.util.List;

/**
 * Hidden Markov Model POS-Tagger.
 * <p/>
 * Для описания см.: <a href="https://en.wikipedia.org/wiki/Viterbi_algorithm">Viterbi algorithm</a>
 * 
 * @author fedor
 *
 */
public class HMMPOSTagger {


	public interface IEmissionPropabilityFacade {

		/**
		 * Выдать probability of observing <code>word</code> from state <code>state</code>.
		 * 
		 * @param state
		 * @param word
		 * @return
		 */
		double getEP(int state, String word);

	}

	public interface IStartPropabilityFacade {
		double getSP(int state);

	}

	public interface ITransmissionPropabilityFacade {

		double getTP(int state, int next_state);

	}

	/**
	 * Вертикальный срез матрицы из алгоритма.
	 * <p>
	 * Количество объектов павно количеству состояний.
	 * 
	 * @author fedor
	 *
	 */
	private class TNode {
		/**
		 * Путь из индексов состояний.
		 */
		public int[] vPath;
		/**
		 * Итоговаяя вероятность пути.
		 */
		public double vPropb;

		public TNode(int[] vPath, double vPropb) {
			this.vPath = copyIntArray(vPath);
			this.vPropb = vPropb;
		}
	}

	/**
	 * Сделать копию массива.
	 * 
	 * @param ia
	 * @return
	 */
	private int[] copyIntArray(int[] ia) {
		int[] newIa = new int[ia.length];
		for (int i = 0; i < ia.length; i++) {
			newIa[i] = ia[i];
		}
		return newIa;
	}

	/**
	 * Сделать копию массива (а последний элемент сделать равным последнему параметру).
	 * 
	 * @param ia
	 * @param newInt
	 * @return
	 */
	private int[] copyIntArray(int[] ia, int newInt) {
		int[] newIa = new int[ia.length + 1];
		for (int i = 0; i < ia.length; i++) {
			newIa[i] = ia[i];
		}
		newIa[ia.length] = newInt;
		return newIa;
	}

	/**
	 * 
	 * 
	 * @param observations
	 * @param states
	 * @param startPropability
	 * @param transitionPropability
	 * @param emissionPropability
	 * @param transitionPropability
	 * @return массив индексов состояний наиболее вероятной последовательности состояний.
	 */
	int[] forwardViterbi(List<String> observations, int statesCount,
			IStartPropabilityFacade startPropability,
			IEmissionPropabilityFacade emissionPropability,
			ITransmissionPropabilityFacade transitionPropability) {
		TNode[] T = new TNode[statesCount];
		for (int state = 0; state < statesCount; state++) {
			int[] intArray = new int[1];
			intArray[0] = state;
			T[state] = new TNode(intArray, startPropability.getSP(state)
					* emissionPropability.getEP(state, observations.get(0)));
		}

		for (int output = 1; output < observations.size(); output++) {
			String currWord = observations.get(output);
			TNode[] U = new TNode[statesCount];
			for (int next_state = 0; next_state < statesCount; next_state++) {
				int[] argmax = new int[0];
				double valmax = 0;
				for (int state = 0; state < statesCount; state++) {
					int[] v_path = copyIntArray(T[state].vPath);
					double v_prob = T[state].vPropb;
					double p = emissionPropability.getEP(next_state, currWord)
							* transitionPropability.getTP(state, next_state);
					v_prob *= p;
					if (v_prob > valmax) {
						if (v_path.length == observations.size()) {
							argmax = copyIntArray(v_path);
						} else {
							argmax = copyIntArray(v_path, next_state);
						}
						valmax = v_prob;

					}
				}
				U[next_state] = new TNode(argmax, valmax);
			}
			T = U;
		}
		// apply sum/max to the final states:
		int[] argmax = new int[0];
		double valmax = 0;
		for (int state = 0; state < statesCount; state++) {
			int[] v_path = copyIntArray(T[state].vPath);
			double v_prob = T[state].vPropb;
			if (v_prob > valmax) {
				argmax = copyIntArray(v_path);
				valmax = v_prob;
			}
		}
		return argmax;
	}


}

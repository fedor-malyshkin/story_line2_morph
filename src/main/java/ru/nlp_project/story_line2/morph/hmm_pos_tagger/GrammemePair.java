package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import ru.nlp_project.story_line2.morph.GrammemeEnum;

/**
 * Биграмма состояний (токенов).
 * 
 * @author fedor
 *
 */
class GrammemePair {
	private GrammemeEnum curr;
	private GrammemeEnum prev;

	public GrammemePair(GrammemeEnum curr, GrammemeEnum prev) {
		super();
		this.curr = curr;
		this.prev = prev;
	}

	public GrammemeEnum getCurr() {
		return curr;
	}

	public GrammemeEnum getPrev() {
		return prev;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((curr == null) ? 0 : curr.hashCode());
		result = prime * result + ((prev == null) ? 0 : prev.hashCode());
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
		GrammemePair other = (GrammemePair) obj;
		if (curr != other.curr)
			return false;
		if (prev != other.prev)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + curr + ", " + prev + "]";
	}



}

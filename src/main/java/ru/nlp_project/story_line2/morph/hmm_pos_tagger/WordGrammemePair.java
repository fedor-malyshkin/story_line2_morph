package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import ru.nlp_project.story_line2.morph.GrammemeEnum;

public class WordGrammemePair {
	private String word;


	private GrammemeEnum pos;

	public WordGrammemePair(String word, GrammemeEnum pos) {
		super();
		this.word = word;
		this.pos = pos;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordGrammemePair other = (WordGrammemePair) obj;
		if (pos != other.pos)
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	public GrammemeEnum getPos() {
		return pos;
	}



	public String getWord() {
		return word;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "[" + word + ", " + pos + "]";
	}


}

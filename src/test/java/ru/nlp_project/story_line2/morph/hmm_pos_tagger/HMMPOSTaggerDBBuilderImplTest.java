package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import ru.nlp_project.story_line2.morph.GrammemeEnum;

public class HMMPOSTaggerDBBuilderImplTest {

	private HMMPOSTaggerDBBuilderImpl testable;

	@Before
	public void setUp() {
		testable = new HMMPOSTaggerDBBuilderImpl();
		testable.startLearning();

	}

	@Test
	public void testAddTokent() {
		testable.startSentence();
		testable.addTokent("слово", GrammemeEnum.noun);
		testable.addTokent("слово", GrammemeEnum.noun);
		testable.addTokent("слово", GrammemeEnum.verb);
		testable.addTokent("сталь", GrammemeEnum.verb);

		testable.endSentence();
		testable.endLearning();

		HMMPOSTaggerDB taggerDB = testable.getHMMPOSTaggerDB();
		assertThat(taggerDB.getStartStatePropability(GrammemeEnum.noun)).isEqualTo(1);
		// вероятность "слова" при verb
		assertThat(taggerDB.getObservationStatePropability("слово", GrammemeEnum.verb))
				.isEqualTo(0.5f);
		// P (noun|noun) (не всегда после noun идёт noun)
		assertThat(taggerDB.getBiGrammPropability(GrammemeEnum.noun, GrammemeEnum.noun))
				.isEqualTo(0.5f);
		// P (verb|verb) (помле verb всегда был verb)
		assertThat(taggerDB.getBiGrammPropability(GrammemeEnum.verb, GrammemeEnum.verb))
				.isEqualTo(1f);
	}

	@Test
	public void testSmoothForUnknonwnTokens() {
		IntStream.range(0, 100).forEach(i -> {
			testable.startSentence();
			testable.addTokent("слово", GrammemeEnum.noun);
			testable.addTokent("слово", GrammemeEnum.noun);
			testable.addTokent("слово", GrammemeEnum.verb);
			testable.endSentence();
		});

		testable.endLearning();

		HMMPOSTaggerDB taggerDB = testable.getHMMPOSTaggerDB();
		// present
		assertThat(taggerDB.getStartStatePropability(GrammemeEnum.noun)).isEqualTo(1);
		// absent
		assertThat(taggerDB.getStartStatePropability(GrammemeEnum.adj)).isEqualTo(0.01f);

		// absent
		assertThat(taggerDB.getObservationStatePropability("дом", GrammemeEnum.verb))
				.isEqualTo(0.01f);
		
		// absent
		assertThat(taggerDB.getBiGrammPropability(GrammemeEnum.verb, GrammemeEnum.verb))
				.isEqualTo(1f/12);
		
		

	}

}

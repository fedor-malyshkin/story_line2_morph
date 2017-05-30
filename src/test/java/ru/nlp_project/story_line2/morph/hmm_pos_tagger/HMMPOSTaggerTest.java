package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import ru.nlp_project.story_line2.morph.GrammemeEnum;
import ru.nlp_project.story_line2.morph.hmm_pos_tagger.HMMPOSTagger.IEmissionPropabilityFacade;
import ru.nlp_project.story_line2.morph.hmm_pos_tagger.HMMPOSTagger.IStartPropabilityFacade;
import ru.nlp_project.story_line2.morph.hmm_pos_tagger.HMMPOSTagger.ITransmissionPropabilityFacade;

public class HMMPOSTaggerTest {

	/**
	 * Пример из страницы Wiki с городом и доктором
	 * (https://en.wikipedia.org/wiki/Viterbi_algorithm).
	 * 
	 */
	@Test
	public void testForwardViterbi_FromWiki() {
		HMMPOSTagger testable = new HMMPOSTagger(null);
		List<String> statesList = Arrays.asList("Healthy", "Fever");

		Map<String, Double> startPropb = new HashMap<String, Double>();
		startPropb.put("Healthy", 0.6);
		startPropb.put("Fever", 0.4);

		Map<String, Map<String, Double>> transPropb = new HashMap<String, Map<String, Double>>();
		Map<String, Double> hTransPropb = new HashMap<String, Double>();
		hTransPropb.put("Healthy", 0.7);
		hTransPropb.put("Fever", 0.3);
		transPropb.put("Healthy", hTransPropb);
		Map<String, Double> fTransPropb = new HashMap<String, Double>();
		fTransPropb.put("Healthy", 0.4);
		fTransPropb.put("Fever", 0.6);
		transPropb.put("Fever", fTransPropb);

		Map<String, Map<String, Double>> emissPropb = new HashMap<String, Map<String, Double>>();
		Map<String, Double> hEmissPropb = new HashMap<String, Double>();
		hEmissPropb.put("normal", 0.5);
		hEmissPropb.put("cold", 0.4);
		hEmissPropb.put("dizzy", 0.1);
		emissPropb.put("Healthy", hEmissPropb);
		Map<String, Double> fEmissPropb = new HashMap<String, Double>();
		fEmissPropb.put("normal", 0.1);
		fEmissPropb.put("cold", 0.3);
		fEmissPropb.put("dizzy", 0.6);
		emissPropb.put("Fever", fEmissPropb);

		IStartPropabilityFacade startPropability = new IStartPropabilityFacade() {
			@Override
			public double getSP(int state) {
				String string = statesList.get(state);
				return startPropb.get(string);
			}
		};


		IEmissionPropabilityFacade emissionPropability = new IEmissionPropabilityFacade() {
			@Override
			public double getEP(int state, String word) {
				String string = statesList.get(state);
				Map<String, Double> map = emissPropb.get(string);
				return map.get(word);
			}
		};


		ITransmissionPropabilityFacade transitionPropability =
				new ITransmissionPropabilityFacade() {
					@Override
					public double getTP(int state, int next_state) {
						String string1 = statesList.get(state);
						String string2 = statesList.get(next_state);
						Map<String, Double> map = transPropb.get(string1);
						return map.get(string2);
					}
				};
		List<String> obs = Arrays.asList("normal", "cold", "dizzy");

		int[] states = testable.forwardViterbi(obs, statesList.size(), startPropability,
				emissionPropability, transitionPropability);

		assertThat(states).isEqualTo(new int[] {0, 0, 1});

	}

	@Test
	public void testForwardViterbiSimpleText() throws IOException {
		// read db
		InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(
				"ru/nlp_project/story_line2/morph/hmm_pos_tagger/annot.opcorpora.no_ambig.xml.zip");
		File tempFile = File.createTempFile("test", "zip");
		FileOutputStream fos = new FileOutputStream(tempFile);
		IOUtils.copy(stream, fos);
		HMMPOSTaggerDBBuilderImpl dbBuilder = new HMMPOSTaggerDBBuilderImpl();
		OpencorporaMarkupReader markupReader = new OpencorporaMarkupReader(dbBuilder);
		markupReader.readZippedMarkupFile(tempFile);
		HMMPOSTaggerDB taggerDB = dbBuilder.getHMMPOSTaggerDB();
		// run test
		HMMPOSTagger testable = new HMMPOSTagger(taggerDB);
		List<String> obs = Arrays.asList("Мы", "стали", ",прыгать");
		List<GrammemeEnum> states = testable.forwardViterbi(obs);
		assertThat(states)
				.isEqualTo(Arrays.asList(GrammemeEnum.npro, GrammemeEnum.verb, GrammemeEnum.infn));
	}


}

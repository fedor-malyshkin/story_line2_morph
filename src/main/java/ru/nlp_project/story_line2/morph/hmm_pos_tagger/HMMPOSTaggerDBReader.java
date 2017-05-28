package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import ru.nlp_project.story_line2.morph.GrammemeEnum;

/**
 * Класс для чтения с диска базы данных "Hidden Markov Model POS-Tagger".
 * 
 * @author fedor
 *
 */
public class HMMPOSTaggerDBReader {
	private HMMPOSTaggerDB db;
	private JsonParser parser;

	HMMPOSTaggerDB read(File file) throws IOException {
		db = new HMMPOSTaggerDB();
		FileInputStream fis = new FileInputStream(file);
		readIntrnal(fis);
		IOUtils.closeQuietly(fis);
		return db;
	}

	private void readAllStateStats() throws IOException {
		JsonToken token = parser.nextToken(); // field_name
		if (token != JsonToken.FIELD_NAME || !parser.getText().equalsIgnoreCase("all-state-stats"))
			throw new IllegalStateException("incorrect POS tagger DB.");
		parser.nextToken(); // skip field_name
		parser.nextToken(); // skip start_array

		Map<GrammemeEnum, Integer> allStateStats = new HashMap<>();
		while (readAllStateStatsEntry(allStateStats))
			parser.nextToken(); // skip end_object
		db.setAllStateStats(allStateStats);
	}

	private boolean readAllStateStatsEntry(Map<GrammemeEnum, Integer> allStateStats)
			throws IOException {
		if (parser.getCurrentToken() != JsonToken.START_OBJECT)
			return false;
		// goto pos
		parser.nextToken();
		// goto pos value
		parser.nextToken();
		String posText = parser.getText();
		GrammemeEnum pos = GrammemeEnum.valueOf(posText);

		// goto count
		parser.nextToken();
		// goto count value
		parser.nextToken();
		int intValue = parser.getIntValue();
		// skip propb value
		parser.nextToken();

		allStateStats.put(pos, intValue);
		return true;
	}

	private void readBiGrammPropability() throws IOException {
		JsonToken token = parser.nextToken(); // field_name
		if (token != JsonToken.FIELD_NAME || !parser.getText().equalsIgnoreCase("bi-gramm-propb"))
			throw new IllegalStateException("incorrect POS tagger DB.");
		parser.nextToken(); // skip field_name
		parser.nextToken(); // skip start_array

		Map<GrammemePair, Float> biGrammPropability = new HashMap<>();
		while (readBiGrammPropabilityEntry(biGrammPropability))
			parser.nextToken(); // skip end_object
		db.setBiGrammPropability(biGrammPropability);
	}

	private boolean readBiGrammPropabilityEntry(Map<GrammemePair, Float> biGrammPropability)
			throws IOException {
		if (parser.getCurrentToken() != JsonToken.START_OBJECT)
			return false;
		// goto curr
		parser.nextToken();
		// goto curr value
		parser.nextToken();
		String currText = parser.getText();
		GrammemeEnum curr = GrammemeEnum.valueOf(currText);

		// goto prev
		parser.nextToken();
		// goto prev value
		parser.nextToken();
		String prevText = parser.getText();
		GrammemeEnum prev = GrammemeEnum.valueOf(prevText);
		// goto propb
		parser.nextToken();
		// goto propb value
		parser.nextToken();
		float floatValue = parser.getFloatValue();
		// skip propb value
		parser.nextToken();

		biGrammPropability.put(new GrammemePair(curr, prev), floatValue);
		return true;
	}

	private void readIntrnal(FileInputStream fis) throws IOException {
		JsonFactory jsonFactory = new JsonFactory(); // or, for data binding,
		parser = jsonFactory.createParser(fis);

		skipPrefix();
		readStartStateStats();
		readObservationStateStats();
		readBiGrammPropability();
		readAllStateStats();
	}

	private void readObservationStateStats() throws IOException {
		JsonToken token = parser.nextToken(); // field_name
		if (token != JsonToken.FIELD_NAME || !parser.getText().equalsIgnoreCase("obs-state-propb"))
			throw new IllegalStateException("incorrect POS tagger DB.");
		parser.nextToken(); // skip field_name
		parser.nextToken(); // skip start_array

		Map<WordGrammemePair, Float> observationStatePropability = new HashMap<>();
		while (readObservationStateStatsEntry(observationStatePropability))
			parser.nextToken(); // skip end_object
		db.setObservationStatePropability(observationStatePropability);
	}

	private boolean readObservationStateStatsEntry(
			Map<WordGrammemePair, Float> observationStatePropability) throws IOException {
		if (parser.getCurrentToken() != JsonToken.START_OBJECT)
			return false;
		// goto word
		parser.nextToken();
		// goto word value
		parser.nextToken();
		String wordText = parser.getText();

		// goto pos
		parser.nextToken();
		// goto pos value
		parser.nextToken();
		String posText = parser.getText();
		GrammemeEnum pos = GrammemeEnum.valueOf(posText);
		// goto propb
		parser.nextToken();
		// goto propb value
		parser.nextToken();
		float floatValue = parser.getFloatValue();
		// skip propb value
		parser.nextToken();

		observationStatePropability.put(new WordGrammemePair(wordText, pos), floatValue);
		return true;
	}

	private void readStartStateStats() throws IOException {
		JsonToken token = parser.nextToken(); // field_name
		if (token != JsonToken.FIELD_NAME
				|| !parser.getText().equalsIgnoreCase("start-state-propb"))
			throw new IllegalStateException("incorrect POS tagger DB.");
		parser.nextToken(); // skip field_name
		parser.nextToken(); // skip start_array

		Map<GrammemeEnum, Float> startStatePropability = new HashMap<>();
		while (readStartStateStatsEntry(startStatePropability))
			parser.nextToken(); // skip end_object
		db.setStartStatePropability(startStatePropability);
	}

	// starts on start_object
	private boolean readStartStateStatsEntry(Map<GrammemeEnum, Float> startStatePropability)
			throws IOException {
		if (parser.getCurrentToken() != JsonToken.START_OBJECT)
			return false;
		// goto pos
		parser.nextToken();
		// goto pos value
		parser.nextToken();
		String posText = parser.getText();
		GrammemeEnum pos = GrammemeEnum.valueOf(posText);
		// goto propb
		parser.nextToken();
		// goto propb value
		parser.nextToken();
		float floatValue = parser.getFloatValue();
		// skip propb value
		parser.nextToken();

		startStatePropability.put(pos, floatValue);
		return true;
	}

	private void skipPrefix() throws IOException {
		while (true) {
			JsonToken token = parser.nextToken();
			if (token == JsonToken.FIELD_NAME
					&& parser.getText().equalsIgnoreCase("hmm-tagger-db")) {
				parser.nextToken(); // start_object
				return;
			}
		}
	}



}

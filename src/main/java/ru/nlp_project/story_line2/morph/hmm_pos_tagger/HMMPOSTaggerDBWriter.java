package ru.nlp_project.story_line2.morph.hmm_pos_tagger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import ru.nlp_project.story_line2.morph.GrammemeEnum;

/**
 * класс для записи на диск базы данных "Hidden Markov Model POS-Tagger".
 * 
 * @author fedor
 *
 */
public class HMMPOSTaggerDBWriter {
	private HMMPOSTaggerDB db = null;
	//private Logger log;
	private JsonFactory jsonFactory;
	private JsonGenerator jg;

	public HMMPOSTaggerDBWriter(HMMPOSTaggerDB db) {
		//this.log = LoggerFactory.getLogger(this.getClass());
		this.db = db;
	}

	public void write(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		jsonStart(fos);
		writeDB();
		jsonEnd(fos);
	}

	private void writeDB() throws IOException {
		writePrefix();
		writeStartStateStats();
		writeObservationStateStats();
		writeBiGrammPropability();
		writeAllStateStats();
		writeSuffix();

	}

	private void writeSuffix() throws IOException {
		jg.writeEndObject();
		jg.writeEndObject();
	}

	private void writePrefix() throws IOException {
		jg.writeStartObject();
		jg.writeFieldName("hmm-tagger-db");
		jg.writeStartObject();
	}

	private void writeAllStateStats() throws IOException {
		jg.writeFieldName("all-state-stats");
		jg.writeStartArray();
		Map<GrammemeEnum, Integer> allStateStats = db.getAllStateStats();
		for (Map.Entry<GrammemeEnum, Integer> entry : allStateStats.entrySet()) {
			jg.writeStartObject();
			jg.writeFieldName("pos");
			jg.writeString(entry.getKey().toString());
			jg.writeFieldName("count");
			jg.writeNumber(entry.getValue());
			jg.writeEndObject();
		}
		jg.writeEndArray();
	}

	private void writeBiGrammPropability() throws IOException {
		jg.writeFieldName("bi-gramm-propb");
		jg.writeStartArray();
		Map<GrammemePair, Float> propb = db.getBiGrammPropability();
		for (Entry<GrammemePair, Float> entry : propb.entrySet()) {
			jg.writeStartObject();
			jg.writeFieldName("curr");
			jg.writeString(entry.getKey().getCurr().toString());
			jg.writeFieldName("prev");
			jg.writeString(entry.getKey().getPrev().toString());
			jg.writeFieldName("propb");
			jg.writeNumber(entry.getValue());
			jg.writeEndObject();
		}
		jg.writeEndArray();
	}

	private void writeObservationStateStats() throws IOException {
		jg.writeFieldName("obs-state-propb");
		jg.writeStartArray();
		Map<WordGrammemePair, Float> propb = db.getObservationStatePropability();
		for (Entry<WordGrammemePair, Float> entry : propb.entrySet()) {
			jg.writeStartObject();
			jg.writeFieldName("word");
			jg.writeString(entry.getKey().getWord());
			jg.writeFieldName("pos");
			jg.writeString(entry.getKey().getPos().toString());
			jg.writeFieldName("propb");
			jg.writeNumber(entry.getValue());
			jg.writeEndObject();
		}
		jg.writeEndArray();
	}

	private void writeStartStateStats() throws IOException {
		jg.writeFieldName("start-state-propb");
		jg.writeStartArray();
		Map<GrammemeEnum, Float> propb = db.getStartStatePropability();
		for (Entry<GrammemeEnum, Float> entry : propb.entrySet()) {
			jg.writeStartObject();
			jg.writeFieldName("pos");
			jg.writeString(entry.getKey().toString());
			jg.writeFieldName("propb");
			jg.writeNumber(entry.getValue());
			jg.writeEndObject();
		}
		jg.writeEndArray();
	}

	void jsonStart(FileOutputStream fos) throws IOException {
		jsonFactory = new JsonFactory(); // or, for data binding,
		jg = jsonFactory.createGenerator(fos);
		jg.useDefaultPrettyPrinter();
	}

	void jsonEnd(FileOutputStream fos) throws IOException {
		jg.close();
		IOUtils.closeQuietly(fos);
	}



}

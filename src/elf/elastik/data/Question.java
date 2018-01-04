/*
 * Elastik Application
 * Copyright (c) 2016 - Hugues Cassé <hugues.casse@laposte.net>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package elf.elastik.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import elf.store.StructuredStore.Load;
import elf.store.StructuredStore.Save;

/**
 * A question matching a specific model definition.
 * @author casse
 */
public class Question {
	private UUID uuid;
	private Model model;
	private String[] values;
	
	/**
	 * Build an initialized question.
	 * @param model		Question model.
	 * @param values	Values of the question.
	 */
	public Question(Model model, String... values) {
		this.uuid = UUID.randomUUID();
		this.model = model;
		this.values = Arrays.copyOf(values, values.length);
	}
	
	/**
	 * Build an initialized question.
	 * @param uuid		UUID of the question.
	 * @param model		Model of the question.
	 * @param values	Values of the question.
	 */
	public Question(UUID uuid, Model model, String... values) {
		this.uuid = uuid;
		this.model = model;
		this.values = Arrays.copyOf(values, values.length);
	}

	/**
	 * Build a question from a model.
	 * @param model	Model to use.
	 */
	public Question(Model model) {
		this.uuid = UUID.randomUUID();
		this.model = model;
		values = new String[model.count()];
		Arrays.fill(values, "");
	}

	/**
	 * Load a question from the given store.
	 * @param load	Store to load from.
	 * @param map	Current map of question.
	 * @return		Built question.
	 * @throws IOException 		Format error or IO error.
	 */
	public static Question load(Load load, Map<UUID, Question> map) throws IOException {
		load.getStruct();
		
		// get UUID
		if(!load.getField("uuid"))
			throw new IOException("no UUID for a question");
		UUID uuid = UUID.fromString((String)load.get(String.class));
		
		// get model
		if(!load.getField("model"))
			throw new IOException("no model for question " + uuid);
		String name = (String)load.get(String.class);
		Model model = Model.get(name);
		if(model == null)
			throw new IOException("no model named " + name);

		// read the fields
		if(!load.getField("values"))
			throw new IOException("no value in question " + uuid);
		int n = load.getList();
		if(n != model.count())
			throw new IOException("data for " + uuid + " does not match model " + model);
		String [] values = new String[n];
		for(int i = 0; i < n; i++)
			values[i] = (String)load.get(String.class);
		load.end();
		
		// build the question
		load.end();
		Question quest = new Question(uuid, model, values);
		map.put(uuid, quest);
		return quest;
	}
	
	/**
	 * Get the UUID of the question.
	 * @return	Question UUID.
	 */
	public UUID getUUID() {
		return uuid;
	}

	/**
	 * Model of the question.
	 * @return	Question model.
	 */
	public Model getModel() {
		return model;
	}
	
	/**
	 * Get the value of a field.
	 * @param field		Field to get value for.
	 * @return			Field value.
	 */
	public String get(Field field) {
		return values[field.getIndex()];
	}
	
	/**
	 * Change the value of a field.
	 * @param field		Field identifier.
	 * @param value		Value to set.
	 */
	public void set(Field field, String value) {
		values[field.getIndex()] = value;
	}
	
	/**
	 * Save the question to the given store.
	 * @param save	Store to save to.
	 * @throws IOException	IO error propagation.
	 */
	public void save(Save save) throws IOException {
		save.putStruct();
		save.putField("uuid");
		save.put(uuid.toString());
		save.putField("model");
		save.put(model.getURI());
		save.putField("values");
		save.putList();
		for(Field field: model)
			save.put(values[field.getIndex()]);
		save.end();
		save.end();
	}
	
	@Override
	public String toString() {
		return model.toString(this);
	}
	
}

/*
 * Elastik application
 * Copyright (c) 2014 - Hugues Cassé <hugues.casse@laposte.net>
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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import elf.store.StructuredStore.Load;
import elf.store.StructuredStore.Save;

/**
 * Represent a theme in the words, i.e., logical word collection.
 * @author casse
 */
public class Theme implements Iterable<Word> {
	private Language lang;
	private String name;
	private Model model;
	protected LinkedList<Question> questions = new LinkedList<Question>();
	
	// deprecated
	private LinkedList<Word> words = new LinkedList<Word>();

	/**
	 * Build a new theme with the default SINGLE_WORD model.
	 * @param lang		Owner language.
	 * @param nat		Theme name.
	 */
	public Theme(Language lang, String name) {
		this.lang = lang;
		this.name = name;
		this.model = Model.SINGLE_WORD;
	}

	/**
	 * Build a new theme.
	 * @param lang		Owner language.
	 * @param nat		Theme name.
	 * @param model		Model to use.
	 */
	public Theme(Language lang, String name, Model model) {
		this.lang = lang;
		this.name = name;
		this.model = model;
	}
	
	/**
	 * Test if there is 
	 * @return
	 */
	public boolean isEmpty() {
		return questions.isEmpty();
	}
	
	public static Theme load(Language lang, Load load, Map<UUID, Question> map) throws IOException {
		load.getStruct();
		
		// get name
		if(!load.getField("name"))
			throw new IOException("theme without name");
		String name = (String)load.get(String.class);
		
		// get the model if any
		Model model;
		if(!load.getField("model"))
			model = Model.SINGLE_WORD;
		else {
			String mname = (String)load.get(String.class);
			model = Model.get(mname);
			if(model == null)
				throw new IOException("unknown model " + mname + " in definition of theme " + name);
		}
		
		// build the theme
		Theme theme = new Theme(lang, name, model);

		// get words (kept for backward compatibility)
		if(model == Model.SINGLE_WORD && load.getField("words")) {
			int n = load.getList();
			for(int i = 0; i < n; i++) {
				String id = (String)load.get(String.class);
				UUID uuid = UUID.fromString(id);
				Question quest = map.get(uuid);
				if(quest == null)
					throw new IOException("cannot find question " + uuid);
				else if(quest.getModel() != theme.getModel())
					throw new IOException("model of question " + uuid + " does not match model of theme " + name);
				theme.add(quest);
			}
			load.end();
		}
		
		// try to load questions
		if(load.getField("questions")) {
			int n = load.getList();
			for(int i = 0; i < n; i++) {
				String id = (String)load.get(String.class);
				UUID uuid = UUID.fromString(id);
				Question quest = map.get(uuid);
				if(quest == null)
					throw new IOException("cannot find question " + uuid);
				else if(quest.getModel() != model)
					throw new IOException("incompatibility between question " + uuid + " in theme " + name);
				theme.add(quest);
			}
			load.end();
		}

		// finalize
		load.end();
		lang.add(theme);
		return theme;
	}

	/**
	 * Get the theme model.
	 * @return	Theme model.
	 */
	public Model getModel() {
		return model;
	}
	
	/**
	 * Get theme name.
	 * @return	Theme name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Save to a store.
	 * @param save	Saving handler.
	 * @param set	Already handled questions.
	 */
	public void save(Save save) throws IOException {
		save.putStruct();
		save.putField("name");
		save.put(name);
		save.putField("model");
		save.put(model.getURI());
		save.putField("questions");
		save.putList();
		for(Question quest: questions)
			save.put(quest.getUUID().toString());
		save.end();
		save.end();
	}

	/**
	 * Add a word to the theme.
	 * @param word		Added word.
	 */
	public void add(Word word) {
		lang.modify();
		words.add(word);
	}

	/**
	 * Remove a word from the theme.
	 * @param word		Removed word.
	 */
	public void remove(Word word) {
		lang.modify();
		words.remove(word);
	}
	
	/**
	 * Add a question to the theme.
	 * @param quest		Added question.
	 */
	public void add(Question quest) {
		assert quest.getModel() == model;
		lang.modify();
		questions.add(quest);
	}
	
	/**
	 * Remove a question from the theme.
	 * @param quest
	 */
	public void remove(Question quest) {
		lang.modify();
		questions.remove(quest);
	}
	
	/**
	 * Get the words in the theme.
	 * @return	Theme words.
	 */
	public Collection<Word> getWords() {
		return words;
	}

	@Override
	public Iterator<Word> iterator() {
		return words.iterator();
	}

	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Get the list of questions.
	 * @return	List of questions.
	 */
	public Collection<Question> getQuestions() {
		return questions;
	}
}

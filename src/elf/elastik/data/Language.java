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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeMap;
import java.util.UUID;

import elf.elastik.Main;
import elf.os.OS;
import elf.store.Storage;
import elf.store.StructuredStore;
import elf.store.XMLStructuredStore;
import elf.ui.I18N;

/**
 * Represent a language to learn. 
 * @author casse
 */
public class Language {
	private static I18N english;
	private static final HashMap<String, Eval> map = new HashMap<String, Eval>();
	private static final Eval NULL_EVAL = new Eval() {
		@Override public String eval(String key, I18N i18n, Language lang)
			{ return ""; }
	};
	private String nat, name;
	private LinkedList<Theme> themes = new LinkedList<Theme>();
	private HashMap<Model, Theme> alls = new HashMap<Model, Theme>();
	private boolean loaded = false, modified = false;
	private I18N for_i18n;

	static {
		map.put("@native@", new Eval() {
			@Override public String eval(String key, I18N i18n, Language lang)
				{ return i18n.t("@" + lang.nat + "@"); }
		});
		map.put("@foreign@", new Eval() {
			@Override public String eval(String key, I18N i18n, Language lang)
				{
					String r = i18n.look("@" + lang.name + "@");
					if(r != null)
						return r;
					else
						return i18n.t("foreign");
				}
		});
		Eval to_foreign = new Eval() {
			@Override public String eval(String key, I18N i18n, Language lang)
				{ return lang.getForeignI18N().t("@" + key); }
		};
		map.put("@1@", to_foreign);
		map.put("@2@", to_foreign);
		map.put("@3@", to_foreign);
		map.put("@1s@", to_foreign);
		map.put("@2s@", to_foreign);
		map.put("@3s@", to_foreign);
	}
	
	/**
	 * Build a language.
	 * @param monitor	Current monitor.
	 * @param nat		Native language.
	 * @param lang		Current language.
	 * @param is_new	True if the language is just created, false else.
	 * @param all_name	Native name for all.
	 */
	public Language(String nat, String lang, boolean is_new, String all_name) {
		this.nat = nat;
		this.name = lang;
		loaded = is_new;
		modified = is_new;
	}
	
	/**
	 * Get the theme corresponding to all words.
	 * @return
	 */
	public Theme getAll() {
		return getAll(Model.SINGLE_WORD);
	}
	
	/**
	 * Get the "all" theme for a model.
	 * @param model		Model to get the "all" theme for.
	 * @return			"all" theme for model.
	 */
	public Theme getAll(Model model) {
		Theme theme = alls.get(model);
		if(theme == null) {
			theme = new Theme(this, "all (" + model.getName() + ")", model);
			alls.put(model, theme);
			add(theme);
		}
		return theme;
	}
	
	/**
	 * Test if the theme is all.
	 * @param theme		Tested theme.
	 * @return			True if it is all, false else.
	 */
	public boolean isAll(Theme theme) {
		return alls.containsValue(theme);
	}
	
	/**
	 * Annotate the language as modified.
	 */
	public void modify() {
		modified = true;
	}
	
	/**
	 * Get a name for storage.
	 * @return	Storage name.
	 */
	private String getStoreName() {
		return nat + "-" + name + ".xml";
	}
	
	/**
	 * Test if the language has been modified.
	 * @return	True if modified, false else.
	 */
	public boolean isModified() {
		return modified;
	}
	
	/**
	 * Test if the language has been loaded.
	 * @return	True if loaded, false else.
	 */
	public boolean isLoaded() {
		return loaded;
	}
	
	/**
	 * Save the language.
	 * @throws IOException	IO error.
	 */
	public void save() throws IOException {
		Storage store = OS.os.getLocalStore(Main.APP_NAME, getStoreName());
		XMLStructuredStore xstore = new XMLStructuredStore(store);
		StructuredStore.Save save = xstore.save();
		
		// save header
		save.putField("nat");
		save.put(nat);
		save.putField("name");
		save.put(name);
		
		// save questions
		save.putField("questions");
		save.putList();
		for(Theme theme: alls.values())
			for(Question quest: theme.getQuestions())
				quest.save(save);
		save.end();
		
		// save themes
		save.putField("themes");
		save.putList();
		for(Theme theme: themes)
			if(!isAll(theme))
				theme.save(save);
		save.end();
		
		// closing
		save.end();
		modified = false;
	}
	
	/**
	 * Load the language description.
	 * @throws IOException	Thrown if there is an error.
	 */
	public void load() throws IOException {
		Storage store = OS.os.getLocalStore(Main.APP_NAME, getStoreName());
		XMLStructuredStore xstore = new XMLStructuredStore(store);
		StructuredStore.Load load = xstore.load();
		
		// load header
		if(load.getField("nat"))
			nat = (String)load.get(String.class);
		if(load.getField("name"))
			name = (String)load.get(String.class);
		
		// load the questions (if any)
		TreeMap<UUID, Question> qmap = new TreeMap<UUID, Question>(); 
		if(load.getField("questions")) {
			int n = load.getList();
			for(int i = 0; i < n; i++) {
				Question quest = Question.load(load, qmap);
				qmap.put(quest.getUUID(), quest);
				getAll(quest.getModel()).add(quest);
			}
			load.end();
		}
		
		// compatibility load
		if(load.getField("words")) {
			int n = load.getList();
			for(int i = 0; i < n; i++) {
				load.getStruct();
				Word word = new Word(load);
				load.end();
				Question quest = new Question(word.getID(), Model.SINGLE_WORD, word.getNative(), word.getForeign());
				qmap.put(word.getID(), quest);
				getAll(quest.getModel()).add(quest);
			}
			load.end();
		}
		
		// load themes
		if(load.getField("themes")) {
			int n = load.getList();
			for(int i = 0; i < n; i++)
				Theme.load(this, load, qmap);
			load.end();
		}
		
		// ending
		load.end();
		loaded = true;
		modified = false;
	}

	/**
	 * Is the language empty (no words).
	 * @return	True if empty, false else.
	 */
	public boolean isEmpty() {
		for(Theme theme: alls.values())
			if(!theme.isEmpty())
				return false;
		return true;
	}
	
	/**
	 * Get the native language.
	 * @return	Native language.
	 */
	public String getNative() {
		return nat;
	}
	
	/**
	 * Get the name of the language.
	 * @return	Language name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the list of words.
	 * @return	Word list.
	 */
	public Iterable<Word> getWords() {
		return null;
	}
	
	/**
	 * Add a word.
	 * @param word	Word to add.
	 */
	public void add(Word word) {
		// all.add(word);
	}
	
	/**
	 * Remove a word.
	 * @param word	Removed word.
	 */
	public void remove(Word word) {
		// all.remove(word);
	}
	
	/**
	 * Add a theme.
	 * @param theme		Added theme.
	 */
	void add(Theme theme) {
		modify();
		themes.add(theme);
	}
	
	/**
	 * Remove a theme.
	 * @param theme		Removed theme.
	 */
	public void remove(Theme theme) {
		modify();
		themes.remove(theme);
	}
	
	/**
	 * Get the list of themes.
	 * @return	List of themes.
	 */
	public Collection<Theme> getThemes() {
		return themes;
	}
	
	/**
	 * Get the internationalization for the foreign language.
	 * @return	Foreign language internationalization.
	 */
	public I18N getForeignI18N() {
		if(for_i18n == null)
			for_i18n = new I18N("Elastik", Locale.forLanguageTag(name));
		return for_i18n;
	}
	
	/**
	 * Translate a string into the current language supporting
	 * symbols between @...@.
	 * @param text	Text to translate.
	 * @param lang	UI locale.
	 * @return		Translated text.
	 */
	public String t(String text, I18N i18n) {
		
		// try to translate, else use english language
		String t = i18n.look(text);
		if(t == null) {
			if(english == null)
				english = new I18N(Main.APP_NAME, Locale.forLanguageTag("en"));
			i18n = english;
		}
		else
			text = t;
		
		// evaluate the symbols
		int i = text.indexOf('@');
		while(i >= 0) {
			int j = text.indexOf('@', i + 1);
			if(j < 0)
				break;
			String id = text.substring(i, j + 1);
			Eval eval = map.get(id);
			if(eval == null)
				eval = NULL_EVAL;
			String val = eval.eval(id, i18n, this);
			text = text.substring(0, i) + val + text.substring(j + 1);
			i = text.indexOf('@', i + val.length() + 1);
		}
		return text;
	}

	private static interface Eval {
		
		String eval(String key, I18N i18n, Language lang);
		
	}
	
}

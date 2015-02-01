package elf.elastik;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.UUID;

import elf.os.OS;
import elf.store.Storage;
import elf.store.StructuredStore;
import elf.store.XMLStructuredStore;

/**
 * Represent a language to learn. 
 * @author casse
 */
public class Language {
	private String nat, name;
	private final Theme all;
	private LinkedList<Theme> themes = new LinkedList<Theme>();
	private boolean loaded = false, modified = false;
	
	/**
	 * Build a language.
	 * @param monitor	Current monitor.
	 * @param nat		Native language.
	 * @param lang		Current language.
	 * @param is_new	True if the language is just created, false else.
	 * @param all_name	Native name for all.
	 */
	public Language(String nat, String lang, boolean is_new, String all_name) {
		all = new Theme(this, all_name, all_name);
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
		return all;
	}
	
	/**
	 * Test if the theme is all.
	 * @param theme		Tested theme.
	 * @return			True if it is all, false else.
	 */
	public boolean isAll(Theme theme) {
		return all == theme;
	}
	
	/**
	 * Annotate the language as modified.
	 */
	void modify() {
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
		
		// save words
		save.putField("words");
		save.putList();
		for(Word word: all.getWords()) {
			save.putStruct();
			word.save(save);
			save.end();
		}
		save.end();
		
		// save themes
		save.putField("themes");
		save.putList();
		for(Theme theme: themes)
			if(theme != all) {
				save.putStruct();
				theme.save(save);
				save.end();
			}
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
		System.out.println("=== " + nat + "-" + name + "===");
		
		// load words
		TreeMap<UUID, Word> map = new TreeMap<UUID, Word>();
		if(load.getField("words")) {
			int n = load.getList();
			for(int i = 0; i < n; i++) {
				load.getStruct();
				Word word = new Word(load);
				all.add(word);
				map.put(word.getID(), word);
				load.end();
				System.out.println("- " + word.getNative() + " / " + word.getForeign());
			}
			load.end();
		}
		
		// load themes
		if(load.getField("themes")) {
			int n = load.getList();
			for(int i = 0; i < n; i++) {
				load.getStruct();
				new Theme(this, load, map);
				load.end();
			}
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
		return all.getWords().isEmpty();
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
		return all.getWords();
	}
	
	/**
	 * Add a word.
	 * @param word	Word to add.
	 */
	public void add(Word word) {
		all.add(word);
	}
	
	/**
	 * Remove a word.
	 * @param word	Removed word.
	 */
	public void remove(Word word) {
		all.remove(word);
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
}

package elf.elastik;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import elf.store.StructuredStore.Load;
import elf.store.StructuredStore.Save;

/**
 * Represent a theme in the words, i.e., logical word collection.
 * @author casse
 */
public class Theme {
	private Language lang;
	private LinkedList<Word> words = new LinkedList<Word>();
	private String name;
	
	/**
	 * Build a new theme.
	 * @param lang		Owner language.
	 * @param nat		Theme name.
	 */
	public Theme(Language lang, String name) {
		this.lang = lang;
		this.name = name;
	}
	
	/**
	 * Build a theme from storage.
	 * @param lang			Owner language.
	 * @param load			Load handler.
	 * @param map			Current words map.
	 * @throws IOException	Thrown with IO error.
	 */
	public Theme(Language lang, Load load, Map<UUID, Word> map) throws IOException {
		this.lang = lang;
		
		// get name
		if(!load.getField("name"))
			throw new IOException("theme without name");
		name = (String)load.get(String.class);
		
		// get words
		if(load.getField("words")) {
			int n = load.getList();
			for(int i = 0; i < n; i++) {
				String id = (String)load.get(String.class);
				UUID uuid = UUID.fromString(id);
				Word word = map.get(uuid);
				if(word != null)
					words.add(word);
			}
			load.end();
		}
		lang.add(this);
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
	 */
	public void save(Save save) throws IOException {
		save.putField("name");
		save.put(name);
		save.putField("words");
		save.putList();
		for(Word word: words)
			save.put(word.getID().toString());
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
	 * Get the words in the theme.
	 * @return	Theme words.
	 */
	public Collection<Word> getWords() {
		return words;
	}
}

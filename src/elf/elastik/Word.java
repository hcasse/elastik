package elf.elastik;

import java.io.IOException;
import java.util.UUID;

import elf.store.XMLStructuredStore;

/**
 * A word in the language.
 * @author casse
 */
public class Word {
	private UUID uuid;
	private String nat, fore;
	
	/**
	 * Build a word.
	 * @param nat		Native version.
	 * @param fore		Language version.
	 */
	public Word(String nat, String fore) {
		uuid = UUID.randomUUID();
		this.nat = nat;
		this.fore = fore;
	}
	
	/**
	 * Get the word unique ID.
	 * @return	Word ID.
	 */
	public UUID getID() {
		return uuid;
	}
	
	/**
	 * Create a word from a store.
	 * @param load	Loader to load from.
	 */
	public Word(XMLStructuredStore.Load load) throws IOException {
		if(!load.getField("id"))
			throw new IOException("mal-formed word: no id");
		String id = (String)load.get(String.class);
		uuid = UUID.fromString(id);
		if(!load.getField("nat"))
			throw new IOException("mal-formed word: no native word");
		nat = (String)load.get(String.class);
		if(!load.getField("word"))
			throw new IOException("mal-formed word: no language word");
		fore = (String)load.get(String.class);
	}
	
	/**
	 * Save a word to store.
	 * @param save	Store to save to.
	 * @throws IOException	In case of error.
	 */
	public void save(XMLStructuredStore.Save save) throws IOException {
		save.putField("id");
		save.put(uuid.toString());
		save.putField("nat");
		save.put(nat);
		save.putField("word");
		save.put(fore);
	}

	/**
	 * Get native word.
	 * @return	Native word.
	 */
	public String getNative() {
		return nat;
	}
	
	/**
	 * Get the word in current language.
	 * @return	Current language word.
	 */
	public String getForeign() {
		return fore;
	}
}

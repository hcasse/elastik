package elf.elastik;

/**
 * Interface shared by modifiers of basic tests.
 * @author casse
 */
public interface TestManager extends Test {

	/**
	 * Get the current word.
	 * @return	Current word.
	 */
	Word getWord();
	
	/**
	 * Add a word to the current list of tested words
	 * @param word	Added word.
	 */
	void add(Word word);
	
}

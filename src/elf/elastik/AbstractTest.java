package elf.elastik;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import elf.util.Duration;

/**
 * Implement a test.
 * @author casse
 */
public abstract class AbstractTest implements TestManager {
	private Duration duration = Duration.NULL;
	protected Language lang;
	protected Random random;
	protected LinkedList<Word> words = new LinkedList<Word>();
	protected LinkedList<Word> initial = new LinkedList<Word>();
	protected Word word;
	private int word_count, try_count, success_count;
	
	public AbstractTest(Language lang, Collection<Theme> themes) {
		this.lang = lang;
		for(Theme theme: themes)
			for(Word word: theme.getWords())
				if(!words.contains(word))
					words.add(word);
		initial.addAll(words);
		word_count = words.size();
		random = new Random();
		random.setSeed(System.currentTimeMillis());
	}
	
	@Override
	public int getWordCount() {
		return word_count;
	}
	
	@Override
	public int getRemainCount() {
		return words.size();
	}
	
	/**
	 * Get the next word to test.
	 * @return	Next word to test using faire random number.
	 */
	protected Word nextWord() {
		if(words.isEmpty()) {
			word = null;
			return null;			
		}
		else {
			word = words.get(random.nextInt(getRemainCount()));
			words.remove(word);
			return word;			
		}
	}
	
	/**
	 * Get the current word.
	 * @return	Current word.
	 */
	public Word getWord() {
		return word;
	}
	
	/**
	 * Word is returned to the set of tested words.
	 * @param word	Reput word.
	 */
	public void add(Word word) {
		words.add(word);
	}
	
	/**
	 * Add one to success and try counters.
	 */
	protected void succeeded() {
		try_count++;
		success_count++;
	}
	
	/**
	 * Add one to try counter.
	 */
	protected void failed() {
		try_count++;
	}
	
	@Override
	public int getTryCount() {
		return try_count;
	}
	
	@Override
	public int getSuccessCount() {
		return success_count;
	}

	@Override
	public void reset() {
		words.clear();
		words.addAll(initial);
		word = null;
		try_count = 0;
		success_count = 0;
	}

	@Override
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	@Override
	public Duration getDuration() {
		return duration;
	}
}

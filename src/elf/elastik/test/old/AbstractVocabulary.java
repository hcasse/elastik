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
package elf.elastik.test.old;

import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Abstract implementation of a vocabulary test.
 * @author casse
 */
public abstract class AbstractVocabulary implements Iterable<AbstractVocabulary.Word> {
	TreeMap<String, Word> words = new TreeMap<String, Word>();

	/**
	 * Get the question language of the vocabulary.
	 * @return	Question language.
	 */
	protected abstract String getQuestionLanguage();

	/**
	 * Get the answer language of the vocabulary.
	 * @return	Answer language.
	 */
	protected abstract String getAnswerLanguage();

	/**
	 * Called to build the vocabulary. The implementing class
	 * must be perform several add() method to add words from this method.
	 */
	protected abstract void make();

	/**
	 * Add the given question word and answer word to the set of questions.
	 * Synonyms will be taken into account at this point.
	 * @param question		Question word.
	 * @param answer		Answer word.
	 */
	protected void add(String question, String answer) {
		Word word = words.get(question.toLowerCase());
		if(word == null) {
			word = new Word(question);
			words.put(question.toLowerCase(), word);
		}
		word.add(answer);
	}

	/**
	 * Represents a word of the vocabulary.
	 * @author casse
	 */
	public static class Word implements Iterable<String> {
		String word;
		TreeSet<String> answers = new TreeSet<String>();

		public Word(String word) {
			this.word = word;
		}

		/**
		 * Get the asked word.
		 * @return	Asked word.
		 */
		public String getWord() {
			return word;
		}

		/**
		 * Add an answer.
		 * @param answer	Added answer.
		 */
		public void add(String answer) {
			answers.add(answer);
		}

		@Override
		public Iterator<String> iterator() {
			return answers.iterator();
		}
	}

	@Override
	public Iterator<Word> iterator() {
		make();
		return words.values().iterator();
	}
}

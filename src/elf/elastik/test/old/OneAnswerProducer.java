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

import elf.elastik.test.old.AbstractVocabulary.Word;


/**
 * A question producer based on a vocabulary requiring only one answer.
 * @author casse
 */
public class OneAnswerProducer implements Producer {
	private BasicTest test;
	private AbstractVocabulary vocabulary;

	public OneAnswerProducer(BasicTest test, AbstractVocabulary vocabulary) {
		this.test = test;
		this.vocabulary = vocabulary;
	}

	@Override
	public Iterator<Question> iterator() {
		return new MyIterator();
	}

	private class MyIterator implements Iterator<Question> {
		private Iterator<Word> i;

		public MyIterator() {
			i = vocabulary.iterator();
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public Question next() {
			return new MyQuestion(i.next());
		}

		@Override
		public void remove() {
		}

	}

	private class MyQuestion implements Question {
		Word word;

		public MyQuestion(Word word) {
			this.word = word;
		}

		@Override
		public String getQuestion() {
			return word.getWord();
		}

		@Override
		public String getQuestionLanguage() {
			return vocabulary.getQuestionLanguage();
		}

		@Override
		public String check(String answer) {
			String good_answer = "";
			int count = 0;
			for(String ans: word) {
				count++;
				good_answer = ans;
				if(ans.equalsIgnoreCase(answer))
					return null;
			}
			if(count > 1)
				return String.format("Sorry, this is a bad answer. A good answer could be \"%s\"", good_answer);
			else
				return String.format("Sorry, this is a bad answer. The good answer is \"%s\"", good_answer);
		}

		@Override
		public String getAnswerLanguage() {
			return vocabulary.getAnswerLanguage();
		}

		@Override
		public String getLabel() {
			return String.format(test.t("What's the word for..."), word.getWord());
		}

	}
}

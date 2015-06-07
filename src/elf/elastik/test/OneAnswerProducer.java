package elf.elastik.test;

import java.util.Iterator;

import elf.elastik.test.AbstractVocabulary.Word;


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

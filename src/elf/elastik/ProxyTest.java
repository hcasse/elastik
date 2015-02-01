package elf.elastik;

import elf.util.Duration;

/**
 * Test forwarding method to a subtest.
 * @author casse
 */
public class ProxyTest implements TestManager {
	private TestManager test;
	
	/**
	 * Build a test proxy.
	 * @param test	Test behind the proxy.
	 */
	public ProxyTest(TestManager test) {
		this.test = test;
	}
	
	@Override
	public int getWordCount() {
		return test.getWordCount();
	}

	@Override
	public int getRemainCount() {
		return test.getRemainCount();
	}

	@Override
	public String getQuestionLang() {
		return test.getQuestionLang();
	}

	@Override
	public String getAskedLang() {
		return test.getAskedLang();
	}

	@Override
	public String nextQuestion() {
		return test.nextQuestion();
	}

	@Override
	public String checkAnswer(String answer) {
		return test.checkAnswer(answer);
	}

	@Override
	public int getTryCount() {
		return test.getTryCount();
	}

	@Override
	public int getSuccessCount() {
		return test.getSuccessCount();
	}

	@Override
	public void reset() {
		test.reset();
	}

	@Override
	public void setDuration(Duration duration) {
		test.setDuration(duration);
	}

	@Override
	public Duration getDuration() {
		return test.getDuration();
	}

	@Override
	public Word getWord() {
		return test.getWord();
	}

	@Override
	public void add(Word word) {
		test.add(word);
	}

}

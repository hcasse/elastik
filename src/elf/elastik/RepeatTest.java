package elf.elastik;

import java.util.LinkedList;

/**
 * Test with repetition of failed word.
 * @author casse
 */
public class RepeatTest extends ProxyTest {
	public LinkedList<Word> failed = new LinkedList<Word>();
	public String word;
	
	public RepeatTest(TestManager test) {
		super(test);
	}

	@Override
	public String checkAnswer(String answer) {
		String result = super.checkAnswer(answer);
		if(result != null && !failed.contains(getWord())) {
			failed.add(getWord());
			add(getWord());
		}
		return result;
	}

	@Override
	public void reset() {
		super.reset();
		failed.clear();
	}

}

package elf.elastik.test;

import java.util.Random;

/**
 * Random selector of questions.
 * @author casse
 */
public class RandomManager implements Manager {
	private Random random = new Random();
	private Question last;

	@Override
	public Question next(BasicTest test) {
		int i = random.nextInt(test.getQuestionNumber() - test.getDoneNumber());
		last = test.get(i);
		return last;
	}

	@Override
	public String check(BasicTest test, String answer) {
		String r = last.check(answer);
		if(r == null)
			test.succeeded(last);
		else
			test.failed(last);
		return r;
	}

	@Override
	public void reset() {
	}

}

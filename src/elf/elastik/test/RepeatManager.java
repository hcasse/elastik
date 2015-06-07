package elf.elastik.test;

import java.util.HashSet;

/**
 * This manager repeat failed questions.
 * @author casse
 */
public class RepeatManager implements Manager {
	private Manager manager;
	private HashSet<Question> tested = new HashSet<Question>();
	private Question last;

	public RepeatManager(Manager manager) {
		this.manager = manager;
	}

	@Override
	public Question next(BasicTest test) {
		last = manager.next(test);
		return last;
	}

	@Override
	public String check(BasicTest test, String answer) {
		String r = last.check(answer);
		if(r == null) {
			test.succeeded(last);
			return r;
		}
		else if(tested.contains(last)) {
			test.failed(last);
			return r;
		}
		else {
			tested.add(last);
			return test.t("Error! One try remaining.");
		}
	}

	@Override
	public void reset() {
		tested.clear();
	}

}

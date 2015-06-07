package elf.elastik.test;

/**
 * A test manager used by AbstractTest to manage the test.
 * @author casse
 */
public interface Manager {

	/**
	 * Reset the manager.
	 */
	void reset();

	/**
	 * Get the next question.
	 * @param test	Current test.
	 * @return		Next question or null.
	 */
	Question next(BasicTest test);

	/**
	 * Check for an answer.
	 * @param test		Current test.
	 * @param answer	Proposed answer.
	 */
	String check(BasicTest test, String answer);
}

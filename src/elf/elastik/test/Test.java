package elf.elastik.test;

import elf.ui.Icon;

/**
 * Interface describing a test.
 * @author casse
 */
public interface Test {

	/**
	 * Get the label of the test.
	 * @return	Test label.
	 */
	String getLabel();

	/**
	 * Get the icon of the test.
	 * @return	Test icon.
	 */
	Icon getIcon();

	/**
	 * Get the next question.
	 * @return		Next question.
	 */
	Question next();

	/**
	 * Check for an answer.
	 * @param answer	Checked answer.
	 * @return			Null for success, error message else.
	 */
	String check(String answer);

	/**
	 * Get the number of questions.
	 * @return		Question number.
	 */
	int getQuestionNumber();

	/**
	 * Get the number of succeeded questions.
	 * @return		Succeeded question number.
	 */
	int getSucceededNumber();

	/**
	 * Get the number of done questions.
	 * @return	Done question number.
	 */
	int getDoneNumber();

	/**
	 * Get the count of tried questions.
	 * @return	Tried questions count.
	 */
	int getTryCount();

	/**
	 * Reset the test.
	 */
	void reset();

	/**
	 * Get the last test duration.
	 * @return	Test duration (in ms).
	 */
	public long getDuration();
}

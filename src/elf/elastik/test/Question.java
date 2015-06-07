package elf.elastik.test;

/**
 * A question from a test.
 * @author casse
 */
public interface  Question {
	public static final Question NULL = new Question() {
		@Override public String getLabel() { return "no question"; }
		@Override public String getQuestion() { return ""; }
		@Override public String getQuestionLanguage() { return ""; }
		@Override public String check(String answer) { return null; }
		@Override public String getAnswerLanguage() { return ""; }
	};

	/**
	 * Get the question label.
	 * @return	Question label.
	 */
	String getLabel();

	/**
	 * Get the question.
	 * @return	Question as string.
	 */
	String getQuestion();

	/**
	 * Get the question language international identifier.
	 * @return	Question language.
	 */
	String getQuestionLanguage();

	/**
	 * Check an answer.
	 * @param answer	Answer to check.
	 * @return			Null for success, an error message else.
	 */
	String check(String answer);

	/**
	 * Get the answer language international identifier.
	 * @return			Answer language.
	 */
	String getAnswerLanguage();
}

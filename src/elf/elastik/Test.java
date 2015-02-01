package elf.elastik;

import java.util.Collection;

import elf.util.Duration;

/**
 * Interface for tests.
 * @author casse
 */
public interface Test {

	/**
	 * Get the total word count.
	 * @return	Total word count.
	 */
	int getWordCount();
	
	/**
	 * Return the number of words that remains to test.
	 * @return		Remain count.
	 */
	int getRemainCount();
	
	/**
	 * Get the display language.
	 * @return	Display language.
	 */
	String getQuestionLang();
	
	/**
	 * Get the asked language.
	 * @return	Asked language.
	 */
	String getAskedLang();
	
	/**
	 * Get the next word to test.
	 * @return	Next word or null if end reached.
	 */
	String nextQuestion();
	
	/**
	 * Check the answer.
	 * @param anwser	Answer to test.
	 * @return	Return null for success, correct answer else.
	 */
	String checkAnswer(String answer);
	
	/**
	 * Get the try count.
	 * @return	Try count.
	 */
	int getTryCount();
	
	/**
	 * Get the success count.
	 * @return	Success count.
	 */
	int getSuccessCount();

	/**
	 * Reset the test.
	 */
	void reset();
	
	/**
	 * Set the duration of the test.
	 * @param duration	Test duration.
	 */
	void setDuration(Duration duration);
	
	/**
	 * Get the test duration.
	 * @return	Test duration.
	 */
	Duration getDuration();
	
	/**
	 * Test type enumeration.
	 * @author casse
	 */
	public enum Type {
		FOREIGN_TO_NATIVE,
		NATIVE_TO_FOREIGN;
		
		public String getLabel() {
			switch(this) {
			case FOREIGN_TO_NATIVE: return "Find native word";
			case NATIVE_TO_FOREIGN: return "Find foreign word";
			default:				return "";
			}
		}
		
		public TestManager getTest(Language lang, Collection<Theme> themes) {
			switch(this) {
			case FOREIGN_TO_NATIVE:	return new ForeignToNativeTest(lang, themes);
			case NATIVE_TO_FOREIGN:	return new NativeToForeignTest(lang, themes);
			default:				return null;}
		}
	}
}

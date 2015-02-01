package elf.elastik;

import java.util.Collection;

/**
 * Test where foreign word is displayed and native word is asked.
 * @author casse
 */
public class ForeignToNativeTest extends AbstractTest {

	public ForeignToNativeTest(Language lang, Collection<Theme> themes) {
		super(lang, themes);
	}

	@Override
	public String getQuestionLang() {
		return lang.getName();
	}

	@Override
	public String getAskedLang() {
		return lang.getNative();
	}

	@Override
	public String nextQuestion() {
		nextWord();
		if(word == null)
			return null;
		else
			return word.getForeign();
	}

	@Override
	public String checkAnswer(String answer) {
		if(word.getNative().equals(answer)) {
			succeeded();
			return null;
		}
		else {
			failed();
			return word.getNative();
		}
	}

}

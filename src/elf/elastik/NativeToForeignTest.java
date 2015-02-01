package elf.elastik;

import java.util.Collection;

/**
 * Test where foreign word is displayed and native word is asked.
 * @author casse
 */
public class NativeToForeignTest extends AbstractTest {

	public NativeToForeignTest(Language lang, Collection<Theme> themes) {
		super(lang, themes);
	}

	@Override
	public String getQuestionLang() {
		return lang.getNative();
	}

	@Override
	public String getAskedLang() {
		return lang.getName();
	}

	@Override
	public String nextQuestion() {
		nextWord();
		if(word == null)
			return null;
		else
			return word.getNative();
	}

	@Override
	public String checkAnswer(String answer) {
		if(word.getForeign().equals(answer)) {
			succeeded();
			return null;
		}
		else {
			failed();
			return word.getForeign();
		}
	}

}

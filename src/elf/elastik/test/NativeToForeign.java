package elf.elastik.test;

import java.util.Collection;

import elf.elastik.data.Theme;

/**
 * Vocabulary test asking with native word for matching foreign word.
 * @author casse
 */
public class NativeToForeign extends AbstractVocabulary {
	private Collection<Theme> themes;
	private String qlang, alang;

	public NativeToForeign(Collection<Theme> themes, String forn, String natv) {
		this.themes = themes;
		this.qlang = forn;
		this.alang = natv;
	}

	@Override
	protected String getQuestionLanguage() {
		return qlang;
	}

	@Override
	protected String getAnswerLanguage() {
		return alang;
	}

	@Override
	protected void make() {
		for(Theme theme: themes)
			for(elf.elastik.data.Word word: theme)
				add(word.getNative(), word.getForeign());
	}

}

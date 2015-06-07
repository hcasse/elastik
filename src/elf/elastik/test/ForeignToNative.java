package elf.elastik.test;

import java.util.Collection;

import elf.elastik.data.Theme;

/**
 * Vocabulary test asking with foreign word for matching native word.
 * @author casse
 */
public class ForeignToNative extends AbstractVocabulary {
	private Collection<Theme> themes;
	private String qlang, alang;

	public ForeignToNative(Collection<Theme> themes, String forn, String natv) {
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
				add(word.getForeign(), word.getNative());
	}

}

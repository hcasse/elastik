/*
 * Elastik application
 * Copyright (c) 2014 - Hugues Cassé <hugues.casse@laposte.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package elf.elastik.test.old;

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

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

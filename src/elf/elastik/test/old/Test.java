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

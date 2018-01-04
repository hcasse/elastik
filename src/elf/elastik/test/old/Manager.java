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
 * A test manager used by AbstractTest to manage the test.
 * @author casse
 */
public interface Manager {

	/**
	 * Reset the manager.
	 */
	void reset();

	/**
	 * Get the next question.
	 * @param test	Current test.
	 * @return		Next question or null.
	 */
	Question next(BasicTest test);

	/**
	 * Check for an answer.
	 * @param test		Current test.
	 * @param answer	Proposed answer.
	 */
	String check(BasicTest test, String answer);
}

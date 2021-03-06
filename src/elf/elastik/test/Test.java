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
package elf.elastik.test;

import elf.elastik.LanguageModel;
import elf.elastik.data.Model;
import elf.ui.Icon;
import elf.ui.meta.Var;

/**
 * Interface describing a test.
 * @author casse
 */
public interface Test {

	void configure(Var<Integer> succeeded, Var<Integer> done);
	
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
	 * Get the language model.
	 * @return	Language model.
	 */
	LanguageModel getLanguage();
	
	/**
	 * Get the model of the questions.
	 * @return	Question model.
	 */
	Model getModel();

	/**
	 * Get the next question.
	 * @param values	Array to store the displayed values of the questions. Null fields represents values to answer.
	 * @return			True if there it remains questions, false else (values not written).
	 */
	boolean next(String[] values);

	/**
	 * Check if the answers are the right ones. 
	 * @param values	Fixed values (one for each model field, good answers are set to null).
	 * @return			True for success, false else.
	 */
	boolean check(String[] values);
	
	/**
	 * Get the number of questions.
	 * @return		Question number.
	 */
	int getQuestionNumber();

	/**
	 * Get the number of succeeded questions.
	 * @return		Succeeded question number.
	 */
	Var<Integer> getSucceededNumber();

	/**
	 * Get the number of done questions.
	 * @return	Done question number.
	 */
	Var<Integer> getDoneNumber();

	/**
	 * Get the count of tried questions.
	 * @return	Tried questions count.
	 */
	int getTryCount();

	/**
	 * Reset the test.
	 */
	void reset();

}

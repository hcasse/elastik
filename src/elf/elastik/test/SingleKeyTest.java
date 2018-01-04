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

import java.util.Collection;

import elf.elastik.LanguageModel;
import elf.elastik.data.Field;
import elf.elastik.data.Model;
import elf.elastik.data.Question;
import elf.ui.Icon;
import elf.ui.meta.Var;

/**
 * Test asking for a single field.
 * @author casse
 */
public class SingleKeyTest implements Test {
	private LanguageModel language;
	private Model model;
	private Field field;
	private Collection<Question> questions;
	private Manager manager;
	private Question current;
	private int tries;
	private Var<Integer> succeeded;
	
	public SingleKeyTest(LanguageModel language, Model model, Field field, Collection<Question> questions, Manager manager) {
		this.language = language;
		this.model = model;
		this.field = field;
		this.questions = questions;
		this.manager = manager;
		manager.setQuestions(questions);
	}
	
	@Override
	public String getLabel() {
		return "Test based on " + field.getIndex();
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public int getQuestionNumber() {
		return questions.size();
	}

	@Override
	public Var<Integer> getSucceededNumber() {
		return succeeded;
	}

	@Override
	public Var<Integer> getDoneNumber() {
		return manager.getDoneNumber();
	}

	@Override
	public int getTryCount() {
		return tries;
	}

	@Override
	public void reset() {
		succeeded.set(0);
		tries = 0;
		manager.setQuestions(questions);
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public boolean next(String[] values) {
		current = manager.next();
		if(current == null)
			return false;
		for(Field field: model)
			if(this.field == field)
				values[field.getIndex()] = current.get(field);
			else
				values[field.getIndex()] = null;
		return true;
	}

	@Override
	public boolean check(String[] values) {
		tries++;
		boolean success = true;
		for(Field field: model)
			if(field == this.field)
				values[field.getIndex()] = null;
			else if(values[field.getIndex()].equals(current.get(field)))
				values[field.getIndex()] = null;
			else {
				success = false;
				values[field.getIndex()] = current.get(field);
			}
		if(success) {
			succeeded.set(succeeded.get() + 1);
			manager.succeed(current);
		}
		else {
			manager.failed(current);
		}
		return success;
	}

	@Override
	public LanguageModel getLanguage() {
		return language;
	}

	@Override
	public void configure(Var<Integer> succeeded, Var<Integer> done) {
		this.succeeded = succeeded;
		manager.configure(done);
	}

}

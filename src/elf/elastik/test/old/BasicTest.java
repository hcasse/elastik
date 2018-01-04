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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import elf.ui.I18N;
import elf.ui.Icon;

public class BasicTest implements Test, Iterable<Question> {
	private Producer producer;
	private Manager manager;
	private LinkedList<Question> questions;
	private ArrayList<Question> todo;
	private I18N i18n;
	private int success_count, try_count;
	private long start_time = -1, end_time = -1;

	public BasicTest(I18N i18n) {
		this.i18n = i18n;
	}

	public String t(String text) {
		return i18n.t(text);
	}

	/**
	 * Get the current producer.
	 * @return		Current producer.
	 */
	public Producer getProducer() {
		return producer;
	}

	/**
	 * Set the used producer.
	 * @param producer	New producer.
	 */
	public void setProducer(Producer producer) {
		this.producer = producer;
	}

	/**
	 * Get the current manager.
	 * @return	Current manager.
	 */
	public Manager getManager() {
		return manager;
	}

	/**
	 * Set the used manager.
	 * @param manager	Used manager.
	 */
	public void setManager(Manager manager) {
		this.manager = manager;
	}

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public Question next() {
		init();
		if(start_time < 0)
			start_time = System.currentTimeMillis();
		if(todo.isEmpty()) {
			end_time = System.currentTimeMillis();
			return null;
		}
		else {
			try_count++;
			return manager.next(this);
		}
	}

	@Override
	public int getQuestionNumber() {
		init();
		return questions.size();
	}

	@Override
	public int getSucceededNumber() {
		init();
		return success_count;
	}

	@Override
	public int getDoneNumber() {
		init();
		return questions.size() - todo.size();
	}

	/**
	 * Initialize the test.
	 */
	private void init() {
		if(questions != null)
			return;
		questions = new LinkedList<Question>();
		for(Question question: producer)
			questions.add(question);
		todo = new ArrayList<Question>();
		reset();
	}

	@Override
	public void reset() {
		init();
		start_time = -1;
		end_time = -1;
		todo.clear();
		todo.addAll(questions);
		success_count = 0;
		try_count = 0;
	}

	@Override
	public Iterator<Question> iterator() {
		return producer.iterator();
	}

	/**
	 * Get a question by its index.
	 * @param i		Question index.
	 * @return		Matching index.
	 */
	public Question get(int i) {
		return todo.get(i);
	}

	/**
	 * Mark a question as failed (it is removed from the todo list).
	 * @param question	Failed question.
	 */
	public void failed(Question question) {
		todo.remove(question);
	}

	/**
	 * Mark a question as succeeded.
	 * @param question	Succeeded question.
	 */
	public void succeeded(Question question) {
		todo.remove(question);
		success_count++;
	}

	@Override
	public String check(String answer) {
		return manager.check(this, answer);
	}

	@Override
	public long getDuration() {
		return end_time - start_time;
	}

	@Override
	public int getTryCount() {
		return try_count;
	}
}

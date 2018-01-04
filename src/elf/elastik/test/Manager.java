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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import elf.elastik.data.Question;
import elf.ui.meta.Var;

/**
 * A manager is in charge of managing the collection of questions.
 * @author casse
 */
public abstract class Manager {
	protected LinkedList<Question> questions = new LinkedList<Question>();
	protected Selector selector = new Selector.InOrderSelector();
	protected Var<Integer> done;
	
	/**
	 * Configure the manager.
	 * @param done	Done variable.
	 */
	public void configure(Var<Integer> done) {
		this.done = done;
	}
	
	/**
	 * Reset the test management.
	 */
	public void reset() {
		selector.reset();
	}
	
	/**
	 * Set the collection of questions.
	 * @param questions		Collection of questions.
	 */
	public void setQuestions(Collection<Question> questions) {
		this.questions.clear();
		this.questions.addAll(questions);
	}
	
	/**
	 * Set the current selector (as a default, the InOrderSelector
	 * is used.
	 * @param selector	Selector to use.
	 */
	public void setSelector(Selector selector) {
		this.selector = selector;
	}
	
	/**
	 * Get the current selector.
	 * @return	Current selector.
	 */
	public Selector getSelector() {
		return selector;
	}
	
	/**
	 * Get the next question.
	 * @return	Next question.
	 */
	public abstract Question next();
	
	/**
	 * Record that a question answer succeeded.
	 * @param question	Succeeded question.
	 */
	public abstract void succeed(Question question);
	
	/**
	 * Record that a question answer failed.
	 * @param question	Failed question.
	 */
	public abstract void failed(Question question);
	
	/**
	 * Get the number of questions that are completely done
	 * (that won't be asked again).
	 * @return	Done question number.
	 */
	public Var<Integer> getDoneNumber() {
		return done;
	}
	
	/**
	 * Manager that proposes questions only once.
	 * @author casse
	 */
	public static class OneShot extends Manager {

		@Override
		public Question next() {
			if(questions.isEmpty())
				return null;
			else {
				Question question = selector.select(questions);
				questions.remove(question);
				return question;				
			}
		}

		@Override
		public void succeed(Question question) {
			done.set(done.get() + 1);
		}

		@Override
		public void failed(Question question) {
			done.set(done.get() + 1);
		}

	}

	/**
	 * Manager that ask questions twice if it failed.
	 * @author casse
	 */
	public static class TwoShots extends Manager {
		private Set<Question> set = new HashSet<Question>();
		
		@Override
		public Question next() {
			if(questions.isEmpty())
				return null;
			else {
				Question question = selector.select(questions);
				questions.remove(question);
				return question;				
			}
		}

		@Override
		public void succeed(Question question) {	
			done.set(done.get() + 1);
		}

		@Override
		public void failed(Question question) {
			if(!set.contains(question)) {
				set.add(question);
				questions.add(question);
			}
			else
				done.set(done.get() + 1);
		}

		@Override
		public void setQuestions(Collection<Question> questions) {
			super.setQuestions(questions);
			set.clear();
		}

	}

}

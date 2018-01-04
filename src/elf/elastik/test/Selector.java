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
import java.util.Iterator;
import java.util.Random;

import elf.elastik.data.Question;

/**
 * Interface for selecting a question.
 * @author casse
 */
public interface Selector {

	/**
	 * Called to select a question.
	 * @param questions	Collection of questions.
	 * @return			Selected question (not empty).
	 */
	Question select(Collection<Question> questions);
	
	/**
	 * Reset the selector state. 
	 */
	void reset();
	
	/**
	 * Selector that takes question in-order.
	 * @author casse
	 */
	public static class InOrderSelector implements Selector {

		@Override
		public Question select(Collection<Question> questions) {
			return questions.iterator().next();
		}

		@Override
		public void reset() {
		}
		
	}

	/**
	 * Selector that takes a random question.
	 * @author casse
	 *
	 */
	public static class RandomSelector implements Selector {
		Random random = new Random();
		
		@Override
		public Question select(Collection<Question> questions) {
			if(questions.size() == 1)
				return questions.iterator().next();
			else {
				int n = random.nextInt(questions.size());
				Iterator<Question> q = questions.iterator();
				for(int i = 0; i < n; i++)
					q.next();
				return q.next();				
			}
		}

		@Override
		public void reset() {
		}
		
	}

}

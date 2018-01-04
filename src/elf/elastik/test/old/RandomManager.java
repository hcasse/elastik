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

import java.util.Random;

/**
 * Random selector of questions.
 * @author casse
 */
public class RandomManager implements Manager {
	private Random random = new Random();
	private Question last;

	@Override
	public Question next(BasicTest test) {
		int i = random.nextInt(test.getQuestionNumber() - test.getDoneNumber());
		last = test.get(i);
		return last;
	}

	@Override
	public String check(BasicTest test, String answer) {
		String r = last.check(answer);
		if(r == null)
			test.succeeded(last);
		else
			test.failed(last);
		return r;
	}

	@Override
	public void reset() {
	}

}

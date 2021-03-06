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
package elf.elastik;

import java.util.Locale;
import java.util.Vector;

import elf.app.AutoConfiguration;

/**
 * Configuration of the application.
 * @author casse
 */
public class Configuration extends AutoConfiguration {

	public String fname = "", lname = "", nat;
	public Vector<String> langs = new Vector<String>();
	public boolean repeat = false;

	public Configuration(Main main) {
		super(main, "config");
		nat = Locale.getDefault().getLanguage();
	}

	public void addLanguage(String lang) {
		langs.add(lang);
		modify();
	}


	public void removeLanguage(String lang) {
		langs.remove(lang);
		modify();
	}

	public boolean getRepeat() {
		return repeat;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
		modify();
	}

}

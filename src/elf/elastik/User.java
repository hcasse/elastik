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

/**
 * Represents a simple user.
 * @author casse
 */
public class User {
	String fname, lname, lang;
	
	public User(String fname, String lname, String lang) {
		this.fname = fname;
		this.lname = lname;
		this.lang = lang;
	}
	
	/**
	 * Get the first name.
	 * @return	First name.
	 */
	public String getFirstName() {
		return fname;
	}
	
	/**
	 * Get the last name.
	 * @return	Last name.
	 */
	public String getLastName() {
		return lname;
	}
	
	/**
	 * Get the native language of the user.
	 * @return	Native language.
	 */
	public String getLanguage() {
		return lang;
	}
}

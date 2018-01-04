/*
 * Elastik Application
 * Copyright (c) 2016 - Hugues Cassé <hugues.casse@laposte.net>
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
package elf.elastik.data;

/**
 * Field of the model.
 * @author casse
 */
public class Field {
	private String name;
	private boolean key;
	int index;
	
	/**
	 * Build a field that is not a key.
	 * @param name	Field name.
	 */
	public Field(String name) {
		this.name = name;
	}
	
	/**
	 * Build a field and set if it is a key field.
	 * @param name	Field name.
	 * @param key	True for a key, false for not a key.
	 */
	public Field(String name, boolean key) {
		this.name = name;
		this.key = key;
	}

	public String getName() {
		return name;
	}
	
	public int getIndex() {
		return index;
	}

	public boolean isKey() {
		return key;
	}
}

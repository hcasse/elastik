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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class represents models of questions, i.e. the field
 * composing a question.
 * @author casse
 */
public class Model implements Iterable<Field> {
	public static final int
		VERTICAL = 0,
		HORIZONTAL = 1;
	public static final HashMap<String, Model> map = new HashMap<String, Model>();
	public static final Model
		SINGLE_WORD = new Model("Single Word",
			new Field("@@native@@ Word", true),
			new Field("@@foreign@@ Word", true)),
		IRREGULAR_VERB = new Model("Irregular Verb",
			new Field("Infinitive", true),
			new Field("Simple Past", true),
			new Field("Past Participle", true)),
		CONJUGATION = new Model("Conjugation",
			HORIZONTAL,
			new Field("Infinitive", true),
			new Field("@@$1@@", false),
			new Field("@@$2@@", false),
			new Field("@@$3@@", false),
			new Field("@@$1s@@", false),
			new Field("@@$2s@@", false),
			new Field("@@$3s@@", false));

	private String name;
	private String uri;
	private Vector<Field> fields = new Vector<Field>();
	private int type;

	/**
	 * Get a model by its URI.
	 * @param uri	Model URI.
	 * @return		Found model or null.
	 */
	public static Model get(String uri) {
		return map.get(uri);
	}
	
	/**
	 * Get all available models.
	 * @return	Available models.
	 */
	public static Collection<Model> getModels() {
		return map.values();
	}
	
	public Model(String name) {
		this.name = name;
		map.put(getURI(), this);
	}
	
	public Model(String name, Field...fields) {
		this.name = name;
		for(Field field: fields)
			add(field);
		map.put(getURI(), this);
		type = VERTICAL;
	}
	
	public Model(String name, String uri) {
		this.name = name;
		this.uri = uri;
		map.put(uri, this);
		type = VERTICAL;
	}
	
	public Model(String name, String uri, Field... fields) {
		this.name = name;
		this.uri = uri;
		for(Field field: fields)
			add(field);
		map.put(uri, this);
		type = VERTICAL;
	}
	
	public Model(String name, int type, Field...fields) {
		this.name = name;
		for(Field field: fields)
			add(field);
		map.put(getURI(), this);
		this.type = type;
	}
	
	public Model(String name, String uri, int type) {
		this.name = name;
		this.uri = uri;
		map.put(uri, this);
		this.type = type;
	}
	
	public Model(String name, String uri, int type, Field... fields) {
		this.name = name;
		this.uri = uri;
		for(Field field: fields)
			add(field);
		map.put(uri, this);
		this.type = type;
	}
	
	/**
	 * Get the type of display for the model.
	 * @return	One of VERTICAL or HORIZONTAL.
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Count the number of fields.
	 * @return	Number of fields.
	 */
	public int count() {
		return fields.size();
	}

	/**
	 * Get the name of the model.
	 * @return	Model index.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the URI of the model.
	 * @return	Model URI.
	 */
	public String getURI() {
		if(uri == null)
			try {
				uri = "elastik-model://" + URLEncoder.encode(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO	Log it somewhere.
			};
		return uri;
	}
	
	/**
	 * Add a field to the model.
	 * @param field		Added field.
	 */
	public void add(Field field) {
		field.index = fields.size();
		fields.add(field);
	}

	@Override
	public Iterator<Field> iterator() {
		return fields.iterator();
	}
	
	/**
	 * Get the list of keys of the model.
	 * @return	List of keys.
	 */
	public Collection<Field> getKeys() {
		Vector<Field> keys = new Vector<Field>();
		for(Field field: this)
			if(field.isKey())
				keys.add(field);
		return keys;
	}
	
	/**
	 * Provide string output for the given field.
	 * @param quest		Question to display.
	 * @return			Matching string.
	 */
	public String toString(Question quest) {
		assert quest.getModel() != this;
		StringBuffer buf = new StringBuffer();
		boolean first = true;
		for(Field field: getKeys()) {
			if(first)
				first = false;
			else
				buf.append(" / ");
			buf.append(quest.get(field));
		}
		return buf.toString();
	}
}

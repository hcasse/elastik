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

import java.util.Hashtable;
import java.util.Vector;

import elf.elastik.data.Field;
import elf.elastik.data.Model;
import elf.elastik.data.Question;
import elf.elastik.data.Theme;
import elf.ui.Icon;
import elf.ui.Component;
import elf.ui.Form;
import elf.ui.meta.Action;
import elf.ui.meta.CollectionVar;
import elf.ui.meta.Var;

/**
 * Page to edit a word.
 * +------------------+
 * | native   [     ] |
 * | foreign  [     ] |
 * |          [+][++] |
 * +------------------+
 * @author casse
 */
public class QuestionPage extends ApplicationPage {
	private static Hashtable<Model, QuestionPage> word_pages = new Hashtable<Model, QuestionPage>(); 
	private Var<LanguageModel> lang;
	private Vector<FieldVar> vars = new Vector<FieldVar>();
	private CollectionVar<Question> questions;
	private Model model;
	
	private Action add_one = new Action() {
		@Override public void run() { addOne(); }
		@Override public boolean isEnabled() { return check(); }
		@Override public String getHelp() { return app.t("Add one question and leave."); }
		@Override public Icon getIcon() { return Main.getIcon("add_one"); }
		@Override public String getLabel() { return app.t("Add"); }
	};

	private Action add_multi = new Action() {
		@Override public void run() { addMulti(); }
		@Override public boolean isEnabled() { return check(); }
		@Override public String getHelp() { return app.t("Add one question and continue."); }
		@Override public Icon getIcon() { return Main.getIcon("add_multi"); }
		@Override public String getLabel() { return app.t("Next"); }
	};

	/**
	 * Add a new question.
	 * @param lang	Current language.
	 * @param theme	Current theme.
	 */
	static QuestionPage get(Window window, Var<LanguageModel> lang, Var<Theme> theme, CollectionVar<Question> questions) {
		QuestionPage page = word_pages.get(theme.get().getModel());
		if(page == null) {
			page = new QuestionPage(window, lang, theme.get().getModel(), questions);
			word_pages.put(theme.get().getModel(), page);
		}
		return page;
	}

	/**
	 * Build a question page.
	 * @param window		Owner window.
	 * @param lang			Variable for current language.
	 * @param model			Page model.
	 * @param questions		Variable for list of questions.
	 */
	private QuestionPage(Window window, Var<LanguageModel> lang, Model model, CollectionVar<Question> questions) {
		super(window);
		this.lang = lang;
		this.model = model;
		this.questions = questions;
	}
	
	/**
	 * Check if the words are ready.
	 * @return	True if check is successful, false else.
	 */
	private boolean check() {
		for(Var<String> var: vars)
			if("".equals(var.get()))
				return false;
		return true;
	}
	
	/**
	 * Start a new question edition.
	 */
	public void newQuestion() {
		for(Var<String> var: vars)
			var.set("");
	}
	
	/**
	 * Add a new question.
	 *
	 */
	public void addQuestion() {
		lang.get().get().modify();
		Question quest = new Question(model);
		for(FieldVar var: vars)
			var.set(quest);
		questions.add(quest);
		lang.get().get().getAll().add(quest);
	}
	
	/**
	 * Add only one word.
	 */
	public void addOne() {
		addQuestion();
		newQuestion();
		window.doBack();
	}

	/**
	 * Add several words.
	 */
	public void addMulti() {
		addQuestion();
		newQuestion();
	}

	@Override
	public String getTitle() {
		return app.t("Adding questions");
	}

	@Override
	public void make() {
		
		// prepare form
		Form form = page.addForm();
		form.addAction(add_multi);
		switch(model.getType()) {
		case Model.HORIZONTAL:
			form.setStyle(Form.STYLE_TWO_COLUMN);
			break;
		case Model.VERTICAL:
		default:
			form.setStyle(Form.STYLE_VERTICAL);
			break;
		}
		form.addAction(add_one);
		form.setButtonAlignment(Component.RIGHT);
		
		// add fields
		for(final Field field: model) {
			FieldVar var = new FieldVar(field);
			vars.add(var);
			form.addTextField(var);
			add_one.add(var);
			add_multi.add(var);
		}
		
	}
	
	@Override
	public void onShow() {
		super.onShow();
		for(FieldVar var: vars)
			var.fireEntityChange();
	};
	
	/**
	 * Variable for fields.
	 * @author casse
	 */
	public class FieldVar extends Var<String> {
		private Field field;
		
		public FieldVar(Field field) {
			super("");
			this.field = field;
		}
		
		public void set(Question quest) {
			quest.set(field, get());
		}

		@Override
		public String getLabel() {
			return lang.get().get().t(field.getName(), app.i18n);
		}

	}

}

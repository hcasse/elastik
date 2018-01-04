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

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import elf.elastik.data.Field;
import elf.elastik.data.Model;
import elf.elastik.data.Question;
import elf.elastik.data.Theme;
import elf.elastik.test.Manager;
import elf.elastik.test.Selector;
import elf.elastik.test.SingleKeyTest;
import elf.elastik.test.Test;
import elf.ui.AbstractDisplayer;
import elf.ui.ActionBar;
import elf.ui.Box;
import elf.ui.Component;
import elf.ui.Container;
import elf.ui.Form;
import elf.ui.SubsetField;
import elf.ui.meta.Action;
import elf.ui.meta.CollectionVar;
import elf.ui.meta.Var;
import elf.ui.meta.Var.ChangeListener;

/**
 * Configuration page for learn phase.
 *
 * +--------+--------+
 * | config | themes |
 * | option |        |
 * |		|		 |
 * +--------+--------+
 * 			   [start]
 *
 * @author casse
 */
public class ConfigPage extends ApplicationPage {
	private Var<LanguageModel> current_language;
	private CollectionVar<Theme> themes = new CollectionVar<Theme>();
	private CollectionVar<Theme> subset = new CollectionVar<Theme>();
	private Var<TestMaker> choice = new Var<TestMaker>() {
		@Override public String getLabel() { return app.t("Type"); }
		@Override public String getHelp() { return app.t("Select the type of question"); }
	};
	private CollectionVar<TestMaker> choices = new CollectionVar<TestMaker>();
	private final Var<Boolean> repeat =
		new Var.Config<Boolean>(app.config, "repeat") {
			@Override public String getLabel() { return app.t("Repeat"); }
			@Override public String getHelp() { return app.t("Repeat once failed words."); }
		};

	private final Action train = new Action() {
		@Override public void run() { doTrain(); }
		@Override public String getLabel() { return app.t("Train"); }
		@Override public String getHelp() { return app.t("Start a learning session."); }
		@Override public boolean isEnabled() {
			boolean enabled = false;
			for(Theme theme: subset.getCollection())
				if(!theme.getQuestions().isEmpty()) {
					enabled = true;
					break;
				}
			return enabled;
		}
	};
	
	private final ChangeListener<LanguageModel> listener = new ChangeListener<LanguageModel>() {
		@Override public void onChange(Var<LanguageModel> var)
			{ ConfigPage.this.onChange(var); }
	};

	private Model model;

	public ConfigPage(Window window, Var<LanguageModel> current_language) {
		super(window);
		this.current_language = current_language;
		this.getPage().listenExtern(current_language, listener);
	}

	@Override
	public String getTitle() {
		return String.format(app.t("Learning %s"), Locale.forLanguageTag(current_language.get().get().getName()).getDisplayLanguage());
	}

	@Override
	public void make() {
		Container body = page.addBox(Component.VERTICAL);
		Box hbody = body.addBox(Component.HORIZONTAL);
		hbody.setAlign(Component.TOP);
		Form form = hbody.addForm();
		form.addAction(train);
		form.setStyle(Form.STYLE_TWO_COLUMN);
		form.addChoiceField(choice, choices);
		form.addCheckBox(repeat);
		form.setButtonVisible(false);

		SubsetField<Theme> sset = hbody.addSubsetField(themes);
		sset.setDisplayer(new AbstractDisplayer<Theme>() {
			@Override public String asString(Theme theme) {
				return String.format(app.t("%s (%d questions)"), theme.getName(), theme.getQuestions().size());
			}
		});
		sset.setSubset(subset);
		train.add(subset);
		ActionBar bar = body.addActionBar();
		bar.add(train);
		bar.setAlignment(Component.RIGHT);
		
		subset.addListener(new CollectionVar.Listener<Theme>() {
			@Override public void onAdd(Theme item) { if(subset.size() == 1) updateChoices(item); }
			@Override public void onRemove(Theme item) { if(subset.size() == 0) resetChoices(); }
			@Override public void onClear() { resetChoices(); }
			@Override public void onChange() { if(subset.size() >= 1) updateChoices(subset.iterator().next()); }
		});
		
	}

	/**
	 * Update the choices according to the selected model.
	 * @param theme		Selected theme.
	 */
	private void updateChoices(Theme theme) {
		if(theme.getModel() != model) {
			model = theme.getModel();
			choices.clear();
			for(Field key: model.getKeys())
				choices.add(new SingleFieldMaker(key));
			Vector<Theme> to_remove = new Vector<Theme>();
			for(Theme t: themes)
				if(t.getModel() != model)
					to_remove.add(t);
			themes.remove(to_remove);
		}
	}
	
	/**
	 * Remove all choices.
	 */
	private void resetChoices() {
		model = null;
		choices.clear();
		themes.clear();
		themes.add(current_language.get().get().getThemes());
	}
	
	private void onChange(Var<LanguageModel> data) {
		choices.clear();
		subset.clear();
		themes.clear();
		themes.add(current_language.get().get().getThemes());
	}

	/**
	 * Do a a training sessions.
	 */
	private void doTrain() {

		// build selector and manager
		Manager manager;
		if(this.repeat.get())
			manager = new Manager.TwoShots();
		else
			manager = new Manager.OneShot();
		manager.setSelector(new Selector.RandomSelector());

		// build the test
		Set<Question> questions = new HashSet<Question>();
		for(Theme theme: subset)
			questions.addAll(theme.getQuestions());
		Test test = choice.get().make(questions, manager);
		
		// launch the test
		TrainPage.run(window, test);
	}
	
	/**
	 * Interface for the builde of tests.
	 * @author casse
	 */
	public interface TestMaker {
		static final TestMaker NULL = new TestMaker() {
			@Override public Test make(Collection<Question> questions, Manager manager) { return null; }
			@Override public String toString() { return "null"; }
		};
		
		Test make(Collection<Question> questions, Manager manager);

	}
	
	private class SingleFieldMaker implements TestMaker {
		Field key;
		
		public SingleFieldMaker(Field key) {
			this.key = key;
		}
		
		@Override
		public Test make(Collection<Question> questions, Manager manager) {
			return new SingleKeyTest(current_language.get(), model, key, questions, manager);
		}

		@Override
		public String toString() {
			return String.format(app.t("Ask by %s"), current_language.get().get().t(key.getName(), app.i18n));
		}
		
	}

}

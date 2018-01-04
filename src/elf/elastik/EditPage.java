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
import java.util.Locale;
import java.util.Vector;

import elf.elastik.data.Model;
import elf.elastik.data.Question;
import elf.elastik.data.Theme;
import elf.ui.Dialog;
import elf.ui.Displayer;
import elf.ui.ErrorManager;
import elf.ui.Form;
import elf.ui.Icon;
import elf.ui.ActionBar;
import elf.ui.Button;
import elf.ui.ChoiceField;
import elf.ui.Component;
import elf.ui.Container;
import elf.ui.AbstractDisplayer;
import elf.ui.List;
import elf.ui.SplitPane;
import elf.ui.meta.AbstractEntity;
import elf.ui.meta.Action;
import elf.ui.meta.Check;
import elf.ui.meta.CheckedAction;
import elf.ui.meta.CollectionVar;
import elf.ui.meta.Label;
import elf.ui.meta.Var;
import elf.ui.meta.Var.ChangeListener;

/**
 * Page for editing a language.
 *
 * +---------+-----------+
 * | Theme   | Word in   |
 * | list    | the theme |
 * |         |           |
 * +---------+-----------+
 *  [+][-]    [+][-]
 *
 * @author casse
 */
public class EditPage extends ApplicationPage implements ChangeListener<LanguageModel> {
	private boolean updated = false;
	private final Var<LanguageModel> current_language;
	private final Var<Theme> current_theme = new Var<Theme>();
	private final CollectionVar<Question> current_words = new CollectionVar<Question>();
	private final CollectionVar<Theme> themes = new CollectionVar<Theme>(new Vector<Theme>());
	private final CollectionVar<Question> words = new CollectionVar<Question>(new Vector<Question>());
	private CreationDialog dialog;

	/**
	 * Build the page.
	 * @param window	Owner window.
	 */
	public EditPage(Window window, Var<LanguageModel> current_language) {
		super(window);
		this.current_language = current_language;
		current_language.add(this);
	}

	/**
	 * Get the current theme.
	 * @return	Current theme.
	 */
	public Var<Theme> getCurrentTheme() {
		return current_theme;
	}

	@Override
	public void onChange(Var<LanguageModel> data) {
		updated = true;
	}

	@Override
	public String getTitle() {
		return String.format(app.t("Editing %s"), Locale.forLanguageTag(current_language.get().get().getName()).getDisplayLanguage());
	}

	@Override
	public void onShow() {
		super.onShow();
		getPage();
		if(updated) {
			themes.setCollection(current_language.get().get().getThemes());
			updated = false;
		}
	}

	/**
	 * Get the variable of words.
	 * @return
	 */
	public CollectionVar<Question> getWords() {
		return words;
	}

	@Override
	protected void make() {

		// prepare the page
		SplitPane spane = page.addSplitPane(Component.VERTICAL);

		// make theme list
		Container tc = spane.getFirst();
		List<Theme> theme_list = tc.addList(themes);
		theme_list.setSelector(current_theme);
		theme_list.setDisplayer(new AbstractDisplayer<Theme>() {
			@Override public String asString(Theme value) { return value.getName(); }
		});
		ActionBar ta = tc.addActionBar();
		ta.setStyle(Button.STYLE_ICON);
		Action add_theme = new Action() {
			@Override public String getLabel() { return "Add"; }
			@Override public void run() { addTheme(); }
			@Override public Icon getIcon() { return Main.getIcon("list_add"); }
			@Override public String getHelp() { return app.t("Create a new theme."); }
		};
		ta.add(add_theme);
		Action remove_theme =  window.getView().makeValidatedAction(new Action() {
			@Override public String getLabel() { return "Remove"; }
			@Override public void run() { themes.remove(current_theme.get()); }
			@Override public Icon getIcon() { return Main.getIcon("list_remove"); }
			@Override public boolean isEnabled() {
				return current_theme.get() != null
					&& !EditPage.this.current_language.get().get().isAll(current_theme.get());
			}
			@Override public String getHelp() { return app.t("Remove the selected theme."); }
		}, new Label(app.t("Do you want to remove this theme?")));
		remove_theme.add(current_theme);
		ta.add(remove_theme);

		// make word list
		Container wc = spane.getSecond();
		List<Question> word_list = wc.addList(words);
		word_list.setSelector(current_words);
		current_theme.add(new ChangeListener<Theme>() {
			@Override public void onChange(Var<Theme> data) {
				if(data.get() == null)
					words.setCollection(new Vector<Question>());
				else
					words.setCollection(current_theme.get().getQuestions());
			}
		});
		ActionBar wa = wc.addActionBar();
		wa.setStyle(Button.STYLE_ICON);
		Action add_word = new Action() {
			@Override public String getLabel() { return "Add"; }
			@Override public void run() { window.add(QuestionPage.get(window, current_language, current_theme, words)); }
			@Override public Icon getIcon() { return Main.getIcon("list_add"); }
			@Override public boolean isEnabled() { return current_theme.get() != null; }
			@Override public String getHelp() { return app.t("Add a new word to the theme."); }
		};
		add_word.add(current_theme);
		wa.add(add_word);
		Action remove_word =  new Action() {
			@Override public String getLabel() { return "Remove"; }
			@Override public void run() { words.remove(current_words.getCollection()); EditPage.this.current_language.get().get().modify();  }
			@Override public Icon getIcon() { return Main.getIcon("list_remove"); }
			@Override public boolean isEnabled() { return !current_words.isEmpty(); }
			@Override public String getHelp() { return app.t("Remove the selected words from the theme."); }
		};
		remove_word.add(word_list.getMultiSelector());
		wa.add(remove_word);
		Action copy_word =  new Action() {
			@Override public String getLabel() { return "Copy"; }
			@Override public void run() { moveWords(current_words.getCollection()); }
			@Override public Icon getIcon() { return Main.getIcon("copy"); }
			@Override public boolean isEnabled() { return !current_words.isEmpty() && current_language.get().get().getThemes().size() > 2; }
			@Override public String getHelp() { return app.t("Copy the selected words to another theme."); }
		};
		copy_word.add(word_list.getMultiSelector());
		wa.add(copy_word);
	}

	/**
	 * Implements the creation of a theme.
	 */
	private void addTheme() {
		if(dialog == null)
			dialog = new CreationDialog();
		dialog.run();
	}
	
	/**
	 * Ask the user to select the target theme and move words
	 * to the target theme.
	 * @param words		Words to move.
	 */
	private void moveWords(Collection<Question> words) {
		Vector<Theme> themes = new Vector<Theme>();
		for(Theme theme: this.themes.getCollection())
			if(theme != current_theme.get() && theme != current_language.get().get().getAll())
				themes.add(theme);
		Theme theme = window.getView().makeSelectionDialog(app.t("Select the theme to copy words to."), app.t("Word Copy"), themes).show();
		if(theme != null)
			for(Question word: current_words.getCollection())
				theme.add(word);
	}

	/**
	 * Create a theme with the given name.
	 * @param name	Theme name.
	 * @param model	Theme model.
	 */
	private void createTheme(String name, Model model) {
		this.themes.add(new Theme(current_language.get().get(), name, model));
		current_language.get().get().modify();
	}

	private class CreationDialog extends AbstractEntity {
		Dialog dialog;
		Var<String> name = new Var<String>("") {
			@Override public String getLabel() { return app.t("name"); }
		};
		Var<Model> model = new Var<Model>(Model.SINGLE_WORD) {
			@Override public String getLabel() { return app.t("model"); }			
		};

		public CreationDialog() {
			dialog = window.getView().makeDialog(this);
			ErrorManager eman = dialog.addErrorManager();
			Form form = eman.addForm();
			form.addTextField(name);
			ChoiceField<Model> choice = form.addChoiceField(model, new CollectionVar<Model>(Model.getModels()));
			choice.setDisplayer(new Displayer<Model>() {
				@Override public String asString(Model value) { return app.t(value.getName()); }
				@Override public Icon getIcon(Model value) { return null; }
			});
			form.setEnterMode(Form.ENTER_NEXT_AND_SUBMIT);
			form.setButtonVisible(false);
			Check check1 = new Check(form, "Theme already exists!", Check.var(name)) {
				@Override protected boolean check() {
					for(Theme theme: current_language.get().get().getThemes())
						if(theme.getName().equalsIgnoreCase(name.get()))
							return false;
					return true;
				}
			};
			Check check2 = new Check(form, "Empty name!", Check.var(name)) {
				@Override protected boolean check() { return !name.get().equals(""); }
			};
			dialog.addCancel();
			CheckedAction action = new CheckedAction() {
				@Override public void run() { createTheme(name.get(), model.get()); }
				@Override public String getLabel() { return app.t("Create"); }
				@Override public Icon getIcon() { return Main.getIcon("list_add"); }
			};
			form.addMainAction(dialog.add(action));
			action.add(check1);
			action.add(check2);
		}

		@Override
		public String getLabel() {
			return app.t("Theme Creation");
		}

		public void run() {
			dialog.run();
		}
	}
}

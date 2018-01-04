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

import java.util.Comparator;
import java.util.Locale;
import java.util.TreeSet;
import java.util.Vector;

import elf.ui.ActionBar;
import elf.ui.Component;
import elf.ui.Container;
import elf.ui.AbstractDisplayer;
import elf.ui.Icon;
import elf.ui.List;
import elf.ui.SelectionDialog;
import elf.ui.meta.Action;
import elf.ui.meta.Var;
import elf.ui.meta.Label;


/**
 * Main page.
 * +-------+---------+
 * | en    | new     |
 * | es    | learn   |
 * |       | edit    |
 * |       | delete  |
 * +-------+---------+
 * @author casse
 */
public class MainPage extends ApplicationPage {
	private final Var<LanguageModel> lang;
	
	Action add = new Action() {
		@Override public String getLabel() { return app.t("Add"); }
		@Override public void run() { addLanguage(); }
		@Override public Icon getIcon() { return Main.iman.get("add_lang"); }
	};

	Action learn = new Action() {
		@Override public String getLabel() { return app.t("Train"); }
		@Override public void run() { window.doLearn(); }
		@Override public boolean isEnabled() { return lang.get() != null && !lang.get().get().isEmpty(); }
		@Override public Icon getIcon() { return Main.iman.get("learn"); }
	};

	Action edit = new Action() {
		@Override public String getLabel() { return app.t("Edit"); }
		@Override public void run() { window.doEdit(); }
		@Override public boolean isEnabled() { return lang.get() != null; }
		@Override public Icon getIcon() { return Main.iman.get("edit_lang"); }
	};

	Action remove = window.getView().makeValidatedAction(
		new Action() {
			@Override public Icon getIcon() { return Main.iman.get("rem_lang"); }
			@Override public String getLabel() { return app.t("Remove"); }
			@Override public void run() { removeLanguage(); }
			@Override public boolean isEnabled() { return lang != null && lang.get() != null; }
		},
		new Label("") {
			@Override public String getLabel() { return String.format(app.t("Do you want to remove %s?"), lang.get().get().getName()); }
		});
	
	public MainPage(Window window, Var<LanguageModel> language) {
		super(window);
		this.lang = language;
	}

	/**
	 * Open a dialog to select a new language.
	 */
	private void addLanguage() {
		
		// build the list of locales
		TreeSet<Locale> set = new TreeSet<Locale>(new Comparator<Locale>() {
			@Override public int compare(Locale a, Locale b) {
				return a.getDisplayName().compareToIgnoreCase(b.getDisplayName());
			}
		});
		for(Locale locale: Locale.getAvailableLocales()) {
			String id = locale.getLanguage();
			if(locale.getCountry().isEmpty()
			&& locale.getVariant().isEmpty()
			&& !id.equals(app.config.nat)
			&& !app.config.langs.contains(id))
				set.add(locale);
		}
		Vector<Locale> locs = new Vector<Locale>();
		locs.addAll(set);
		
		// open the dialog
		SelectionDialog<Locale> dialog = window.getView().makeSelectionDialog(
				app.t("Select the language:"),
				app.t("Language selection"),
				locs);
		dialog.setAction(app.t("Select"));
		dialog.setDisplayer(new AbstractDisplayer<Locale>() {
			@Override public String asString(Locale value) { return value.getDisplayLanguage(); }
		});
		Locale loc = dialog.show();
		
		// create the new language
		if(loc != null)
			app.addLanguage(loc.getLanguage());
	}
	
	/**
	 * Remove the current language.
	 */
	private void removeLanguage() {
		LanguageModel lang = this.lang.get();
		app.removeLanguage(lang);
	}

	@Override
	public String getTitle() {
		return app.getName() + " " + app.getVersion();
	}

	@Override
	protected void make() {
		
		// set dependencies
		learn.add(lang);
		learn.add(app.langs);
		edit.add(lang);
		edit.add(app.langs);
		remove.add(lang);
		remove.add(app.langs);

		// set the body
		Container body = page.addBox(Component.HORIZONTAL);
		
		// build the list
		List<LanguageModel> list = body.addList(app.langs);
		list.setSelector(lang);
		list.setDisplayer(new AbstractDisplayer<LanguageModel>() {
			
			@Override public String asString(LanguageModel value) {
				return Main.getLanguageDisplay(value.getID());
			}

			@Override
			public Icon getIcon(LanguageModel value) {
				return Main.getLanguageIcon(value.getID());
			}
			
		});
		
		// build the button bar
		ActionBar abar = body.addActionBar();
		abar.setAlignment(ActionBar.SPREAD);
		abar.setAxis(Component.VERTICAL);
		abar.add(add);
		abar.add(learn);
		abar.add(edit);
		abar.add(remove);
	}

}

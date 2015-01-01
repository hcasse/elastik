package elf.elastik;

import java.util.Comparator;
import java.util.Locale;
import java.util.TreeSet;
import java.util.Vector;

import elf.ui.ActionBar;
import elf.ui.Component;
import elf.ui.Container;
import elf.ui.Displayer;
import elf.ui.List;
import elf.ui.PagePane;
import elf.ui.PagePane.Page;
import elf.ui.SelectionDialog;
import elf.ui.meta.Action;
import elf.ui.meta.Var;


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
public class MainPage implements ApplicationPage {
	private final Main app;
	private final Window window;
	private final PagePane.Page page;
	private final Var<LanguageModel> lang;
	
	Action add = new Action() {
		@Override public String getLabel() { return app.t("Add"); }
		@Override public void run() { addLanguage(); }
	};

	Action learn = new Action() {
		@Override public String getLabel() { return app.t("Learn"); }
		@Override public void run() { window.doLearn(); }
		@Override public boolean isEnabled() { return lang.get() != null && !lang.get().get().isEmpty(); }
	};

	Action edit = new Action() {
		@Override public String getLabel() { return app.t("Edit"); }
		@Override public void run() { window.doEdit(); }
		@Override public boolean isEnabled() { return lang.get() != null; }
	};

	Action remove = new Action() {
		@Override public String getLabel() { return app.t("Remove"); }
		@Override public void run() { removeLanguage(); }
		@Override public boolean isEnabled() { return lang.get() != null; }
	};
	
	public MainPage(Window window, Var<LanguageModel> language) {
		this.window = window;
		this.app = window.getApplication();
		this.page = window.makePage();
		this.lang = language;
		
		// set dependencies
		learn.add(language);
		learn.add(app.langs);
		edit.add(language);
		edit.add(app.langs);
		remove.add(language);
		remove.add(app.langs);

		// set the body
		Container body = page.addBox(Component.HORIZONTAL);
		
		// build the list
		List<LanguageModel> list = body.addList(app.langs);
		list.setSelector(language);
		list.setDisplayer(new Displayer<LanguageModel>() {
			@Override public String asString(LanguageModel value) {
				return Locale.forLanguageTag(value.getID()).getDisplayName();
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
		dialog.setDisplayer(new Displayer<Locale>() {
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
		Locale loc = Locale.forLanguageTag(lang.getID());
		if(window.getView().showConfirmDialog(
		String.format(app.t("Do you want to remove %s?"), loc.getDisplayName()),
		app.t("Language Removal")))
			app.removeLanguage(lang);
	}

	@Override
	public String getTitle() {
		return app.getName() + " " + app.getVersion();
	}
	
	@Override
	public Page getPage() {
		return page;
	}

}

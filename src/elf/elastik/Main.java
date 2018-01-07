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

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Locale;

import elf.data.Version;
import elf.elastik.data.Language;
import elf.ui.Icon;
import elf.ui.I18N;
import elf.ui.meta.CollectionVar;
import elf.ui.meta.LateLoader;

/**
 * Elastic entry point.
 * @author casse
 */
public class Main extends elf.app.Application {
	public static final String APP_NAME = "Elastik";
	public static final Version APP_VERSION = new Version(1, 4, 0);
	private static final Hashtable<String, String> lang_names = new Hashtable<String, String>();
	private static final Hashtable<String, Icon> lang_icons = new Hashtable<String, Icon>();
	Configuration config = new Configuration(this);
	CollectionVar<LanguageModel> langs = new CollectionVar<LanguageModel>(new LinkedList<LanguageModel>());
	I18N i18n;
	static Icon.Manager iman = new Icon.Manager(Main.class.getResource("/pix/"));
	private LanguageModel.Context context;
	
	public Main() {
		super(APP_NAME, APP_VERSION);
		i18n = new I18N(APP_NAME, new Locale[] { Locale.getDefault()});
		context = new LanguageModel.Context(this);
	}

	/**
	 * Get an icon from its name.
	 * @param name	Icon name.
	 * @return		Found icon.
	 */
	public static Icon getIcon(String name) {
		Icon icon = iman.get(name);
		return icon;
	}

	/**
	 * Perform a translation.
	 * @param s		String to translate.
	 * @return		Translation.
	 */
	public String t(String s) {
		return i18n.t(s);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main main = new Main();
		main.run(args);
	}

	@Override
	protected void proceed() {

		// prepare the list of languages
		for(String lang: config.langs)
			langs.add(new LanguageModel(context, lang) {
				@Override
				public Language load(String id) throws IOException {
					Language lang = new Language(config.nat, id, false, t("all"));
					lang.load();
					return lang;
				}
			} );
		new Window(this);
	}

	/**
	 * Add a language.
	 * @param name	Added language name.
	 */
	public void addLanguage(String name) {
		langs.add(new LanguageModel(context, new Language(config.nat, name, true, t("all"))));
		config.addLanguage(name);
	}

	/**
	 * Remove a language.
	 * @param lang	Removed language.
	 */
	public void removeLanguage(LanguageModel lang) {
		langs.remove(lang);
		config.removeLanguage(lang.getID());
	}

	@Override
	protected void cleanup() {
		super.cleanup();

		// save languages
		for(LateLoader<Language, String> lang: langs)
			if(lang.isLoaded() && lang.get().isModified())
				try {
					lang.get().save();
				} catch (IOException e) {
					System.err.printf("ERROR: cannot save %s: %s", lang.get().getName(), e.getLocalizedMessage());
				}
	}

	/**
	 * Get the display name from a language tag.
	 * @param tag	Language tag.
	 * @return		Display name.
	 */
	public static String getLanguageDisplay(String tag) {
		String name = lang_names.get(tag);
		if(name == null) {
			name = Locale.forLanguageTag(tag).getDisplayLanguage();
			lang_names.put(tag, name);
		}
		return name;
	}

	/**
	 * Get the language icon for a language tag.
	 * @param tag	Language tag.
	 * @return		Matching icon or null.
	 */
	public static Icon getLanguageIcon(String tag) {
		Icon icon = lang_icons.get(tag);
		if(icon == null) {
			icon = getIcon("flag-" + tag);
			try {
				InputStream stream = icon.getURL().openStream();
				stream.close();
			} catch (IOException e) {
				icon = Icon.BROKEN;
			}
			lang_icons.put(tag, icon);
		}
		if(icon == Icon.BROKEN)
			return null;
		else
			return icon;
	}

	@Override public String getLicense() { return "Copyright (c) 2015 - LGPL v3"; }
	@Override public String getSite() { return "http://www.elastik.fr"; }
	@Override public String[] getAuthors() { return new String[] { "Hugues Cassé" }; }
	@Override public Icon getLogo() { return getIcon("logo"); }

}

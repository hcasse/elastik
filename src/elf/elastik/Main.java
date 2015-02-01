package elf.elastik;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;

import elf.data.Version;
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
	Configuration config = new Configuration(this);
	CollectionVar<LanguageModel> langs = new CollectionVar<LanguageModel>(new LinkedList<LanguageModel>());
	I18N i18n;
	Icon.Manager iman = new Icon.Manager(Main.class.getResource("/pix/"));
	private LanguageModel.Context context;

	public Main() {
		super(APP_NAME, new Version(1, 0, 0));
		i18n = new I18N(APP_NAME, new Locale[] { Locale.getDefault()});
		context = new LanguageModel.Context(this);
	}

	/**
	 * Get an icon from its name.
	 * @param name	Icon name.
	 * @return		Found icon.
	 */
	public Icon getIcon(String name) {
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
		return Locale.forLanguageTag(tag).getDisplayLanguage();
	}

	@Override
	public String getLicense() {
		return "Copyright (c) 2015 - LGPL v3";
	}

	@Override
	public String getSite() {
		return "http://www.elastik.fr";
	}

	@Override
	public String[] getAuthors() {
		return new String[] {
			"Hugues Cassé"
		};
	}

	@Override
	public Icon getLogo() {
		return this.getIcon("logo");
	}

}

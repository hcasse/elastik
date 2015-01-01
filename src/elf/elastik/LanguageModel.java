package elf.elastik;

import java.io.IOException;

import elf.ui.meta.LateLoader;

/**
 * Model for language.
 * @author casse
 */
public class LanguageModel extends LateLoader<Language, String> {
	Main app;
	
	public LanguageModel(Context context, String id) {
		super(context, id);
		app = context.app;
	}
	
	public LanguageModel(Context context, Language lang) {
		super(context, lang.getName());
		app = context.app;
		set(lang);
	}

	@Override
	public Language load(String id) throws IOException {
		Language lang = new Language(app.config.nat, id, false, app.t("all"));
		lang.load();
		return lang;
	}

	public static class Context extends LateLoader.Context<Language> {
		Main app;
		
		public Context(Main app) {
			super(new Language("", "", true, app.t("all")));
			this.app = app;
		}
		
	}
}

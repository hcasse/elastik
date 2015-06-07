package elf.elastik;

import java.io.IOException;
import java.util.Locale;

import elf.elastik.data.Language;
import elf.ui.meta.LateLoader;

/**
 * Model for language.
 * @author casse
 */
public class LanguageModel extends LateLoader<Language, String> {
	private Main app;
	private String for_name, nat_name;
	
	public LanguageModel(Context context, String id) {
		super(context, id);
		app = context.app;
	}
	
	public String getForeignName() {
		if(for_name == null)
			for_name = Locale.forLanguageTag(get().getName()).getDisplayName();
		return for_name;
	}
	
	public String getNativeName() {
		if(nat_name == null)
			nat_name = Locale.forLanguageTag(get().getNative()).getDisplayName();
		return nat_name;
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

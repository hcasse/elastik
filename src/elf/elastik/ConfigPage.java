package elf.elastik;

import java.util.Locale;

import elf.ui.ActionBar;
import elf.ui.Component;
import elf.ui.Container;
import elf.ui.Displayer;
import elf.ui.Form;
import elf.ui.PagePane.Page;
import elf.ui.SubsetField;
import elf.ui.meta.Action;
import elf.ui.meta.CollectionVar;
import elf.ui.meta.SetterVar;
import elf.ui.meta.Var;

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
public class ConfigPage implements ApplicationPage, Var.Listener<LanguageModel> {
	private Main app;
	private Window window;
	private Var<LanguageModel> current_language;
	private CollectionVar<Theme> themes = new CollectionVar<Theme>();
	private CollectionVar<Theme> subset;
	private Page page;
	
	private final Action learn = new Action() {
		@Override public void run() { }
		@Override public String getLabel() { return app.t("Learn"); }
		@Override public String getHelp() { return app.t("Start a learning session."); }
		@Override public boolean isEnabled() {
			boolean enabled = false;
			for(Theme theme: subset.getCollection())
				if(!theme.getWords().isEmpty()) {
					enabled = true;
					break;
				}
			return enabled;
		}
	};
	
	private final SetterVar<Boolean> repeat = new SetterVar<Boolean>(null, "Repeat") {
		@Override public String getLabel() { return app.t("Repeat once failed words."); }
	};
	
	public ConfigPage(Window window, Var<LanguageModel> current_language) {
		this.window = window;
		app = window.getApplication();
		this.current_language = current_language;
		repeat.setObject(app.config);
	}
	
	@Override
	public String getTitle() {
		return String.format(app.t("Learning %s"), Locale.forLanguageTag(current_language.get().get().getName()).getDisplayLanguage());
	}

	@Override
	public Page getPage() {
		if(page == null) {
			change(current_language);
			page = window.makePage();
			Container body = page.addBox(Component.VERTICAL);
			Container hbody = body.addBox(Component.HORIZONTAL);
			Form form = hbody.addForm(Form.STYLE_TWO_COLUMN, learn);
			form.addCheckBox(repeat);
			form.setButtonVisible(false);
			SubsetField<Theme> sset = hbody.addSubsetField(themes);
			sset.setDisplayer(new Displayer<Theme>() {
				@Override public String asString(Theme theme) {
					return String.format(app.t("%s (%d words)"), theme.getNative(), theme.getWords().size());
				}
			});
			subset = sset.getSubset();
			learn.add(subset);
			ActionBar bar = body.addActionBar();
			bar.add(learn);
			bar.setAlignment(Component.RIGHT);
			current_language.addListener(this);
		}
		return page;
	}

	@Override
	public void change(Var<LanguageModel> data) {
		themes.setCollection(current_language.get().get().getThemes());
	}

}

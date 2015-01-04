package elf.elastik;

import java.util.Locale;

import elf.ui.ActionBar;
import elf.ui.Component;
import elf.ui.Container;
import elf.ui.Displayer;
import elf.ui.Form;
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
public class ConfigPage extends ApplicationPage implements Var.Listener<LanguageModel> {
	private Var<LanguageModel> current_language;
	private CollectionVar<Theme> themes = new CollectionVar<Theme>();
	private CollectionVar<Theme> subset;
	
	private final Action learn = new Action() {
		@Override public void run() { window.doTrain(); }
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
		super(window);
		this.current_language = current_language;
		repeat.setObject(app.config);
	}
	
	@Override
	public String getTitle() {
		return String.format(app.t("Learning %s"), Locale.forLanguageTag(current_language.get().get().getName()).getDisplayLanguage());
	}

	@Override
	public void make() {
		change(current_language);
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

	@Override
	public void change(Var<LanguageModel> data) {
		themes.setCollection(current_language.get().get().getThemes());
	}

}

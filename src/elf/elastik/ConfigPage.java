package elf.elastik;

import java.util.Locale;

import elf.ui.AbstractDisplayer;
import elf.ui.ActionBar;
import elf.ui.Box;
import elf.ui.Component;
import elf.ui.Container;
import elf.ui.Form;
import elf.ui.SubsetField;
import elf.ui.meta.Accessor;
import elf.ui.meta.Action;
import elf.ui.meta.CollectionVar;
import elf.ui.meta.EnumVar;
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
public class ConfigPage extends ApplicationPage implements Var.ChangeListener<LanguageModel> {
	private Var<LanguageModel> current_language;
	private CollectionVar<Theme> themes = new CollectionVar<Theme>();
	private CollectionVar<Theme> subset = new CollectionVar<Theme>();
	private final Var<Test> test;
	
	private final Action learn = new Action() {
		@Override public void run() { doTrain(); }
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
	
	private final Var<Boolean> repeat;
	private final EnumVar<Test.Type> type;
	
	/**
	 * Get the selected themes.
	 * @return	Selected themes.
	 */
	public CollectionVar<Theme> getSelectedThemes() {
		return subset;
	}
	
	public ConfigPage(Window window, Var<LanguageModel> current_language, Var<Test> test) {
		super(window);
		this.current_language = current_language;
		this.test = test;
		type = new EnumVar<Test.Type>(new Accessor.Config<Test.Type>(app.config, "type"), app.i18n) {
			@Override public String getLabel() { return app.t("Exercise"); }
			@Override public String getHelp() { return app.t("Find foreign word from native word and the reverse."); }
		};
		repeat = new Var.Config<Boolean>(app.config, "repeat") {
			@Override public String getLabel() { return app.t("Repeat"); }
			@Override public String getHelp() { return app.t("Repeat once failed words."); }
		};
	}
	
	@Override
	public String getTitle() {
		return String.format(app.t("Learning %s"), Locale.forLanguageTag(current_language.get().get().getName()).getDisplayLanguage());
	}

	@Override
	public void make() {
		change(current_language);
		Container body = page.addBox(Component.VERTICAL);
		Box hbody = body.addBox(Component.HORIZONTAL);
		hbody.setAlign(Component.TOP);
		Form form = hbody.addForm();
		form.addAction(learn);
		form.setStyle(Form.STYLE_TWO_COLUMN);
		form.addEnumField(type);
		form.addCheckBox(repeat);
		form.setButtonVisible(false);
		SubsetField<Theme> sset = hbody.addSubsetField(themes);
		sset.setDisplayer(new AbstractDisplayer<Theme>() {
			@Override public String asString(Theme theme) {
				return String.format(app.t("%s (%d words)"), theme.getName(), theme.getWords().size());
			}
		});
		sset.setSubset(subset);
		learn.add(subset);
		ActionBar bar = body.addActionBar();
		bar.add(learn);
		bar.setAlignment(Component.RIGHT);
		current_language.addChangeListener(this);
	}

	@Override
	public void change(Var<LanguageModel> data) {
		themes.setCollection(current_language.get().get().getThemes());
	}

	/**
	 * Do a a training sessions.
	 */
	private void doTrain() {
		TestManager ntest = type.get().getTest(current_language.get().get(), subset.getCollection());
		if(app.config.repeat)
			ntest = new RepeatTest(ntest);
		test.set(ntest);
		window.doTrain();
	}
}

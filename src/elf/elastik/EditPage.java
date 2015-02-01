package elf.elastik;

import java.util.Locale;
import java.util.Vector;

import elf.ui.Icon;
import elf.ui.ActionBar;
import elf.ui.Button;
import elf.ui.Component;
import elf.ui.Container;
import elf.ui.Displayer;
import elf.ui.List;
import elf.ui.SplitPane;
import elf.ui.meta.Action;
import elf.ui.meta.CollectionVar;
import elf.ui.meta.Var;
import elf.ui.meta.Var.Listener;

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
public class EditPage extends ApplicationPage implements Listener<LanguageModel> {
	private boolean updated = false;
	private final Var<LanguageModel> current_language;
	private final Var<Theme> current_theme = new Var<Theme>();
	private final Var<Word> current_word = new Var<Word>();
	private final CollectionVar<Theme> themes = new CollectionVar<Theme>(new Vector<Theme>()); 
	private final CollectionVar<Word> words = new CollectionVar<Word>(new Vector<Word>()); 
	
	/**
	 * Build the page.
	 * @param window	Owner window.
	 */
	public EditPage(Window window, Var<LanguageModel> current_language) {
		super(window);
		this.current_language = current_language;
		current_language.addListener(this);		
	}
	
	/**
	 * Get the current theme.
	 * @return	Current theme.
	 */
	public Var<Theme> getCurrentTheme() {
		return current_theme;
	}
	
	@Override
	public void change(Var<LanguageModel> data) {
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
	public CollectionVar<Word> getWords() {
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
		theme_list.setDisplayer(new Displayer<Theme>() {
			@Override public String asString(Theme value) { return value.getNative(); }				
		});
		ActionBar ta = tc.addActionBar();
		ta.setStyle(Button.STYLE_ICON);
		Action add_theme = new Action() {
			@Override public String getLabel() { return "Add"; }
			@Override public void run() { }
			@Override public Icon getIcon() { return app.getIcon("list_add"); }
		};
		ta.add(add_theme);
		Action remove_theme =  new Action() {
			@Override public String getLabel() { return "Remove"; }
			@Override public void run() { themes.remove(current_theme.get()); }
			@Override public Icon getIcon() { return app.getIcon("list_remove"); }
			@Override public boolean isEnabled() {
				return current_theme.get() != null
					&& !EditPage.this.current_language.get().get().isAll(current_theme.get());
			}
		}; 
		remove_theme.add(current_theme);
		ta.add(remove_theme);		
		
		// make word list
		Container wc = spane.getSecond();
		List<Word> word_list = wc.addList(words);
		word_list.setSelector(current_word);
		word_list.setDisplayer(new Displayer<Word>() {
			@Override public String asString(Word value) { return value.getForeign() + " / " + value.getNative(); }		
		});
		current_theme.addListener(new Listener<Theme>() {
			@Override public void change(Var<Theme> data) {
				if(data.get() == null)
					words.setCollection(new Vector<Word>());
				else
					words.setCollection(current_theme.get().getWords());
			}
		});
		ActionBar wa = wc.addActionBar();
		wa.setStyle(Button.STYLE_ICON);
		Action add_word = new Action() {
			@Override public String getLabel() { return "Add"; }
			@Override public void run() { EditPage.this.window.doAdd();  }
			@Override public Icon getIcon() { return app.getIcon("list_add"); }
			@Override public boolean isEnabled() { return current_theme.get() != null; }
		};
		add_word.add(current_theme);
		wa.add(add_word);
		Action remove_word =  new Action() {
			@Override public String getLabel() { return "Remove"; }
			@Override public void run() { words.remove(current_word.get()); EditPage.this.current_language.get().get().modify();  }
			@Override public Icon getIcon() { return app.getIcon("list_remove"); }
			@Override public boolean isEnabled() { return current_word.get() != null; }
		};
		remove_word.add(word_list.getSelector());
		wa.add(remove_word);		
	}
}

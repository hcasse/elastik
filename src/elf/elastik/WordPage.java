package elf.elastik;

import elf.ui.Component;
import elf.ui.Form;
import elf.ui.Icon;
import elf.ui.meta.Action;
import elf.ui.meta.CollectionVar;
import elf.ui.meta.SingleVar;

/**
 * Page to edit a word.
 * +------------------+
 * | native   [     ] |
 * | foreign  [     ] |
 * |          [+][++] |
 * +------------------+
 * @author casse
 */
public class WordPage extends ApplicationPage {
	private SingleVar<LanguageModel> lang;
	private SingleVar<Theme> theme;
	private CollectionVar<Word> words;

	private SingleVar<String> nat_word = new SingleVar<String>("") {
		@Override public String getLabel() { return app.t("Native Word"); }
	};

	private SingleVar<String> for_word = new SingleVar<String>("") {
		@Override public String getLabel() { return app.t("Foreign Word"); }
	};

	private Action add_one = new Action() {
		@Override public void run() { addOne(); }
		@Override public boolean isEnabled() { return check(); }
		@Override public String getHelp() { return app.t("Add one word and leave."); }
		@Override public Icon getIcon() { return app.getIcon("add_one"); }
		@Override public String getLabel() { return app.t("Add"); }
	};

	private Action add_multi = new Action() {
		@Override public void run() { addMulti(); }
		@Override public boolean isEnabled() { return check(); }
		@Override public String getHelp() { return app.t("Add one word and continue."); }
		@Override public Icon getIcon() { return app.getIcon("add_multi"); }
		@Override public String getLabel() { return app.t("Next"); }
	};

	public WordPage(Window window, SingleVar<LanguageModel> lang, SingleVar<Theme> theme, CollectionVar<Word> words) {
		super(window);
		this.lang = lang;
		this.theme = theme;
		this.words = words;
	}
	
	/**
	 * Check if the words are ready.
	 * @return	True if check is successful, false else.
	 */
	private boolean check() {
		return !"".equals(nat_word.get()) && !"".equals(for_word.get());
	}
	
	/**
	 * Start a new word edition.
	 */
	public void newWord() {
		nat_word.set("");
		for_word.set("");
	}
	
	/**
	 * Add a new word.
	 *
	 */
	public void addWord() {
		Word word = new Word(nat_word.get(), for_word.get());
		lang.get().get().modify();
		if(theme.get() != lang.get().get().getAll())
			lang.get().get().add(word);
		words.add(word);
	}
	
	/**
	 * Add only one word.
	 */
	public void addOne() {
		addWord();
		newWord();
		window.doBack();
	}

	/**
	 * Add several words.
	 */
	public void addMulti() {
		addWord();
		newWord();
	}

	@Override
	public String getTitle() {
		return app.t("Adding words");
	}

	@Override
	public void make() {
		Form form = page.addForm(Form.STYLE_VERTICAL, add_multi);
		form.addAction(add_one);
		form.setButtonAlignment(Component.RIGHT);
		form.addTextField(for_word);
		form.addTextField(nat_word);
		add_one.add(for_word);
		add_one.add(nat_word);
		add_multi.add(for_word);
		add_multi.add(nat_word);
	}
}

package elf.elastik;

import elf.elastik.test.Test;
import elf.os.OS;
import elf.ui.Component;
import elf.ui.Container;
import elf.ui.Monitor;
import elf.ui.PagePane;
import elf.ui.PagePane.Page;
import elf.ui.TitleBar;
import elf.ui.View;
import elf.ui.meta.Action;
import elf.ui.meta.Var;

/**
 * Main window.
 * @author casse
 */
class Window {
	private Main app;
	private final Var<LanguageModel> current_language = new Var<LanguageModel>();
	private final Monitor mon;
	private TitleBar tbar;
	private final View view;
	private final PagePane page_pane;
	private final MainPage main_page;
	private final EditPage edit_page;
	private final WordPage word_page;
	private final ConfigPage config_page;
	private final TrainPage train_page;
	private final CompletionPage comp_page;
	private final Var<Test> current_test = new Var<Test>();
	
	public Window(Main app) {
		this.app = app;

		// build the main box
		view = OS.os.getUI().makeView(app);
		mon = view.getMonitor();
		Container mbox = view.addBox(Component.VERTICAL);
		tbar = mbox.addTitleBar();
		page_pane = mbox.addPagePane();
		tbar.addMenu(page_pane.getBackAction());
		tbar.addMenu(app.getAboutAction(view));
		tbar.addMenu(Action.QUIT);
		tbar.addLeft(page_pane.getBackAction());
		
		// build the pages
		main_page = new MainPage(this, current_language);
		edit_page = new EditPage(this, current_language);
		word_page = new WordPage(this, current_language, edit_page.getCurrentTheme(), edit_page.getWords());
		config_page = new ConfigPage(this, current_language, current_test);
		train_page = new TrainPage(this, current_language, current_test);
		comp_page = new CompletionPage(this, current_test);

		// open all
		tbar.setTitle(main_page.getTitle());
		page_pane.set(main_page.getPage());
		view.show();
	}
	
	/**
	 * Get the current view.
	 * @return	View
	 */
	public View getView() {
		return view;
	}
	
	/**
	 * Make a new page.
	 * @return	Built page.
	 */
	public Page makePage() {
		return page_pane.addPage();
	}
	
	/**
	 * Get the window monitor.
	 * @return	Window monitor.
	 */
	public Monitor getMonitor() {
		return mon;
	}
	
	/**
	 * Get the owner application.
	 * @return	Application owner.
	 */
	public Main getApplication() {
		return app;
	}
	
	/**
	 * Set the title in the title bar.
	 * @param title		New title.
	 */
	public void setTitle(String title) {
		tbar.setTitle(title);
	}
	
	/**
	 * Back to the previous page.
	 */
	public void doBack() {
		page_pane.back();
	}
	
	/**
	 * Edit the page.
	 */
	public void doEdit() {
		add(edit_page);
	}
	
	/**
	 * Perform addition of a word.
	 */
	public void doAdd() {
		add(word_page);
	}
	
	/**
	 * Perform learning.
	 */
	public void doLearn() {
		add(config_page);
	}
	
	/**
	 * Launch training session.
	 */
	public void doTrain() {
		add(train_page);
	}
	
	/**
	 * Display completion page.
	 */
	public void doCompletion() {
		set(comp_page);
	}
	
	/**
	 * Make the given page active.
	 * @param page	Page to make active.
	 */
	private void add(ApplicationPage page) {
		page_pane.push(page.getPage());
	}
	
	/**
	 * Set the current page (replacing the previous one).
	 * @param page		Replacing page.
	 */
	private void set(ApplicationPage page) {
		page_pane.set(page.getPage());
	}
	
}

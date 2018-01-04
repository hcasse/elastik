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
import elf.util.Duration;

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
	private final ConfigPage config_page;
	private final CompletionPage comp_page;

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
		config_page = new ConfigPage(this, current_language /*, current_test*/);
		comp_page = new CompletionPage(this);

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
	 * Perform learning.
	 */
	public void doLearn() {
		add(config_page);
	}
	
	/**
	 * Display completion page.
	 */
	public void doCompletion(Test test, Duration duration) {
		comp_page.set(test, duration);
		set(comp_page);
	}
	
	/**
	 * Make the given page active.
	 * @param page	Page to make active.
	 */
	public void add(ApplicationPage page) {
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

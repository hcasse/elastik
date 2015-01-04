package elf.elastik;

import elf.ui.PagePane;
import elf.ui.PagePane.Page;

/**
 * Abstraction of page for the application.
 * @author casse
 */
public abstract class ApplicationPage implements PagePane.Listener {
	protected final Main app;
	protected final Window window;
	protected Page page;
	
	public ApplicationPage(Window window) {
		this.window = window;
		this.app = window.getApplication();
	}
	
	/**
	 * Called to buid the UI.
	 */
	protected abstract void make();
	
	/**
	 * Get the title of the page.
	 * @return		Page title.
	 */
	public abstract String getTitle();
	
	/**
	 * Get the implementation page.
	 * @return	Implementation.
	 */
	public final Page getPage() {
		if(page == null) {
			page = window.makePage();
			page.setListener(this);
			make();
		}
		return page;
	}

	@Override
	public void onShow() {
		window.setTitle(getTitle());
	}

	@Override
	public void onHide() {
	}
	
}

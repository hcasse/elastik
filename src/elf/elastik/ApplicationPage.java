package elf.elastik;

import elf.ui.PagePane.Page;

/**
 * Abstraction of page for the application.
 * @author casse
 */
public interface ApplicationPage {

	/**
	 * Get the title of the page.
	 * @return		Page title.
	 */
	String getTitle();
	
	/**
	 * Get the implementation page.
	 * @return	Implementation.
	 */
	Page getPage();
	
}

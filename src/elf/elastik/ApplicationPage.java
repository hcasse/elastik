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
	 * Called to build the UI.
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

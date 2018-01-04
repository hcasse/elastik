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

import java.io.IOException;

import elf.elastik.test.Test;
import elf.os.OS;
import elf.ui.ActionBar;
import elf.ui.Box;
import elf.ui.Component;
import elf.ui.Sound;
import elf.ui.TextArea;
import elf.ui.meta.Action;
import elf.util.Duration;

/**
 * Page displaying completion result after a training.
 * @author casse
 */
public class CompletionPage extends ApplicationPage {
	private Test test;
	private Duration duration;
	private TextArea area;
	private Action redo = new Action() {
		@Override public void run() { TrainPage.run(window, test); }
		@Override public String getLabel() { return app.t("Redo"); }
		@Override public String getHelp() { return app.t("Do the test once more."); }
	};
	private Action stop = new Action() {
		@Override public void run() { test.reset(); window.doBack(); }
		@Override public String getLabel() { return app.t("Stop"); }
		@Override public String getHelp() { return app.t("Stop the test and go back to test configuration page."); }
	};
	
	private Sound perfect_sound;

	public CompletionPage(Window window) {
		super(window);
		try {
			perfect_sound = OS.os.getUI().getSound(Main.class.getResource("/sox/perfect.aiff"));
		} catch (IOException e) {
			// TODO log it somewhere
		}
	}
	
	/**
	 * Set the information for complation.
	 * @param test		Test to complete.
	 */
	public void set(Test test, Duration duration) {
		this.test = test;
		this.duration = duration;
	}

	@Override
	protected void make() {
		Box box = getPage().addBox(Component.VERTICAL);
		area = box.addTextArea();
		ActionBar cbar = box.addActionBar();
		cbar.setAlignment(Component.RIGHT);
		cbar.add(redo);
		cbar.add(stop);
	}

	@Override
	public String getTitle() {
		return app.t("Training Completed");
	}

	@Override
	public void onShow() {
		super.onShow();
		
		// compute stats
		int words = test.getQuestionNumber();
		int good = test.getSucceededNumber().get();
		int retry = test.getTryCount();
		int good_percent = good * 100 / test.getTryCount();
		int retry_percent = retry * 100 / words;

		// play perfect sound if required
		if(good_percent == 100 && perfect_sound != null)
			perfect_sound.play();

		// display title
		area.clear();
		area.display(app.t("<big>Test completed!</big>"));

		// display stars
		StringBuffer buf = new StringBuffer();
		buf.append("<p>");
		String full_star = "<img src=\"" + getClass().getResource("/pix/full_star.png") + "\"/>";
		String empty_star = "<img src=\"" + getClass().getResource("/pix/empty_star.png") + "\"/>";
		int full = good_percent / 20, i;
		for(i = 0; i < full; i++)
			buf.append(full_star);
		for(; i < 5; i++)
			buf.append(empty_star);
		buf.append("</p>");
		area.display(buf.toString());

		// display stats
		buf = new StringBuffer();
		buf.append("<br/><table><tr><td align=\"right\"><b>");
		buf.append(app.t("good answer"));
		buf.append("</b></td><td>");
		buf.append(String.format("%d (%3d%%)", good, good_percent));
		buf.append("</td></tr><tr><td align=\"right\"><b>");
		buf.append(String.format(app.t("retry")));
		buf.append("</b></td><td>");
		buf.append(String.format("%d (%3d%%)", retry, retry_percent));
		buf.append("</td></tr><tr><td align=\"right\"><b>");
		buf.append(app.t("words"));
		buf.append("</b></td><td>");
		buf.append(String.format(app.t("%d"), words));
		buf.append("</td></tr><tr><td align=\"right\"><b>");
		buf.append(app.t("time"));
		buf.append("</b></td><td>");
		buf.append((float)duration.getSeconds() / 1000 + "s");
		buf.append("</td></tr></table>");
		area.display(buf.toString());
	}

	@Override
	public void onHide() {
		super.onHide();
		if(perfect_sound != null)
			perfect_sound.stop();
	}

}

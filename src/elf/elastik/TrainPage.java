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
import java.util.HashMap;

import elf.elastik.data.Field;
import elf.elastik.data.Model;
import elf.elastik.test.Test;
import elf.os.OS;
import elf.text.Formatter;
import elf.ui.Box;
import elf.ui.Component;
import elf.ui.Form;
import elf.ui.ProgressBar;
import elf.ui.Sound;
import elf.ui.StatusBar;
import elf.ui.Style;
import elf.ui.TextField;
import elf.ui.TextInfo;
import elf.ui.UI;
import elf.ui.UI.Color;
import elf.ui.UI.Task;
import elf.ui.meta.Action;
import elf.ui.meta.Var;
import elf.util.Duration;


/**
 * Page for training.
 *
 * +-----------------------------+
 * | <language 1>                |
 * | [ read-only ]  [theme]      |
 * | <language 2>                |
 * | [           ]               |
 * |                             |
 * | [===        success gauge  ]|
 * | [======     train gauge    ]|
 * | {status}		       HH:MM |
 * +-----------------------------+
 *
 * @author casse
 */
public class TrainPage extends ApplicationPage {
	private int result_delay = 2000;
	private Var<Integer> progress = Var.make(0);
	private Var<Integer> word_count = Var.make(0);
	private Var<Integer> success = Var.make(0);
	private TextInfo timer;
	private TimerTask timer_task = new TimerTask();
	private WaitTask wait_task = new WaitTask();
	private StatusBar sbar;
	private boolean done = false;
	private Formatter format = new Formatter() {

		@Override
		public Object get(String id) {
			if(id.length() >= 1 && id.charAt(0) == '$')
				return test.getLanguage().getI18n().t(id);
			else
				return super.get(id);
		}

	};
	
	private Test test;
	private Text[] texts;
	private String[] values;
	
	private Action submit = new Action() {
		@Override public void run() { if(!done) { done = true; checkWord(); } }
	};
	private TextInfo info;
	
	private Style
		quest_style		= new Style(null, Style.FONT_SIZE, new Style.FontSize(Style.XX_LARGE)),
		answer_style	= new Style(null, Style.FONT_SIZE, new Style.FontSize(Style.XX_LARGE));

	private Color normal_color, failed_color, succeeded_color;
	private Sound success_sound, error_sound;
	
	private static HashMap<Model, TrainPage> map = new HashMap<Model, TrainPage>(); 
	
	public TrainPage(Window window, Model model) {
		super(window);

		// data initialization
		values = new String[model.count()];
		texts = new Text[model.count()];
		for(Field field: model)
			texts[field.getIndex()] = new Text(field);

		// UI initialization
		normal_color = OS.os.getUI().getColor("#0000ff");
		failed_color = OS.os.getUI().getColor("#ff0000");
		succeeded_color = OS.os.getUI().getColor("#00ff00");
		try {
			success_sound = OS.os.getUI().getSound(Main.class.getResource("/sox/success.aiff"));
			error_sound = OS.os.getUI().getSound(Main.class.getResource("/sox/error.aiff"));
		} catch (IOException e) {
			// TODO log it somewhere
		}
	}
	
	/**
	 * Run the page with the given test.
	 * @param window	Current window.
	 * @param test		Current test.
	 */
	public static void run(Window window, Test test) {
		TrainPage page = map.get(test.getModel());
		if(page == null) {
			page = new TrainPage(window, test.getModel());
			map.put(test.getModel(), page);
		}
		page.configure(test);
		window.add(page);
	}
	
	private void configure(Test test) {
		this.test = test;

		// prepare the formatter
		format.put("native", test.getLanguage().getNativeName());
		format.put("foreign", test.getLanguage().getForeignName());
		
		// reset the score
		success.set(0);
		progress.set(0);
		test.configure(success, progress);		
	}

	@Override
	public String getTitle() {
		return String.format(app.t("Training %s"), test.getLanguage().getForeignName());
	}

	@Override
	public void make() {
		page.setListener(this);
		Box body = page.addBox(Component.VERTICAL);
		
		// build the form
		Form form = body.addForm();
		form.addAction(submit);
		if(test.getModel().getType() == Model.VERTICAL)
			form.setStyle(Form.STYLE_VERTICAL);
		else
			form.setStyle(Form.STYLE_TWO_COLUMN);
		for(Field field: test.getModel())
			texts[field.getIndex()].make(form);
		form.setButtonVisible(false);
		
		// add progress and info bars
		info = body.addTextInfo("");
		body.addFiller();
		ProgressBar success_bar = body.addProgressBar(success, Var.make(0), word_count, Component.HORIZONTAL);
		success_bar.setText(app.t("%02d %% success"));
		ProgressBar progress_bar = body.addProgressBar(progress, Var.make(0), word_count, Component.HORIZONTAL);
		progress_bar.setText(app.t("%02d %% completed"));
		sbar = body.addStatusBar();
		timer = sbar.addTextInfo(" 00:00", Component.RIGHT);
	}

	private class TimerTask extends UI.Task {
		private int m = 0, s = 0;

		public TimerTask() {
			super(1000, true);
		}
		
		public Duration getDuration() {
			return new Duration(m * 60 + s);
		}

		public void start() {
			timer.setText("00:00");
			m = 0;
			s = 0;
			OS.os.getUI().start(this);
		}

		public void stop() {
			OS.os.getUI().stop(this);
		}

		@Override
		public void run() {
			s++;
			if(s == 60) {
				s = 0;
				m++;
			}
			timer.setText(String.format("%02d:%02d", m, s));
		}

	}

	/**
	 * Select the next word to display.
	 */
	private void nextWord() {
		answer_style.setColor(normal_color);

		// process the end
		if(!test.next(values)) {
			sbar.setDelay(StatusBar.FOREVER);
			sbar.set(app.t("Training completed!"));
			info.setText("");
			OS.os.getUI().stop(timer_task);
			window.doCompletion(test, timer_task.getDuration());
		}

		// select next word
		else {
			Text first = null;
			for(Field field: test.getModel()) {
				texts[field.getIndex()].setQuestion(values);
				if(first == null && values[field.getIndex()] == null)
					first = texts[field.getIndex()];
			}
			first.text.gainFocus();
			info.setText("");
			done = false;
		}
	}

	/**
	 * Check if the typed word is the good one.
	 */
	private void checkWord() {
		
		// record the answers
		for(Field field: test.getModel())
			texts[field.getIndex()].getAnswer(values);
		
		// succeeded!
		if(test.check(values)) {
			sbar.set(app.t("Well done!"));
			answer_style.setColor(succeeded_color);
			if(success_sound != null)
				success_sound.play();
			nextWord();			
		}
		
		// failed
		else {
			StringBuffer buf = new StringBuffer();
			buf.append("The answer should have been ");
			boolean first = true;
			for(Field field: test.getModel())
				if(values[field.getIndex()] != null ) {
					if(first)
						first = false;
					else
						buf.append(" / ");
					buf.append(values[field.getIndex()]);
				}
			info.setText(buf.toString());
			answer_style.setColor(failed_color);
			if(error_sound != null)
				error_sound.play();
			OS.os.getUI().start(wait_task);
		}

	}

	@Override
	public void onShow() {
		super.onShow();
		
		// reset UI
		word_count.set(test.getQuestionNumber());
		progress.set(0);
		success.set(0);
		test.reset();
		getPage();
		
		// start the train
		timer_task.start();
		sbar.set(app.t("Training started: complete the translations!"));
		nextWord();
	}

	@Override
	public void onHide() {
		super.onHide();
		timer_task.stop();
	}

	/**
	 * Task for wait after an answer.
	 * @author casse
	 */
	private class WaitTask extends Task {

		public WaitTask() {
			super(result_delay, false);
		}

		@Override
		public void run() {
			nextWord();
			sbar.clear();
		}

	}

	/**
	 * Texts to be entered by the user.
	 * @author casse
	 */
	private class Text extends Var<String> {
		private Field field;
		private TextField<String> text;
		
		public Text(Field field) {
			super("");
			this.field = field;
		}
		
		public void make(Form form) {
			text = form.addTextField(this);
		}
		
		public void setQuestion(String[] question) {
			int i = field.getIndex();
			if(question[i] == null) {
				set("");
				text.setReadOnly(false);
				text.setStyle(quest_style);
			}
			else {
				set(question[i]);
				text.setReadOnly(true);
				text.setStyle(answer_style);
			}
		}
		
		public void getAnswer(String[] question) {
			int i = field.getIndex();
			if(question[i] == null)
				question[i] = get().trim().toLowerCase();
		}

		@Override
		public String getLabel() {
			return format.format(app.t(field.getName()));
		}
		
	}
	
}

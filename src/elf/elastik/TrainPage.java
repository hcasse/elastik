package elf.elastik;

import java.io.IOException;

import elf.elastik.test.Question;
import elf.elastik.test.Test;
import elf.os.OS;
import elf.ui.Box;
import elf.ui.Component;
import elf.ui.Form;
import elf.ui.Icon;
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
	private Var<LanguageModel> current_language;
	private Var<Integer> progress = Var.make(0);
	private Var<Integer> word_count = Var.make(0);
	private Var<Integer> success = Var.make(0);
	private Var<Question> question = Var.make(Question.NULL);
	private TextInfo timer;
	private TimerTask timer_task = new TimerTask();
	private WaitTask wait_task = new WaitTask();
	private StatusBar sbar;
	private boolean done = false;
	private final Var<Test> test;
	private Var<String> text1 = new Var<String>("") {
		@Override public String getLabel() { return question.get().getLabel(); }
		@Override public Icon getIcon() { return Main.getLanguageIcon(question.get().getQuestionLanguage()); }
	};
	private Var<String> text2 = new Var<String>("") {
		@Override public String getLabel() { return app.t("Answer:"); }
		@Override public Icon getIcon() { return Main.getLanguageIcon(question.get().getAnswerLanguage()); }
	};
	private Action submit = new Action() {
		@Override public void run() { if(!done) { done = true; checkWord(); } }
	};
	private TextInfo info;
	
	private Style
		quest_style		= new Style(null, Style.FONT_SIZE, new Style.FontSize(Style.XX_LARGE)),
		answer_style	= new Style(null, Style.FONT_SIZE, new Style.FontSize(Style.XX_LARGE));

	private Color normal_color, failed_color, succeeded_color;
	private Sound success_sound, error_sound;
	
	public TrainPage(Window window, Var<LanguageModel> current_language, Var<Test> test) {
		super(window);
		this.current_language = current_language;
		this.test = test;
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

	@Override
	public String getTitle() {
		return String.format(app.t("Training %s"), current_language.get().getForeignName());
	}

	@Override
	public void make() {
		page.setListener(this);
		Box body = page.addBox(Component.VERTICAL);
		Form form = body.addForm();
		form.addAction(submit);
		form.setStyle(Form.STYLE_VERTICAL);
		TextField<String> field = form.addTextField(text1);
		field.setStyle(quest_style);
		field.setReadOnly(true);
		test.listenForEntity(text1);
		field = form.addTextField(text2);
		field.setStyle(answer_style);
		answer_style.setColor(normal_color);
		test.listenForEntity(text2);
		form.setButtonVisible(false);
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
		question.set(test.get().next());
		answer_style.setColor(normal_color);

		// process the end
		if(question.get() == null) {
			question.set(Question.NULL);
			text1.set("");
			sbar.setDelay(StatusBar.FOREVER);
			sbar.set(app.t("Training completed!"));
			info.setText("");
			OS.os.getUI().stop(timer_task);
			window.doCompletion();
		}

		// select next word
		else {
			text1.set(question.get().getQuestion());
			text2.set("");
			info.setText("");
			done = false;		}
	}

	/**
	 * Check if the typed word is the good one.
	 */
	private void checkWord() {
		String answer = test.get().check(text2.get().toLowerCase());
		progress.set(test.get().getDoneNumber());
		if(answer == null) {
			sbar.set(app.t("Well done!"));
			success.set(test.get().getSucceededNumber());
			answer_style.setColor(succeeded_color);
			if(success_sound != null)
				success_sound.play();
			nextWord();
		}
		else {
			info.setText(answer);
			answer_style.setColor(failed_color);
			if(error_sound != null)
				error_sound.play();
			OS.os.getUI().start(wait_task);
		}
	}

	@Override
	public void onShow() {
		super.onShow();
		word_count.set(test.get().getQuestionNumber());
		progress.set(0);
		success.set(0);
		getPage();
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

}

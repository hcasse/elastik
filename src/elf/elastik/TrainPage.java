package elf.elastik;

import elf.os.OS;
import elf.ui.Box;
import elf.ui.Component;
import elf.ui.Form;
import elf.ui.ProgressBar;
import elf.ui.StatusBar;
import elf.ui.TextField;
import elf.ui.TextInfo;
import elf.ui.UI;
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
	private Var<LanguageModel> current_language;
	private Var<Integer> progress = Var.make(0);
	private Var<Integer> word_count = Var.make(0);
	private Var<Integer> success = Var.make(0);
	private TextInfo timer;
	private TimerTask timer_task = new TimerTask();
	private WaitTask wait_task = new WaitTask();
	private StatusBar sbar;
	private boolean done = false;
	private final Var<Test> test;
	private Var<String> text1 = new Var<String>("") {
		@Override public String getLabel() { return Main.getLanguageDisplay(test.get().getQuestionLang()); }
	};
	private Var<String> text2 = new Var<String>("") {
		@Override public String getLabel() { return Main.getLanguageDisplay(test.get().getAskedLang()); }
	};
	private Action submit = new Action() {
		@Override public void run() { if(!done) { done = true; checkWord(); } }
	};
	private TextInfo info;
	
	public TrainPage(Window window, Var<LanguageModel> current_language, Var<Test> test) {
		super(window);
		this.current_language = current_language;
		this.test = test;
	}
	
	@Override
	public String getTitle() {
		return String.format(app.t("Training %s"), current_language.get().getForeignName());
	}

	@Override
	public void make() {
		page.setListener(this);
		Box body = page.addBox(Component.VERTICAL);
		Form form = body.addForm(Form.STYLE_VERTICAL, submit);
		TextField<String> field = form.addTextField(text1);
		field.setReadOnly(true);
		test.listenForEntity(text1);
		field = form.addTextField(text2);
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
		
		// process the end
		String next = test.get().nextQuestion();
		if(next == null) {
			text1.set("");
			sbar.setDelay(StatusBar.FOREVER);
			sbar.set(app.t("Training completed!"));
			info.setText("");
			OS.os.getUI().stop(timer_task);
			test.get().setDuration(new Duration(timer_task.m * 60 + timer_task.s));
			window.doCompletion();
		}
		
		// select next word
		else {
			text1.set(next);
			text2.set("");
			info.setText("");
			done = false;
		}
	}
	
	/**
	 * Check if the typed word is the good one.
	 */
	private void checkWord() {
		String answer = test.get().checkAnswer(text2.get().toLowerCase());
		progress.set(test.get().getWordCount() - test.get().getRemainCount());
		if(answer == null) {
			sbar.set(app.t("Well done!"));
			success.set(test.get().getSuccessCount());
			nextWord();
		}
		else {
			sbar.set(app.t("Sorry! Bad answer."));
			info.setText(String.format(app.t("Answer should be \"%s\"."), answer));
			OS.os.getUI().start(wait_task);
		}
	}
	
	@Override
	public void onShow() {
		super.onShow();
		word_count.set(test.get().getWordCount());
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

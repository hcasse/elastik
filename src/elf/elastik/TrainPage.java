package elf.elastik;

import elf.os.OS;
import elf.ui.Box;
import elf.ui.Component;
import elf.ui.Form;
import elf.ui.StatusBar;
import elf.ui.TextField;
import elf.ui.TextInfo;
import elf.ui.UI;
import elf.ui.meta.Action;
import elf.ui.meta.SingleVar;
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
	private Var<LanguageModel> current_language;
	private TextInfo timer;
	private TimerTask timer_task = new TimerTask();
	private StatusBar sbar;
	private Var<String> text1 = new SingleVar<String>("") {
		@Override public String getLabel() { return current_language.get().getForeignName(); }
	};
	private Var<String> text2 = new SingleVar<String>("") {
		@Override public String getLabel() { return current_language.get().getNativeName(); }
	};
	private Action submit = new Action() {
		@Override public void run() { }
	};
	
	public TrainPage(Window window, Var<LanguageModel> current_language) {
		super(window);
		this.current_language = current_language;
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
		form.addTextField(text2);
		form.setButtonVisible(false);
		body.addFiller();
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

	@Override
	public void onShow() {
		super.onShow();
		getPage();
		timer_task.start();
		sbar.set(app.t("Training started: complete the translations!"));
	}

	@Override
	public void onHide() {
		super.onHide();
		timer_task.stop();
	}
	
}

package elf.elastik;

import java.util.Locale;
import java.util.Vector;

import elf.app.AutoConfiguration;

/**
 * Configuration of the application.
 * @author casse
 */
public class Configuration extends AutoConfiguration {
	public String fname = "", lname = "", nat;
	public Vector<String> langs = new Vector<String>();
	public boolean repeat = false;
	public Test.Type type = Test.Type.FOREIGN_TO_NATIVE;
	
	public Configuration(Main main) {
		super(main, "config");
		nat = Locale.getDefault().getLanguage();
	}
	
	public void addLanguage(String lang) {
		langs.add(lang);
		modify();
	}

	
	public void removeLanguage(String lang) {
		langs.remove(lang);
		modify();
	}
	
	public boolean getRepeat() {
		return repeat;
	}
	
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
		modify();
	}

	public Test.Type getType() {
		return type;
	}

	public void setType(Test.Type type) {
		this.type = type;
		modify();
	}

}

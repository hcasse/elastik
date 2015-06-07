package elf.elastik;

import java.util.Collection;
import java.util.Locale;
import java.util.Vector;

import elf.app.AutoConfiguration;
import elf.elastik.data.Theme;
import elf.elastik.test.AbstractVocabulary;
import elf.elastik.test.ForeignToNative;
import elf.elastik.test.NativeToForeign;

/**
 * Configuration of the application.
 * @author casse
 */
public class Configuration extends AutoConfiguration {

	enum TestType {
		FOREIGN_TO_NATIVE,
		NATIVE_TO_FOREIGN;

		public AbstractVocabulary getTest(Collection<Theme> themes, String forn, String natv) {
			switch(this) {
			case FOREIGN_TO_NATIVE:	return new ForeignToNative(themes, forn, natv);
			case NATIVE_TO_FOREIGN:	return new NativeToForeign(themes, forn, natv);
			default:				return null;
			}
		}
	};

	public String fname = "", lname = "", nat;
	public Vector<String> langs = new Vector<String>();
	public boolean repeat = false;
	public TestType type = TestType.FOREIGN_TO_NATIVE;

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

	public TestType getType() {
		return type;
	}

	public void setType(TestType type) {
		this.type = type;
		modify();
	}

}

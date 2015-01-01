package elf.elastik;

/**
 * Represents a simple user.
 * @author casse
 */
public class User {
	String fname, lname, lang;
	
	public User(String fname, String lname, String lang) {
		this.fname = fname;
		this.lname = lname;
		this.lang = lang;
	}
	
	/**
	 * Get the first name.
	 * @return	First name.
	 */
	public String getFirstName() {
		return fname;
	}
	
	/**
	 * Get the last name.
	 * @return	Last name.
	 */
	public String getLastName() {
		return lname;
	}
	
	/**
	 * Get the native language of the user.
	 * @return	Native language.
	 */
	public String getLanguage() {
		return lang;
	}
}

package wordcloud;

import java.util.HashSet;

/**
 * Quick class to find invalid strings.
 */
public class StringCleaner {
	public static HashSet<String> invalidStrings;
    public final static String DELIMS = " \t\n";
	
	/**
	 * Initializes all the invalid strings to look for.
	 */
	public static void initInvalidStrings(){
		invalidStrings = new HashSet<String>();
		invalidStrings.add("to");
		invalidStrings.add("I");
		invalidStrings.add("my");
		invalidStrings.add("me");
		invalidStrings.add("the");
		invalidStrings.add("if");
		invalidStrings.add("to");
		invalidStrings.add("of");
		invalidStrings.add("a");
		invalidStrings.add("and");
		invalidStrings.add("in");
		invalidStrings.add("it");
		invalidStrings.add("for");
		invalidStrings.add("he");
		invalidStrings.add("his");
		invalidStrings.add("she");
		invalidStrings.add("her");
		invalidStrings.add("by");
		invalidStrings.add("or");
		invalidStrings.add("on");
		invalidStrings.add("as");
		invalidStrings.add("about");
		invalidStrings.add("who");
		invalidStrings.add("so");
		invalidStrings.add("you");
		invalidStrings.add("its");
		invalidStrings.add("they");
		invalidStrings.add("them");
	}
	
	/**
	 * Checks to see if the string is valid.
	 * 
	 * @param str - string to look
	 * @return - true if contains. false otherwise.
	 */
	public static boolean checkValid(String str) {
		return !invalidStrings.contains(str);
	}
	
	/**
	 * Look to see if String contains web links, in which case remove them.
	 * WIP.
	 * 
	 * @param str
	 */
	public static void removeWeb(String str){
		/** TODO */
	}
}

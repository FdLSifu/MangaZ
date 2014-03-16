package com.fdl.mangaz.utils;

import java.util.ArrayList;
import java.util.Locale;

//https://github.com/Skylion007/java-manga-reader/blob/master/src/org/skylion/mangareader/util/StringUtil.java
public final class StringUtil {

	private StringUtil(){};//Prevents instantiation

	/**
	 * Checks if it contains a number
	 * @param input The String you want to check
	 * @return True if it does, false otherwise
	 */
	public static boolean containsNum(String input){
		for(char c: input.toCharArray()){
			if(Character.isDigit(c)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the number is parsable into a int or double (contains only num)
	 * @param input
	 * @return
	 */
	public static boolean isNum(String input){
		for(char c: input.toCharArray()){
			if(!Character.isDigit(c) || c!='.'){//Special case for decimals
				return false;
			}
		}
		return true;
	}

	public static boolean isValidCharacter(char input){
		char[] validChar ="abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-._~:/?#[]@!$&'()*+,;=;"
				.toCharArray();
		for(char c: validChar){
			if(c == input){
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes trailing white spaces from a String
	 * @param input The String you want to modify
	 * @return
	 */
	public static String removeTrailingWhiteSpaces(String input){
		while(input.length()>=1 && input.charAt(input.length()-1)==' '){//Removes trailing whitespaces
			input = input.substring(0,input.length()-1);
		}
		return input;
	}

	public static ArrayList<String> formatChapterNames(ArrayList<String> out){
		for(int i = 0; i<out.size(); i++){
			System.out.println(out.get(i));
			out.set(i, formatChapterNames(out.get(i)));
		}
		return out;
	}


	public static String formatChapterNames(String input){
		input = removeTrailingWhiteSpaces(input);
		input = input.substring(0,input.lastIndexOf(' ')+1);
		return input;
	}

	public static String titleCase(String realName) {
	    String space = " ";
	    String[] names = realName.split(space);
	    StringBuilder b = new StringBuilder();
	    for (String name : names) {
	        if (name == null || name.isEmpty()) {
	            b.append(space);
	            continue;
	        }
	        b.append(name.substring(0, 1).toUpperCase(Locale.getDefault()))
	                .append(name.substring(1).toLowerCase(Locale.getDefault()))
	                .append(space);
	    }
	    return b.toString();
	}
	
	public static int getChapterNumber(String chapter_name) {
		String[] c = chapter_name.split(" ");
		for(int i = 0; i < c.length ; i ++)
			if(c[i].equals(":"))
				return Integer.parseInt(c[i-1]);
		
		return -1;
	}
	
	  public static String sanitizeFilename(String name) {
		    return name.replaceAll("[:\\\\/*?|<>]", "_");
	  }
}
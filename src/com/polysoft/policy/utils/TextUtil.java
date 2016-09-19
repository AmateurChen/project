package com.polysoft.policy.utils;

import java.io.File;

public class TextUtil {

	public static void main(String[] args) {
		String test = "/sdfsd/sd";
		System.out.println(replaceEndStr(test, "sd", ""));
	}
	
	public static boolean isEmpty(String str) {
		if(null == str || "".equals(str)) {
			return true;
		}
		return false;
	}
	
	
	public static boolean isEndSeparator(String str) {
		return str.matches(".*[/\\\\]");
	}
	
	public static boolean isMatches(String str, String regex) {
		return str.matches(regex);
	}
	
	public static String subStrEndDirectory(String str) {
		File file = new File(str);
		return file.getName();
	}
	
	public static String replaceEndStr(String str, String endStr, String repStr) {
		return str.replaceAll(endStr + "+?$", repStr);
	}
	
	public static void appendSeparator(String str) {
		str += File.separator;
	}
}

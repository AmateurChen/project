package com.polysoft.policy.utils;

public class TextUtil {

	public static boolean isEmpty(String str) {
		if(null == str || "".equals(str)) {
			return true;
		}
		return false;
	}
	
}

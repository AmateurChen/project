package com.polysoft.policy.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {

	public static final String PATTERN_1 = "yyyy-MM-dd hh:mm:ss";
	private static final SimpleDateFormat sdf = new SimpleDateFormat();
	
	public static String transForm(long time, String pattern) {
		Date date = new Date(time);
		sdf.applyPattern(pattern);
		return sdf.format(date);
	}
	
}

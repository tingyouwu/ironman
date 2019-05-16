package com.kw.app.commonlib.utils;

import java.util.regex.Pattern;

public class NumberUtil {
	/*
	 * 是否为浮点数？double或float类型。
	 * @param str 传入的字符串。
	 * @return 是浮点数返回true,否则返回false。
	 */
	public static boolean isDoubleOrFloat(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
		return pattern.matcher(str).matches();
	}

}

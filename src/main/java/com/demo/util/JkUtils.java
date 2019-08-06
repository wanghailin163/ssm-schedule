package com.demo.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JkUtils {
	
	public static String transt_0 = "0";//未记账
	public static String transt_1 = "1";//已正常记账
	public static String transt_2 = "2";//重复记账
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMM");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM");
	public static String getTime(Date d) {
		return sdf.format(d);
	}
	public static String getDate(Date d) {
		return sdf1.format(d);
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	public static boolean isEmptyList(List<?> list) {
		return null == list || list.size() == 0;
	}
	
	public static String getExceptionTrace(Exception e) {
		StringWriter sw = new StringWriter();  
        e.printStackTrace(new PrintWriter(sw, true));  
        return  sw.toString();  
	}
	
	public static String formatAccountingDate(String date) {
		try {
			return sdf2.format(sdf1.parse(date));
		} catch (ParseException e) {
			return date;
		}
	}
	
	public static String formatPeriodName(String date) {
		try {
			return sdf4.format(sdf3.parse(date));
		} catch (ParseException e) {
			return date;
		}
	}
}

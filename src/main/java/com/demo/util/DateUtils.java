package com.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具类
 */
public class DateUtils {
	
	public static int null2Int(Object obj){
		return obj == null?0:Integer.parseInt(obj.toString());
	}
	
	public static String null2Str(Object obj){
		return obj == null?"":obj.toString();
	}
	
	public static String formatDate(Date date,String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	
	public static Date str2Date(String dateString,String format) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(dateString);
	}
	
	public static String int2Date(int date,String format) throws ParseException{
		Date tempDate = str2Date(String.valueOf(date), format);
		return formatDate(tempDate, format);
	}
    public static String Date2Str(Date date ,String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    
    public static Date parse(String date,String pattern) throws ParseException{
    	SimpleDateFormat sdf = new SimpleDateFormat(pattern) ;
    	return sdf.parse(date) ;
    }
    
    public static Double null2Double(Object obj){
		return obj == null ? 0.00 : Double.valueOf(String.valueOf(obj));
	}
	
}

package com.demo.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * 字符串工具类
 */
public class StringUtil {
	/**
	 * 判断字符串是否空
	 * @param str 目标字符串
	 * @return 如果为 空、空串或空格返回true，否则返回false
	 */
	public static boolean isEmpty(String str) {
		if (null == str || str.trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断字符串是否非空
	 * @param str 目标字符串
	 * @return 如果为 空、空串或空格返回false，否则返回true
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	/**
	 * 获得格式化的字符串
	 * @param str 要处理的字符串
	 * @return 如果字符串为空则返回""，否则返回去除首位空格的字符串
	 */
	public static String getFormatString(String str) {
		if (null != str && !"".equals(str.trim())) {
			return str.trim();
		} else {
			return "";
		}
	}
	
	/**
	 * 获得格式化的字符串
	 * @param str 要处理的字符串
	 * @param defaultValue 如果str为空返回的默认值
	 * @return 如果字符串为空则返回""，否则返回去除首位空格的defaultValue字符串
	 */
	public static String getFormatString(String str, String defaultValue) {
		if (null != str && !"".equals(str.trim())) {
			return str.trim();
		} else {
			return getFormatString(defaultValue);
		}
	}
	
	/**
	 * 格式化后，判断两个穿是否相等
	 * @param arg0 
	 * @param arg1
	 * @return true:&nbsp;相等<br>false:&nbsp;不相等
	 */
	public static boolean isEqual(String arg0, String arg1) {
		return getFormatString(arg0).equals(getFormatString(arg1));
	}
	
	/**
	 * 判断两个数值是否相同
	 * @param arg0
	 * @param arg1
	 * @return Boolean
	 */
	public static boolean isNumEqual(String arg0, String arg1) {
		arg0 = arg0.replaceAll(",", "");
		arg1 = arg1.replaceAll(",", "");
		try {
			BigDecimal decimal1 = new BigDecimal((arg0));
			BigDecimal decimal2 = new BigDecimal((arg1));
			return (decimal1.compareTo(decimal2) == 0);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 判断两个数值是否相等，如果两个都为空或空串，则认定为相等
	 * @param arg0
	 * @param arg1
	 * @return Boolean
	 */
	public static boolean isNumCompare(String arg0, String arg1) {
		if (StringUtil.isEmpty(arg0) && StringUtil.isEmpty(arg1)) {
			return true;
		} else {
			arg0 = arg0.trim();
			arg1 = arg1.trim();
			arg0 = StringUtil.getFormatString(arg0);
			arg1 = StringUtil.getFormatString(arg1);
			return isNumEqual(arg0, arg1);
		}
	}
	
	/**
	 * 格式化金额
	 * @param numStr
	 * @return String
	 */
	public static String formatNumber(String numStr) {
		DecimalFormat nf = new DecimalFormat("###,###.####################");
		try {
			return nf.format(new BigDecimal(numStr));
		} catch (Exception e) {
			return "";
		}
	}
	
	public static String getDivRsNumber(String numStr, String div) {
		try {
			BigDecimal b1 = new BigDecimal(numStr);
			BigDecimal b2 = new BigDecimal(div);
			BigDecimal rs = b1.divide(b2);
			return formatNumber(rs.toString());
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 将数字字符串变为百分比数值
	 * @param valueStr
	 * @return String
	 */
	public static String getPercentValue(String valueStr) {
		try {
			valueStr = getFormatString(valueStr);
			valueStr = valueStr.replace(",", "");
			valueStr = valueStr.replace("%", "");
			BigDecimal rate = new BigDecimal(valueStr);
			rate = rate.multiply(new BigDecimal(100));
			return rate.toString();
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 判断一个字符串是否数字
	 * @param numberStr
	 * @return Boolean
	 */
	public static boolean isNumber(String numberStr) {
		boolean isNum = false;
		try {
			Integer.parseInt(getFormatString(numberStr));
			isNum = true;
		} catch (Exception e) {
			isNum = false;
		}
		return isNum;
	}
	
	public static String removeLastChar(String str) {
		if (isEmpty(str)) {
			return str;
		} else {
			return str.substring(0, str.length() -1);
		}
	}
	
	public static String null2Str(Object obj){
		return obj == null ? "" : obj.toString() ;
	}
	
	public static String acccodeList2Str(List<String> list){
		StringBuffer sb = new StringBuffer();
		for(String str : list){
			sb.append("'" + str + "'").append(",");
		}
		String finalStr = sb.toString().substring(0, sb.toString().length()-1);
		return finalStr;
	}
	
	/**
	 * 全角转半角
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {        
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
          	   c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
          	   c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);
        return returnString;
    }
	
	
	public static boolean compareNumber(BigDecimal amnt1,BigDecimal amnt2){
		//1000,1005
		//返回true ,是amnt2-amnt1  取amnt2 对应的名称 
		//返回false ,是amnt1-amnt2 取amnt1 对应的名称 
		if(amnt1.compareTo(BigDecimal.ZERO)==1){
			//如果大于0
			if(amnt1.compareTo(amnt2)==-1){
				return  true;
			}else if(amnt1.compareTo(amnt2)==1){
				return false;
			}else if(amnt1.compareTo(amnt2)==0){
				return true;
			}
		}else if(amnt1.compareTo(BigDecimal.ZERO)==-1){
			//如果小于0
			//-1000,-1005
			if(amnt1.compareTo(amnt2)==-1){
                return  false; 				
			}else if(amnt1.compareTo(amnt2)==1){
				return true;
			}else if(amnt1.compareTo(amnt2)==0){
                return true;				
			}
		}
		return true;
	}
	
	
	
	//讲字符串补位加空格
	public static String rpad(String str,int length,String redix){
         int strLength =0;
			try {
				strLength = str.getBytes("gb2312").length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
         String rtnStr = "";
         if(strLength>length){
        	 rtnStr = str.substring(strLength-length, strLength);
         }else if(strLength==length){
             rtnStr=str;        	 
         }else if(strLength<length){
        	 //如果长度不够，补空格
        	 rtnStr = str;       
        	 for(int i=length-strLength;i>0;i--){
        		 rtnStr = rtnStr +redix;
        	 }
         }
		return rtnStr;
	}
	
	   //讲字符串补位右加0
		public static String lpad(String str,int length,String redix){
	         int strLength =0;
			try {
				strLength = str.getBytes("gb2312").length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
	         String rtnStr = "";
	         if(strLength>length){
	        	 rtnStr = str.substring(strLength-length, strLength);
	         }else if(strLength==length){
	             rtnStr=str;        	 
	         }else if(strLength<length){
	        	 //如果长度不够，补0
	             rtnStr = str;       	
	             for(int i=length-strLength;i>0;i--){
	        		 rtnStr = redix+rtnStr;
	        	 }
	         }
			return rtnStr;
		}
	
	
}

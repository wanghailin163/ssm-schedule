package com.demo.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.demo.model.ParamConfig;



/**
 * 常量定义类
 */
public class Constants {
	
	
	private static Map<String,ParamConfig> configMap = new HashMap<String, ParamConfig>() ;
	
	public static void setConfigMap(Map<String,ParamConfig> configMap){
		Constants.configMap = configMap ;
	}
	
	public static Map<String,ParamConfig> getConfigMap(){
		return Constants.configMap ;
	}
	
	public static String getAbsoutePath(String path) throws IOException{
		URL url = Constants.class.getResource("/") ;
		String tmp = null ;
		if(url == null){
			url = Constants.class.getResource("/com") ;
			tmp = url.getPath() ;
			tmp = tmp.substring(tmp.indexOf("/"),tmp.indexOf("lib/")+4) ;
			path = new File(tmp,path).getCanonicalPath() ;
		}else{
			tmp = url.getFile() ;
			if(!tmp.contains(":")){
				path = new File(tmp,path).getCanonicalPath() ;
			}else{
				path = url.getPath() ;
			}
		}
		
		if(!path.endsWith("/")){
			path = path + "/" ;
		}
		
		return path ;
	}

}

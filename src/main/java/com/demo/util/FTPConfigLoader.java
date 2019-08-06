package com.demo.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FTPConfigLoader {
	public static final String URL="url";
	private Properties properties = new Properties();
	private static FTPConfigLoader configLoader;
	private String path;
	private FTPConfigLoader() {
		path = this.getClass().getResource("/ftp.properties").getPath();
		loadConfig();
	}
	private void loadConfig() {
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(this.path);
			properties.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getValue(String key) {
		if (properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		return null;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public static  synchronized FTPConfigLoader getInstance() {
		if(configLoader == null) {
			configLoader = new FTPConfigLoader();
		}
		return configLoader;
	}
}

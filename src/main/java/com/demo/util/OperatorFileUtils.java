package com.demo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读/写文件操作类
 */
public class OperatorFileUtils {

	private static Logger logger = LoggerFactory.getLogger(OperatorFileUtils.class);

	public static boolean exportDataToFile(String path, String fileName, List<String> dataList) {
		BufferedWriter bw = null;
		try {
			if (JkUtils.isEmpty(path)) {
				throw new RuntimeException("生成文件时本地路径为空,无法生成文件!");
			}
			if (JkUtils.isEmpty(fileName)) {
				throw new RuntimeException("生成文件时文件名为空,无法生成文件!");
			}

			String fullName = null;
			if (path.trim().endsWith("/")) {
				fullName = path.trim() + fileName.trim();
			} else {
				fullName = path.trim() + "/" + fileName.trim();
			}

			File file = new File(fullName);

			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), JkConstants.UTF_8));

			if (!JkUtils.isEmptyList(dataList)) {
				int num = dataList.size();
				for (int i = 0; i < num; i++) {
					bw.write(dataList.get(i));
					if(i != num-1) {
						bw.write(JkConstants.UNIX_NEW_LINE);
					}
					bw.flush();
				}
				
			}
			return true;
		} catch (Exception e) {
			logger.error("生成" + fileName + "文件失败", e);
			throw new RuntimeException("生成" + fileName + "文件失败,详细信息如下 ：\n" + e.getMessage());
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error("关闭" + fileName + "失败", e);
				}
			}
		}
	}

	public static boolean exportOkFile(String path, String fileName, String size) {
		BufferedWriter bw = null;
		try {
			if (JkUtils.isEmpty(path)) {
				throw new RuntimeException("生成文件时本地路径为空,无法生成文件!");
			}
			if (JkUtils.isEmpty(fileName)) {
				throw new RuntimeException("生成文件时文件名为空,无法生成文件!");
			}

			String fullName = null;
			if (path.trim().endsWith("/")) {
				fullName = path.trim() + fileName.trim();
			} else {
				fullName = path.trim() + "/" + fileName.trim();
			}

			File file = new File(fullName);

			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), JkConstants.UTF_8));
			bw.write(size);
			bw.newLine();
			bw.flush();

			return true;
		} catch (Exception e) {
			logger.error("生成" + fileName + "失败", e);
			throw new RuntimeException("生成" + fileName + "失败,详细信息如下 ：\n" + e.getMessage());
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error("关闭" + fileName + "失败", e);
				}
			}
		}
	}

	public static boolean exportSuccessFile(String path, String fileName) {
		BufferedWriter bw = null;
		try {
			if (JkUtils.isEmpty(path)) {
				throw new RuntimeException("生成文件时本地路径为空,无法生成文件!");
			}
			if (JkUtils.isEmpty(fileName)) {
				throw new RuntimeException("生成文件时文件名为空,无法生成文件!");
			}

			String fullName = null;
			if (path.trim().endsWith("/")) {
				fullName = path.trim() + fileName.trim();
			} else {
				fullName = path.trim() + "/" + fileName.trim();
			}

			File file = new File(fullName);

			File parent = file.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}

			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), JkConstants.UTF_8));
			return true;
		} catch (Exception e) {
			logger.error("生成" + fileName + "失败", e);
			throw new RuntimeException("生成" + fileName + "失败,详细信息如下 ：\n" + e.getMessage());
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error("关闭" + fileName + "失败", e);
				}
			}
		}
	}
	
	/**
	 * 读取本地文件
	 * @param localDir 本地路径
	 * @param fileName 文件名
	 * @return List
	 */
	public static List<String> readFile(String localDir,String fileName){
		List<String> list = new ArrayList<String>();
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(new File(localDir + fileName));
			br = new BufferedReader(new InputStreamReader(fis,"GB2312"));
			String line = "";
			while((line = br.readLine()) != null){
				list.add(line.trim());
			}
			br.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return list;
	}
	
	public static File[] getAllFiles(String dirUrl, final String suffix) {
		File srcDir = new File(dirUrl);
		if (!srcDir.exists() || !srcDir.isDirectory()) {
			return null;
		}

		File[] files = srcDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.getName().endsWith(suffix) && !f.isDirectory()) {
					return true;
				}
				return false;
			}
		});

		return files;
	}
	
	

	/**
	 * 写文件
	 * @param s 写入的字符串
	 */
	public static String[] writeString(String s, String filePath){
		try{
			
			//根据当前日期获取文件夹名称
			File packageFile = new File(filePath);
			//判断文件夹是否存在,如果不存在则创建文件夹
			if (!packageFile.exists()) {
				packageFile.mkdir();
			}
			String date = DateUtils.formatDate(new Date(), "yyyyMMdd");
			//根据当前日期获取文件夹名称
			String packageNames = DateUtils.formatDate(new Date(), "yyyyMMdd");
			
			File packageFiles = new File(filePath + packageNames);
			//判断文件夹是否存在,如果不存在则创建文件夹
			if (!packageFiles.exists()) {
				packageFiles.mkdir();
			}
			
			String filename = date+"/"+ System.currentTimeMillis()+".dos";
			
			String [] str = new String[]{date,filename};
			//创建文件
			File file = new File(packageFile + "/" + filename);
			
			//将数据写入文件
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
			bw.write(s);
			bw.flush();
			bw.close();	
			
			return str;
		}catch(Exception e){
			e.printStackTrace();
		}
		return new String[]{};
	}
	
	
	/**
	 * 写文件
	 * @param s 写入的字符串
	 */
	public static String writeStrings(String s, String filePath){
		try{
			//根据当前日期获取文件夹名称
			File packageFile = new File(filePath);
			//判断文件夹是否存在,如果不存在则创建文件夹
			if (!packageFile.exists()) {
				packageFile.mkdir();
			}
			File packageFiles = new File(filePath);
			//判断文件夹是否存在,如果不存在则创建文件夹
			if (!packageFiles.exists()) {
				packageFiles.mkdir();
			}
			
			String filename = "autoftp.sh";
			
			//创建文件
			File file = new File(packageFile + "/" + filename);
			
			//将数据写入文件
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
			bw.write(s);
			bw.flush();
			bw.close();	
			
			return "";
		}catch(Exception e){
			e.printStackTrace();
		}
		return new String();
	}
	
	
}

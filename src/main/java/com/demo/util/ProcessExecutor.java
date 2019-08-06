package com.demo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * 执行命令行命令 
 */
public class ProcessExecutor {
	private static Logger logger = LoggerFactory.getLogger(ProcessExecutor.class);
	/***
	 *    tftclient   -d[up/down]  -h[iHostNo] -r[sSrvFileName] [sClientFileName] -t[TradeCode/MachinCode]
     *     tft64client -d[up/down]  -h[iHostNo] -r[sSrvFileName] [sClientFileName] -t[TradeCode/MachinCode]    
     *     32位和64位参数相同
     *     -d 上传下载方式，up为上传，down为下载
     *     -h tft主机标示，即配置文件中HOSTNO=0值
     *     -r tft服务主机文件路径，可使用绝对路径，相对路径从server端配置文件中的TFT_SERVER_PRINTDIR路径开始。
     *     sClientFileName 为本机文件路径，可使用绝对路径，相对路径从本机配置文件中的TFT_SERVER_PRINTDIR路径开始。
     */	
	//public static String tftpDownloadStr="tft64client -ddown  -h"+iHostNo+" -r"+sSrvFileName "+" "+sClientFileName+" -t"+TradeCode;
	
	/**
	 * 读取输入流
	 * @param inputStream
	 * @param out
	 */
	private static String read(InputStream inputStream) {
		StringBuffer sb = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("读取命令行环境inputstream內容异常："+e.getMessage());
			}
		}
		return sb.toString();
	}
    
	/***
	 * 执行 command命令
	 * @param command
	 * @return  messageBean code=0 为成功执行
	 */
	public static MessageBean process(String command) {
		MessageBean m = new MessageBean();
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(command);
			String content=read(process.getInputStream());
			if (logger.isDebugEnabled()){
				logger.debug("process inputstream 内容："+content);
			}
			String errorContent=read(process.getErrorStream());
			if (logger.isDebugEnabled()){
				logger.debug("process errorstream 内容："+errorContent);
			}
			int exitCode = process.waitFor();
			m.setCode(String.valueOf(exitCode));
			m.setMessage(errorContent);			
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("执行命令：["+command+"]过程中发生异常："+e.getMessage());
			m.setCode("-1");
			m.setMessage("执行命令：["+command+"]过程中发生异常："+e.getMessage());
			return m;
		}
		return m;
	}
}

package com.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketHelper {	

	private static Logger logger = LoggerFactory.getLogger(SocketHelper.class);

	public MessageBean send(String ip, int port, String content) {		
		Socket client =null;
		OutputStream os = null;
		InputStream is = null;
		String   responseStr="";
		MessageBean mBean = new MessageBean();
		try {
			client = new Socket(ip, port);
			/*设置超时5s*/
			client.setSoTimeout(50000);
			os = client.getOutputStream();
			byte[] contentBytes=content.getBytes();
			os.write(contentBytes);
			os.flush();
			if (logger.isDebugEnabled()){
				logger.debug("socket 发送内容："+content);
			}
			is = client.getInputStream();
			System.out.println("接收到的报文为：----------"+is);
			byte[] responseByte = readReceivePackage(is);
			responseStr = new String(responseByte, "GB2312");
			if (logger.isDebugEnabled()){
				logger.debug("socket 接收内容："+content);
			}	
			mBean.setCode("0");
			mBean.setMessage(responseStr);
			return mBean;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("socket 发送异常："+e.getMessage());
			mBean.setCode("-1");
			mBean.setMessage(e.getMessage());
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (is != null) {
					is.close();
				}
				if (client != null) {
					client.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
		}	
		return mBean;
	}

	/***
	 * 返回byte[] 前十位定长保存报文内容长度
	 * @param in
	 * @return
	 */
	private  static byte[] readReceivePackage(InputStream in) throws Exception {
		int headerLength= 8;	
		byte[] headerBuf = new byte[headerLength];
		int off = 0;
		// 获得包头报文长度
		while (off < headerLength) {
			off = off + in.read(headerBuf,off,headerLength-off);
			if (off < 0) {
				logger.error("获取报文长度出现异常");
				throw new Exception("获取报文长度出现异常");
			}		
		}
		logger.debug(String.valueOf(headerBuf));
		int messageLength=0;
		try {
			System.out.println("报文头:"+new String(headerBuf));
		    messageLength = Integer.parseInt(new String(headerBuf));	
		    System.out.println(messageLength);
		}  catch (Exception e){
			logger.error("转换报文长度出现异常");
			throw new Exception("转换报文长度出现异常");
		}		
		if (messageLength == 0) {
			logger.error("无效的报文报文长度");
			throw new Exception("无效的报文长度");
		}
		off = 0;
		byte[] messageBuf = new byte[messageLength];
		while (off < messageLength) {					    
			off = off+in.read(messageBuf, off, messageLength-off);
			/*如果输入缓冲区里无内容了,中断读取*/
			if (in.available()==0){
				break;
			}
		}		
		if (logger.isDebugEnabled()){
			logger.debug("返回报文内容："+ new String(messageBuf));
		}
		return messageBuf;
	}
	
	
	public static void main(String[] args){
		SocketHelper s = new SocketHelper();
		s.send("127.0.0.1",9999, "10abcdefghi");
	}
	
	
	
	
}

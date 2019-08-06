package com.demo.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketReqUtils {
	private static OutputStream os = null;
	private static BufferedInputStream bis = null;
	private static Socket cs = null;
	public static String serviceInterface(String ip, int port, String xml) {
		try {
			int length = xml.getBytes().length;
			byte[] buf = new byte[length + 8];
			String lenValue = String.valueOf(length + 8);
			System.arraycopy(xml.getBytes(), 0, buf, 8, length);
			for (int i = 0; i < 8; i++)
				buf[i] = '0';
			int idx = 0;
			for (int i = 8 - lenValue.length(); i < 8; i++) {
				buf[i] = (byte) lenValue.charAt(idx++);
			}
			System.out.println("====================>发送的报文====" + new String(buf));
			
			Socket cs = new Socket(ip, port);// 连接服务器
			os = cs.getOutputStream();
			os.write(buf);
			System.out.println("reqXml send ok.....");
			
			InputStream in = cs.getInputStream();
			byte[] byteString = readPackage(in);
			String finalString = new String(byteString,"GB2312");
			System.out.println("====================>接收报文 ====" + finalString);
			return finalString;
		} catch (Exception e) {
			try {
				if (os != null) {
					os.close();
				}
				if (bis != null) {
					bis.close();
				}
				if (cs != null) {
					cs.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return "";
	}
	
	
	public static byte[] readPackage(InputStream in) throws IOException, Exception {
		int lengthHeadLen = 8;
		int headInfoLen = 25;
		byte[] lenHeadBuf = new byte[lengthHeadLen];
		int off = 0;
		// 获得包头报文长度
		while (off < lengthHeadLen){
			off = off + in.read(lenHeadBuf, off, lengthHeadLen - off);
			if (off < 0)
			{
				throw new Exception("Socket was closed! while reading!");
			}
		}
		
		int contentLength = 0;		
		contentLength = Integer.parseInt(new String(lenHeadBuf)) - lengthHeadLen - headInfoLen;	

		if (contentLength == 0)
			throw new Exception("Invalid TCPIP req package protocol!");
		off = 0;
		byte[] tempBuf = new byte[headInfoLen];
		while (off < headInfoLen)
		{

			int len = in.read(tempBuf, off, headInfoLen - off);
			if (len <= 0)
			{
				break;
			}
			off = off + len;
		}			
		// 获得包体内容开始
		off = 0;
		byte[] contentBuf = new byte[contentLength];
		while (off < contentLength)
		{

			int len = in.read(contentBuf, off, contentLength - off);
			if (len <= 0)
			{
				break;
			}
			off = off + len;
		}
		return contentBuf;
	}
}

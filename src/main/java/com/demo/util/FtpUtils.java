package com.demo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * FTP工具类
 */
public class FtpUtils {

	final static Logger logger = Logger.getLogger(FtpUtils.class);

	public static FTPClient ftpClient = new FTPClient();

	/**
	 * 上传文件至FTP服务器
	 * 
	 * @param path
	 *            本地文件路径
	 * @param fileName
	 *            本地文件名称
	 * @param ip
	 *            IP地址
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @param dir
	 *            上传文件路径
	 * @return Boolean
	 */
	public static synchronized Boolean ftpToServer(String path, String fileName, String ip, String port, String username, String password, String dir) {

		try {
			if (!connect(ip, Integer.parseInt(port), username, password)) {
				throw new RuntimeException("无法连接ftp服务器,ip:"+ip+",端口:"+port+",用户名:"+username+",密码:"+password+";登录状态:"+ftpClient.getReplyString());
			}
			if (isNull(path)) {
				logger.info("读取本地路径为空");
				throw new RuntimeException("读取本地路径为空");
			}
			if (isNull(dir)) {
				logger.info("FTP上传路径为空");
				throw new RuntimeException("FTP上传路径为空");
			}

			String localFileFullName = null;
			String remoteFileFullName = null;
			
			if(path.trim().endsWith("/")) {
				localFileFullName = path.trim() + fileName;
			}else {
				localFileFullName = path.trim() + "/" + fileName;
			}
			System.out.println(localFileFullName);
			
			if(dir.trim().endsWith("/")) {
				remoteFileFullName = dir.trim() + fileName;
			}else {
				remoteFileFullName = dir.trim() + "/" + fileName;
			}
			
			UploadStatus us = upload(localFileFullName, remoteFileFullName);

			logger.info("upload file '" + dir + fileName + "' is '" + us);

			disconnect();

			if ((us == UploadStatus.UPLOAD_NEW_FILE_SUCCESS || us == UploadStatus.UPLOAD_FROM_BREAK_SUCCESS)) {
				logger.info("上传文件：" + fileName + " 至ftp服务器成功");
				return true;
			} else {
				throw new RuntimeException("上传文件至ftp服务器失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("上传文件至ftp服务器失败", e);
			throw new RuntimeException("上传文件至ftp服务器失败,详细信息如下 ：\n" + e.getMessage());
		}
	}

	/**
	 * 连接到FTP服务器
	 * 
	 * @param hostname
	 *            主机名
	 * @param port
	 *            端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return 是否连接成功
	 * @throws Exception
	 * @throws IOException
	 */
	public static boolean connect(String hostname, Integer port, String username, String password) throws Exception {
		try {
			port = port == null ? 21 : port;
			ftpClient.connect(hostname, port);
			ftpClient.login(username, password);
			ftpClient.setConnectTimeout(8000);
			ftpClient.setControlEncoding("UTF-8");
			logger.info("登录FTP状态:"+ftpClient.getReplyCode());
			if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				return true;
			}
		} catch (Exception e) {
			throw new Exception("Connect to ftp server exception " + e.getMessage());
		}
		disconnect();
		return false;
	}

	/**
	 * 不支持断点续传上传方法
	 * 
	 * @param local
	 *            本地路径文件
	 * @param remote
	 *            远程路径
	 * @return UploadStatus 上传状态对象
	 * @throws IOException
	 */
	public static UploadStatus upload(String local, String remote) throws IOException {
		// 设置PassiveMode传输
		ftpClient.enterLocalPassiveMode();
		// 设置以二进制流的方式传输
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		ftpClient.setControlEncoding("UTF-8");
		String remoteFileName = remote;
		File f = new File(local);
		return uploadFile(remoteFileName, f, ftpClient, 0);
	}

	public Boolean deleteFile(String remoteFileName) throws Exception {
		Boolean _rs = false;
		try {
			_rs = ftpClient.deleteFile(remoteFileName);
		} catch (Exception e) {
			throw new Exception("Delete file exception ." + e.getMessage());
		}
		return _rs;
	}

	/**
	 * 断开与远程服务器的连接
	 */
	public static void disconnect() throws Exception {
		try {
			if (ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		} catch (Exception e) {
			throw new Exception("Disconnect the ftp server exception." + e.getMessage());
		}
	}

	/**
	 * 上传文件到服务器,新上传和断点续传
	 * 
	 * @param remoteFile
	 *            远程文件名，在上传之前已经将服务器工作目录做了改变
	 * @param localFile
	 *            本地文件File句柄，绝对路径
	 * @param processStep
	 *            需要显示的处理进度步进值
	 * @param ftpClient
	 *            FTPClient引用
	 * @return UploadStatus 上传状态
	 * @throws IOException
	 */
	public static UploadStatus uploadFile(String remoteFile, File localFile, FTPClient ftpClient, long remoteSize) throws IOException {
		UploadStatus status = null;
		RandomAccessFile raf = null;
		OutputStream out = null;
		// 显示进度的上传
		try {
			long step = localFile.length();
			long process = 0;
			long localreadbytes = 0L;
			ftpClient.deleteFile(remoteFile);
			raf = new RandomAccessFile(localFile, "r");
			out = ftpClient.appendFileStream(new String(remoteFile.getBytes("UTF-8"), "ISO_8859_1"));
			// 断点续传
			if (remoteSize > 0) {
				ftpClient.setRestartOffset(remoteSize);
				process = remoteSize / step;
				raf.seek(remoteSize);
				localreadbytes = remoteSize;
			}
			byte[] bytes = new byte[1024];
			int c;
			while ((c = raf.read(bytes)) != -1) {
				out.write(bytes, 0, c);
				localreadbytes += c;
				if (localreadbytes / step != process) {
					process = localreadbytes / step;
				}
			}
			out.flush();
			raf.close();
			out.close();
			boolean result = ftpClient.completePendingCommand();
			if (remoteSize > 0) {
				status = result ? UploadStatus.UPLOAD_FROM_BREAK_SUCCESS : UploadStatus.UPLOAD_FROM_BREAK_FAILED;
			} else {
				status = result ? UploadStatus.UPLOAD_NEW_FILE_SUCCESS : UploadStatus.UPLOAD_NEW_FILE_FAILED;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (Exception ex) {
					logger.error(ex);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception ex) {
					logger.error(ex);
				}
			}
		}
		return status;
	}
	
	
	/**
	 * 从FTP服务器上下载文件
	 * 
	 * @param remote 远程文件路径
	 * @param local 本地文件路径
	 * @return 上传的状态
	 * @throws Exception 
	 */
	public static DownloadStatus download(String remote, String local) throws Exception{
		DownloadStatus result ;
		FileOutputStream out = null ;
		InputStream in=null;
		try {
//			ftpClient.changeWorkingDirectory(Constants.paraConstantMap.get(Constants.ACCOUNT_REMOTE_DOWNLOAD_DIR).getConfig_Value());
//			FTPFile[] fs = ftpClient.listFiles(); 
//			if(fs.length > 0)
//				return DownloadStatus.REMOTE_FILE_NOEXIST;
			// 设置被动模式
			ftpClient.enterLocalPassiveMode();
			// 设置以二进制方式传输
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			// 检查远程文件是否存在
//			FTPFile[] files = ftpClient.listFiles(new String(Constants.paraConstantMap.get(Constants.ACCOUNT_REMOTE_DOWNLOAD_DIR).getConfig_Value().getBytes("UTF-8"), "iso-8859-1"));
			FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
			if (files.length != 1) {
				return DownloadStatus.REMOTE_FILE_NOEXIST;
			}

			File f = new File(local);
			out = new FileOutputStream(f);
//			ftpClient.setBufferSize(1024); 
            //设置文件类型（二进制） 
//            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); 
//			ftpClient.retrieveFile(remote, out);
//			ftpClient.retrieveFile(new String(remote.getBytes("UTF-8"), "iso-8859-1"), out);
//			in = ftpClient.retrieveFileStream(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
			in = ftpClient.retrieveFileStream(remote);
			byte[] bytes = new byte[1024];
			int c;
			while ((c = in.read(bytes)) != -1) {
				out.write(bytes, 0, c);
			}
			in.close();
			out.close();
			boolean upNewStatus = ftpClient.completePendingCommand();
			if (upNewStatus) {
				result = DownloadStatus.DOWNLOAD_NEW_SUCCESS;
			} else {
				result = DownloadStatus.DOWNLOAD_NEW_FAILED;
			}
			
		} catch (Exception e) {
			throw new Exception("Download file exceptions."+e.getMessage());
		} finally{
			IOUtils.closeQuietly(out); 
			if(out!=null){
				try{
					out.close();
				}catch(Exception ex){
					logger.error(ex);
				}
			}
			if(in!=null){
				try{
					in.close();
				}catch(Exception ex){
					logger.error(ex);
				}
			}
		}
		return result;
	}
	
	/** 
	 * Description: 从FTP服务器下载文件 
	 * @Version1.0 Jul 27, 2008 5:32:36 PM by 崔红保（cuihongbao@d-heaven.com）创建 
	 * @param url FTP服务器hostname 
	 * @param port FTP服务器端口 
	 * @param username FTP登录账号 
	 * @param password FTP登录密码 
	 * @param remotePath FTP服务器上的相对路径 
	 * @param fileName 要下载的文件名 
	 * @param localPath 下载后保存到本地的路径 
	 * @return 
	 */  
	public static boolean downFile(String url, int port,String username, String password, 
			String remotePath,String localPath) throws Exception{ 
		System.out.println("url:"+url+" port:"+port+" username:"+username+" password:"+password+" remotePath:"+remotePath+" localPath:"+localPath);
	    boolean success = false;  
	    FTPClient ftp = new FTPClient();
	    
	    
	    try {  
	        int reply;  
	        ftp.connect(url, port);  
	        //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器  
	        ftp.login(username, password);//登录  
	        reply = ftp.getReplyCode();  
	        if (!FTPReply.isPositiveCompletion(reply)) {  
	            ftp.disconnect();  
	            return success;  
	        }  
	        ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录  
	        
	        
	        FTPFile[] fs = ftp.listFiles();  
	        System.out.println("ftp文件夹中文件数:"+fs.length);
	        String date = DateUtils.formatDate(new Date(), "yyyyMMdd");
	        File packageFiles = new File(localPath +"/"+date);
			//判断文件夹是否存在,如果不存在则创建文件夹
			if (!packageFiles.exists()) {
				packageFiles.mkdir();
			}
			
	        for(FTPFile ff:fs){  
	            if(ff.getName().startsWith(ConfigLoader.getInstance().getValue("FILE_NAME"))&&ff.getSize()>0){  
	                File localFile = new File(localPath+"/"+date+"/"+ff.getName());  
	                OutputStream is = new FileOutputStream(localFile);   
	                ftp.retrieveFile(ff.getName(), is);  
	                is.close();  
	            }  
	        }  
	        ftp.logout();  
	        success = true;  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	        throw e;
	    } finally {  
	        if (ftp.isConnected()) {  
	            try {  
	                ftp.disconnect();  
	            } catch (IOException ioe) {  
	            }  
	        }  
	    }  
	    return success;  
	}
	
	public static boolean downFiles(String url, int port,String username, String password, 
			String remotePath,String localPath) throws Exception{ 
		
		
		System.out.println("url:"+url+" port:"+port+" username:"+username+" password:"+password+" remotePath:"+remotePath+" localPath:"+localPath);
	    boolean success = false;  
	    FTPClient ftp = new FTPClient();
	    
	    
	    try {  
	        int reply;  
	        ftp.connect(url, port);  
	        //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器  
	        ftp.login(username, password);//登录  
	        reply = ftp.getReplyCode();  
	        if (!FTPReply.isPositiveCompletion(reply)) {  
	            ftp.disconnect();  
	            return success;  
	        }  
	        ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录  
//	        String date = DateUtils.formatDate(new Date(), "yyyyMMdd");
	        
	        String date = ConfigLoader.getInstance().getValue("KERNEL_QUERY_DATE");
    	    String is_use_kernel_date = ConfigLoader.getInstance().getValue("IS_USE_KERNEL_DATE");
    	    if("Y".equals(is_use_kernel_date)){
    	    	date = ConfigLoader.getInstance().getValue("KERNEL_QUERY_DATE");
    	    }else{
    	    	date = DateUtils.formatDate(new Date(), "yyyyMMdd");
    	    }
	        
	        File packageFiles = new File(localPath +"/"+date);
			//判断文件夹是否存在,如果不存在则创建文件夹
			if (!packageFiles.exists()) {
				packageFiles.mkdir();
			}
	        
	        ftp.logout();  
	        success = true;  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	        throw e;
	    } finally {  
	        if (ftp.isConnected()) {  
	            try {  
	                ftp.disconnect();  
	            } catch (IOException ioe) {  
	            }  
	        }  
	    }  
	    return success;  
	}

	/**
	 * @param arags
	 * @throws Exception
	 */
	public static void main(String[] arags) throws Exception{
		
//		FtpUtils c = new FtpUtils();
//		c.connect("127.0.0.1", 21, "adolfmc", "adolfmc");
//		FTPClient ftpClient = c.ftpClient;
//		ftpClient.disconnect();
//		System.out.println(ftpClient.isConnected());
//		 if(!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
//			 ftpClient.disconnect();
//		        System.err.println("FTP server refused connection.");
//		 }
//		System.out.println(ftpClient.getReplyCode());
		
//		boolean isTrue = FtpUtils.downFile("192.168.8.57", 21, "user1","user1","/export/home/user1/testdir/", "123.txt", "d:/test/");
//    	System.out.println(isTrue);
		
//		FtpUtils.connect(hostname, port, username, password);
		
	}
	
	private static boolean isNull(String str) {
		return str == null || str.trim().length() == 0;
	}
}

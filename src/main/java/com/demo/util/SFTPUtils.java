package com.demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtils {
	private static ChannelSftp sftp = null;
	private static Session sshSession = null;

	public static void connect(String host, int port, String username, String password) {
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			sshSession = jsch.getSession(username, host, port);
			sshSession.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			sshSession.setConfig(sshConfig);
			sshSession.connect();
			Channel channel = sshSession.openChannel("sftp");
			channel.connect();
			sftp = (ChannelSftp) channel;
		} catch (Exception e) {
			throw new RuntimeException("建立SFTP连接失败！" + JkConstants.WINDOWS_NEW_LINE + JkUtils.getExceptionTrace(e));
		}
	}

	/**
	 * Disconnect with server
	 */
	public static void disconnect() {
		if (sftp != null && sftp.isConnected()) {
			sftp.disconnect();
		}
		if (sshSession != null && sshSession.isConnected()) {
			sshSession.disconnect();
		}
	}

	public static boolean upload(String localDir, String fileName, String remoteDir) {
		FileInputStream fis = null;
		try {
			String fileFullName = localDir.endsWith("/") ? localDir + fileName : localDir + "/" + fileName;
			sftp.cd(remoteDir);
			File file = new File(fileFullName);
			fis = new FileInputStream(file);
			sftp.put(fis, fileName, ChannelSftp.OVERWRITE);
			return true;
		} catch (Exception e) {
			throw new RuntimeException("上传文件失败,localDir=" + localDir + ",fileName=" + fileName + ",remoteDir=" + remoteDir + JkConstants.WINDOWS_NEW_LINE + JkUtils.getExceptionTrace(e));
		}finally {
			try {
				fis.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean upload(String host, int port, String username, String password, String localDir, String fileName, String remoteDir) {
		try {
			connect(host, port, username, password);
			boolean res = upload(localDir, fileName, remoteDir);
			disconnect();
			return res;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public File downloadFile(String directory, String remoteFileName,String localFile) {  
        //log.info(">>>>>>>>FtpUtil-->downloadFile--ftp下载文件"+remoteFileName+"开始>>>>>>>>>>>>>");  
        //connect();  
        File file = null;  
        OutputStream output = null;  
        try {  
            file = new File(localFile);  
            if (file.exists()){  
                file.delete();  
            }  
            file.createNewFile();  
            sftp.cd(directory);  
            output = new FileOutputStream(file);  
            sftp.get(remoteFileName, output);  
            //log.info("===DownloadFile:" + remoteFileName + " success from sftp.");  
        }  
        catch (SftpException e) {  
            if (e.toString().equals("No such file")) {  
                //log.info(">>>>>>>>FtpUtil-->downloadFile--ftp下载文件失败" + directory +remoteFileName+ "不存在>>>>>>>>>>>>>");  
                //throw new GoPayException("FtpUtil-->downloadFile--ftp下载文件失败" + directory +remoteFileName + "不存在");  
            }  
            //throw new GoPayException("ftp目录或者文件异常，检查ftp目录和文件" + e.toString());  
        }  
        catch (FileNotFoundException e) {  
            //throw new GoPayException("本地目录异常，请检查" + file.getPath() + e.getMessage());  
        }  
        catch (IOException e) {  
            //throw new GoPayException("创建本地文件失败" + file.getPath() + e.getMessage());  
        }  
        finally {  
            if (output != null) {  
                try {  
                    output.close();  
                }  
                catch (IOException e) {  
                    //throw new GoPayException("Close stream error."+ e.getMessage());  
                }  
            }  
            disconnect();  
        }  
  
        //log.info(">>>>>>>>FtpUtil-->downloadFile--ftp下载文件结束>>>>>>>>>>>>>");  
        return file;  
    }  

}

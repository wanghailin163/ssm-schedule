package com.demo.util ;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

/**
 * @author kevin
 *
 */
public class FTPUtil {
	
	final static Logger logger = Logger.getLogger(FTPUtil.class);
	
	private static FTPClient ftpClient = null ;
	
	private static InputStream is = null ;
	
	private static OutputStream os = null ;
	
	public FTPUtil(){
		if(ftpClient == null){
			ftpClient = new FTPClient() ;
		}
	}
	/**
	 * Connect to FTP Server
	 * @param ftpConfig
	 * @return if connected return true else return false 
	 * @throws Exception
	 */
	public boolean connect(String host,int port,String user,String pwd,String encoding) {
		try {
			port = port == 0 ? 21 : port ;
			ftpClient.connect(host, port);
			ftpClient.setControlEncoding(encoding==null || "".equals(encoding)? "utf-8" : encoding);
			if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				return ftpClient.login(user,pwd) ;
			}else{
				return false ;
			}
		} catch (Exception e) {
			logger.info("Failed to Connect the FTP Server , error info as follows : " + e.getMessage(),e) ;
			throw new RuntimeException("Failed to Connect the FTP Server , error info as follows : " + e.getMessage(),e.getCause());
		}
	}
	
	/**
	 * disconnect to ftp server 
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws Exception {
		try {
			if (ftpClient.isConnected()) {
				ftpClient.disconnect();
			}
		} catch (Exception e) {
			logger.info("Failed to shutdow ftp connection as exception occours , error message as follows : "+e.getMessage(),e); 
			throw new Exception("Failed to shutdow ftp connection as exception occours , error message as follows : "+e.getMessage(),e.getCause());
		}
	}
	
	/**
	 * Create Directory if it not exists on ftp server
	 * @param remote_path
	 * @return
	 * @throws Exception
	 */
	public UploadStatus createDirecroty(String remote_path) {
		UploadStatus status = UploadStatus.CREATE_DIRECTORY_SUCCESS;
		try{
			if(!remote_path.equals("/")){
				if(remote_path.startsWith("/")){
					ftpClient.changeWorkingDirectory("/") ;
					remote_path = remote_path.substring(1) ;
				}
				
				String[] path_arr = remote_path.split("/") ;
				for(String path : path_arr){
					if(!ftpClient.changeWorkingDirectory(path)){
						ftpClient.makeDirectory(path) ;
						ftpClient.changeWorkingDirectory(path) ;
					}
				}
			} 
		}catch (Exception e) {
				logger.info("Failed to create Directory , error message : " + e.getMessage(),e) ;
				throw new RuntimeException("Failed to create Directory , error message : " + e.getMessage(), e.getCause()) ;
			}
		return status;
	}
	
	public UploadStatus uploadFile(String local_file,String remote_file,String encoding) throws IOException{

		UploadStatus status = UploadStatus.UPLOAD_NEW_FILE_FAILED ;
		
		ftpClient.enterLocalPassiveMode();
		
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		
		ftpClient.setControlEncoding(encoding);
		
		ftpClient.deleteFile(remote_file) ;
	
		try {
			is = new FileInputStream(local_file) ; // read local file
			os = ftpClient.appendFileStream(remote_file) ; // write content to remote file 
			int i = is.available() ;
			byte[] b = new byte[i] ;
			is.read(b) ;
			String w = new String(b,encoding) ;
			os.write(w.getBytes()) ;
			os.flush() ;
			is.close() ;
			os.close();
			boolean result = ftpClient.completePendingCommand() ;
			status = result ? UploadStatus.UPLOAD_NEW_FILE_SUCCESS : UploadStatus.UPLOAD_NEW_FILE_FAILED ;
			
		} catch (Exception e) {
			logger.info("Upload File Failed , error message : " + e.getMessage(),e) ;
			throw new RuntimeException("Upload File Failed , error message : " + e.getMessage(),e) ;
		}finally{
			if(is != null){
				is.close() ;
			}
			
			if(os != null){
				os.close() ;
			}
		}
		
		return status ;
	}
	
	public DownloadStatus download(String remote, String local) throws Exception{
		DownloadStatus result ;
		FileOutputStream out = null ;
		InputStream in=null; ;
		try {
			ftpClient.enterLocalPassiveMode();
			
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			
			FTPFile[] files = ftpClient.listFiles(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
			if (files.length != 1) {
				return DownloadStatus.REMOTE_FILE_NOEXIST;
			}

			long lRemoteSize = files[0].getSize();
			File f = new File(local);
			//if (f.exists()) {
			if(f.exists()){
				long localSize = f.length();
				if (localSize >= lRemoteSize) {
					return DownloadStatus.LOCAL_BIGGER_REMOTE;
				}

				out = new FileOutputStream(f, true);
				ftpClient.setRestartOffset(localSize);
				in = ftpClient.retrieveFileStream(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
				byte[] bytes = new byte[1024];
				long step = (lRemoteSize / 100)==0?1:(lRemoteSize / 100);
				long process = localSize / step;
				int c;
				while ((c = in.read(bytes)) != -1) {
					out.write(bytes, 0, c);
					localSize += c;
					long nowProcess = localSize / step;
					if (nowProcess > process) {
						process = nowProcess;
						if (process % 10 == 0);
					}
				}
				in.close();
				out.close();
				boolean isDo = ftpClient.completePendingCommand();
				if (isDo) {
					result = DownloadStatus.DOWNLOAD_FROM_BREAK_SUCCESS;
				} else {
					result = DownloadStatus.DOWNLOAD_FROM_BREAK_FAILED;
				}
			} else {
				out = new FileOutputStream(f);
				in = ftpClient.retrieveFileStream(new String(remote.getBytes("UTF-8"), "iso-8859-1"));
				byte[] bytes = new byte[1024];
				long step = (lRemoteSize / 100)==0?1:(lRemoteSize / 100);
				long process = 0;
				long localSize = 0L;
				int c;
				while ((c = in.read(bytes)) != -1) {
					out.write(bytes, 0, c);
					localSize += c;
					long nowProcess = localSize / step;
					if (nowProcess > process) {
						process = nowProcess;
						if (process % 10 == 0)
							System.out.println("处理进度：" + process);
					}
				}
				in.close();
				out.close();
				boolean upNewStatus = ftpClient.completePendingCommand();
				if (upNewStatus) {
					result = DownloadStatus.DOWNLOAD_NEW_SUCCESS;
				} else {
					result = DownloadStatus.DOWNLOAD_NEW_FAILED;
				}
			}
		} catch (Exception e) {
			throw new Exception("Download file exceptions."+e.getMessage());
		} finally{
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
	 * 锟叫伙拷目录
	 * @param remote
	 * @return
	 */
	public Boolean changworkdir(String remote_path){
		Boolean isSucceded = false ;
		try {
			isSucceded = ftpClient.changeWorkingDirectory(remote_path) ;
			return isSucceded ;
		} catch (Exception e) {
			logger.error("Failed to change working directory , error message as follows : "+e.getMessage(),e);
			throw new RuntimeException("Failed to change working directory , error message as follows : "+e.getMessage(),e) ;
		}
	}
	
	public Boolean deleteFile(String remoteFileName) throws Exception{
		Boolean _rs = false;
		try {
			_rs= ftpClient.deleteFile(remoteFileName);
		} catch (Exception e) {
			throw new Exception ("Delete file exception ."+e.getMessage());
		}
		return _rs;
	}
}
package com.demo.util;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil
{
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void deleteAllFile(String dirUrl)
    {
        File srcDir = new File(dirUrl);
        if (srcDir != null && srcDir.exists() && !srcDir.isDirectory())
            return;

        File[] files = srcDir.listFiles(new FileFilter()
        { //读取路径中的非目录文件
                    @Override
                    public boolean accept(File file)
                    {
                        return file.isFile();
                    }
                });

        for (File file : files)
            file.delete();
    }

    public static TreeMap<Integer, File> getAllFilesForHK(String dirUrl, final String suffix)
    {
        File srcDir = new File(dirUrl);
        if (srcDir != null && srcDir.exists() && !srcDir.isDirectory())
            return null;

        File[] files = srcDir.listFiles(new FileFilter()
        { //读取路径中的非目录文件
                    @Override
                    public boolean accept(File file)
                    {
                        if (suffix == null || suffix.equals(""))
                            return file.isFile();
                        else
                            return file.isFile() && file.getName().endsWith(suffix);
                    }
                });

        if(files.length < 1)
            return null;
        
        TreeMap<Integer, File> map = new TreeMap<Integer, File>();
        
        for(File file : files)
        {
            String fileName = file.getName();
            
//            Integer key = new Integer(fileName.replaceAll("TBS_", "").replaceAll("FBS_", "").replaceAll(".xml", ""));
            Integer key = null ;
            if(suffix != null && !"".equals(suffix)){
                key = new Integer(fileName.substring(14,fileName.indexOf(suffix))) ;
            }else{
                key = new Integer(fileName.substring(14)) ;
            }
            map.put(key, file);
        }
        
//        for(Integer key : map.keySet())
//        {
//            System.out.println(key + " : " + map.get(key).getName() );
//        }
        return map;
    }
    
    public static TreeMap<Integer, File> getAllFiles(String dirUrl, final String suffix)
    {
        File srcDir = new File(dirUrl);
        if (srcDir != null && srcDir.exists() && !srcDir.isDirectory())
            return null;

        File[] files = srcDir.listFiles(new FileFilter()
        { //读取路径中的非目录文件
                    @Override
                    public boolean accept(File file)
                    {
                        if (suffix == null || suffix.equals(""))
                            return file.isFile();
                        else
                            return file.isFile() && file.getName().endsWith(suffix);
                    }
                });

        if(files.length < 1)
            return null;
        
        TreeMap<Integer, File> map = new TreeMap<Integer, File>();
        
        for(File file : files)
        {
            String fileName = file.getName();
            
            Integer key = null ;
            
            if(suffix != null && !"".equals(suffix)){
                key = new Integer(fileName.substring(14,fileName.indexOf(suffix))) ;
            }else{
                key = new Integer(fileName.replaceAll("TBS_", "").replaceAll("FBS_", "").replaceAll(".xml", ""));
            }
            map.put(key, file);
        }
        
        for(Integer key : map.keySet())
        {
            System.out.println(key + " : " + map.get(key).getName() );
        }
        return map;
    }

    

    public static String readFile(File file, String encoding)
    {
        String rtnStr = "";
        DataInputStream dis = null ;
        try
        {
            dis = new DataInputStream(new FileInputStream(file)) ;
            byte[] b = new byte[dis.available()] ;
            dis.read(b) ;
            rtnStr = new String(b, Charset.forName(encoding)) ;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.info("读取文件出错！文件名：" + file.getName() + ".错误信息：\n" + e.getMessage()) ;
            throw new RuntimeException("读取文件出错！文件名：" + file.getName() + ".错误信息：\n" + e.getMessage(), e.getCause()) ;
        }finally{
            if(dis != null){
                try
                {
                    dis.close() ;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    logger.info("关闭数据流报错，错误原因：\n" + e.getMessage()) ;
                }
            }
        }
        return rtnStr;
    }

    public static boolean backupFiles(File oldFile, File newFile)
    {
        if(newFile.exists()){  //如果文件在备份文件夹中存在，则删除文件
            newFile.delete() ;
        }
        boolean isMoved = oldFile.renameTo(newFile);    //移动文件到备份文件夹
        oldFile.delete();  
        return isMoved;
    }

    public static void deleteFile(String filePath)
    {
        File mainFile = new File(filePath);
        mainFile.deleteOnExit();
        logger.info("删除文件成功：" + filePath);
    }

    public static byte[] convertFile2Byte(File file) throws Exception
    {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        byte[] bytes = new byte[(int) length];
        int offset = 0;

        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
        {
            offset += numRead;
        }
        is.close();
        return bytes;
    }
    
    /**
     * 截取含有中文的字符串方法
     * @param str   字符串
     * @param start 起始字节
     * @param end   终止字节
     * @return
     */
    public static String getSubStr(String str,int start ,int end){
        int offset = 0;
        int subStart = 0;
        int subEnd = 0 ;
        String rtnStr = "" ;
        
        if(start > end){
            throw new RuntimeException("字符串截取报错，起始字节数不能大于终止字节数!");
        }else if(end > str.getBytes().length ){
            throw new RuntimeException("终止字节数不能大于字符串的总字节数！");
        }
        
        for(int i=0;i<str.length();i++){
            char ch = str.charAt(i);
            
            int t = ch & 0xff00; //判断读取字符是否为中文
            if(t > 0){  //如果为中文则字节数+2，否则+1
                offset += 2;
            }else{
                offset++;
            }
            
            if(offset <= start){    //字符串起始长度
                subStart++;
            }
            if(offset <= end){  //字符串终止长度
                subEnd++;
            }else{
                break ;
            }
        }
        rtnStr = str.substring(subStart,subEnd);
        return rtnStr.trim() ;
    }
    
    public static void write(String content,String path,String filename,String encoding){
    	BufferedWriter bw = null ;
    	try {
    		if(encoding==null || "".equals(encoding)){
    			encoding = "GBK" ;
    		}
    		File file = new File(path);
    		if(!file.exists()){
    			file.mkdirs() ;
    		}
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+filename), encoding)) ;
			bw.write(content);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("写本地文件出错，详细原因:{}",e);
			throw new RuntimeException("写本地文件出错，详细原因:" + e.getMessage(),e) ;
		}finally{
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.info("关闭bw出错。");
				}
			}
		}
    }
}

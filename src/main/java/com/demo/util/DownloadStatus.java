package com.demo.util;

/**
 * FTP 下载状态枚举类型
 * 
 *  
 *
 */
public enum DownloadStatus {  
    REMOTE_FILE_NOEXIST,    //远程文件不存在  
    LOCAL_BIGGER_REMOTE,    //本地文件大于远程文件  
    DOWNLOAD_FROM_BREAK_SUCCESS,    //断点下载文件成功  
    DOWNLOAD_FROM_BREAK_FAILED,     //断点下载文件失败  
    DOWNLOAD_NEW_SUCCESS,           //全新下载文件成功  
    DOWNLOAD_NEW_FAILED;            //全新下载文件失败  
} 

package com.demo.util;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class SocketUtil {

	/**
	 * 以Socket形式发送报文。
	 * @param content 报文内容
	 * @param ip 报文接收服务器。
	 * @param port 报文接收服务器通信端口。
	 * @return String 处理结果。
	 * @throws Exception
	 */
	public static String sendMessageClient(String content,String ip,int port,String sCharSet) throws Exception{
		SocketChannel sc=SocketChannel.open();
		try
		{
			sc.configureBlocking(false);    //需在sc.connect之前设置
			sc.connect(new InetSocketAddress(ip,port));
			Selector selector=Selector.open();
			sc.register(selector, SelectionKey.OP_CONNECT);
			StringBuffer sB = new StringBuffer();
			
			for(int i=0;i<2;i++){
			//while(true){
				if(sc.isConnected()){
	
					StringBuffer sb = new StringBuffer(content);	//发送的内容
					CharBuffer cb = CharBuffer.wrap(sb);
					
					ByteBuffer bb=Charset.forName(sCharSet).encode(cb);	//转换编码为unicode
					
					int writeBytes=sc.write(bb);	//写数据
					bb.clear();
					
					if(writeBytes==0){
						sc.register(selector, SelectionKey.OP_WRITE);    //注册写事件（当写缓冲区满时）
					}
					sb.setLength(0);
					cb.clear();
				}
				int selectKeys=selector.select();
				if(selectKeys==0){
					continue;
				}
				for(SelectionKey key:selector.selectedKeys()){
					if(key.isConnectable()){
						SocketChannel socketChanel=(SocketChannel)key.channel();
						if(socketChanel==null){
							continue;
						}
						socketChanel.configureBlocking(false);
						socketChanel.register(selector,SelectionKey.OP_READ);
						socketChanel.finishConnect();
					}else if(key.isReadable()){
						SocketChannel socketChanel=(SocketChannel)key.channel();
						ByteBuffer bf=ByteBuffer.allocate(1024);
						while(socketChanel.read(bf)>0){
							bf.flip();
							
							sB.append(Charset.forName(sCharSet).decode(bf).toString());
							bf.clear();
							Thread.sleep(100);
						}
					}else if(key.isWritable()){
						//只要写缓冲区未满就一直会产生写事件，如果此时又不写数据时，会产生不必要的资源损耗，所以这里需要取消写事件以免cpu消耗100%
						//写数据，如果写缓冲区满时继续注册写事件key.interestOps(key.interestOps()|SelectionKey.OP_WRITE);
						key.interestOps(key.interestOps()&(~SelectionKey.OP_WRITE));
					}
				}
				selector.selectedKeys().clear();
				
			}
			String str = sB.toString();
			selector.close();
			sB.setLength(0);
			return str;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			sc.close();
		}
	}
}

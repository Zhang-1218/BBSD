package com.xalo.bbs.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.xalo.bbs.entity.User;

import java.util.Set;

import java.io.*;
//服务端的功能
/*
 * 服务端的业务功能
 * 1.可以接收到各个客户端的消息，并进行分发
 * 2.可以存储所有的连接用户
 * 3.启动服务
 * 4.关闭服务
 * 
 */
public class ServerService {
	//是否启动服务的标志
	protected volatile static boolean isStart = false;
	//服务端的socket，用来接收消息
	protected static DatagramSocket socket;
	//存储所有连接的用户
	protected volatile static HashMap<String, User> allUsers;
	
	static {
		try {
			if(socket==null) {
				socket=new DatagramSocket(6666);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		if(allUsers==null) {
			allUsers=new HashMap<String, User>();
		}
		
	}
	/*
	 * 开辟一个新线程，用来接收消息，在接收到消息的时候
	 * 需要判断该消息的种类：
	 * 		1.连接消息
	 * 		2.聊天消息
	 * 		3.断开消息
	 * 如何区分消息种类，：我们接收到客户端发送过来的消息
	 * 	从数据包中的得到该用户的ip，判断之前有没有该ip，
	 * 	如果有，说明是2或者3，如果没有则是1.如果接收到的
	 * 	消息内容为over，那么就断开连接
	 * 
	 */
	
	//启动服务端
	public void startServer() {
		
		isStart = true;
		//用以启动线程
		new ServerThread().start();
	}
	
	//关闭服务端
	public void stopServer() {
		isStart=false;
	}
	
}


//开辟线程，处理消息，用户等的操作
class ServerThread extends Thread{
	public void run() {
		while(ServerService.isStart) {
			//接收各种消息
			//创建数据包，用来接收消息
			byte[] cache = new byte[1024*64];
			DatagramPacket receivePacket = new DatagramPacket(cache, cache.length);
			try {
				ServerService.socket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*
			 * 1.如果是连接消息，需要将用户添加进map中
			 * 收到消息之后，ip作为键，user作为值
			 */
			//1.得到客户端的ip
			String clientIP = receivePacket.getAddress().getHostAddress();
			/*
			 * 2.判断当前的map中是否有次键，如果没有，则说明是连接消息
			 *  不需要将此消息分发给其他用户
			 */
			Set allKeys = ServerService.allUsers.keySet();
			if(allKeys == null || !allKeys.contains(clientIP)) {
				//当前消息为连接消息，将此用户存入map中
				byte[] userData = receivePacket.getData();
			
				//将字节流转换为User对象		
				try {
					ByteArrayInputStream in = new ByteArrayInputStream(userData,0,receivePacket.getLength());
					ObjectInputStream objectIn=new ObjectInputStream(in);
					User user = (User)objectIn.readObject();
					//新上线的用户已经添加进了map中
					ServerService.allUsers.put(clientIP, user);
				} catch (IOException e) {
					
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					
					e.printStackTrace();
				}
				
			}else {
				//2.分发消息给各个客户端     3.断开连接的消息
				//step1.拿到消息内容
				byte[] content = receivePacket.getData();
				String contentStr = new String(content,0,receivePacket.getLength());
				//首尾的空白去除
				contentStr = contentStr.trim();
				if(contentStr.equals("over")) {
					//断开连接
					ServerService.allUsers.remove(clientIP);
					
				}else {
					//正常消息，为其他客户端分发该条消息
					//遍历map，获取其他客户端的ip和端口号，发送消息
					Iterator<Entry<String,User>> iterator = ServerService.allUsers.entrySet().iterator();
					//将用户名和消息进行拼接
					User currentUser = ServerService.allUsers.get(clientIP);
					String currentName = currentUser.name;
					String conStr = currentName+":\n "+contentStr+"\n";
					DatagramPacket sendPacket = new DatagramPacket(conStr.getBytes(), conStr.getBytes().length);
					
					while(iterator.hasNext()){
						Entry<String,User> entry=iterator.next();
						String keyIP = entry.getKey();
						User valueUser = entry.getValue();
						if(keyIP.equals(clientIP)) {
							continue;
						}
						//分发消息
						try {
							sendPacket.setAddress(InetAddress.getByName(valueUser.uIP));
						} catch (UnknownHostException e) {

							e.printStackTrace();
						}
						
						sendPacket.setPort(Integer.parseInt(valueUser.uPort));
						
						try {
							ServerService.socket.send(sendPacket);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
					}
				
				}
				
			}
		}
	}
}












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
//����˵Ĺ���
/*
 * ����˵�ҵ����
 * 1.���Խ��յ������ͻ��˵���Ϣ�������зַ�
 * 2.���Դ洢���е������û�
 * 3.��������
 * 4.�رշ���
 * 
 */
public class ServerService {
	//�Ƿ���������ı�־
	protected volatile static boolean isStart = false;
	//����˵�socket������������Ϣ
	protected static DatagramSocket socket;
	//�洢�������ӵ��û�
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
	 * ����һ�����̣߳�����������Ϣ���ڽ��յ���Ϣ��ʱ��
	 * ��Ҫ�жϸ���Ϣ�����ࣺ
	 * 		1.������Ϣ
	 * 		2.������Ϣ
	 * 		3.�Ͽ���Ϣ
	 * ���������Ϣ���࣬�����ǽ��յ��ͻ��˷��͹�������Ϣ
	 * 	�����ݰ��еĵõ����û���ip���ж�֮ǰ��û�и�ip��
	 * 	����У�˵����2����3�����û������1.������յ���
	 * 	��Ϣ����Ϊover����ô�ͶϿ�����
	 * 
	 */
	
	//���������
	public void startServer() {
		
		isStart = true;
		//���������߳�
		new ServerThread().start();
	}
	
	//�رշ����
	public void stopServer() {
		isStart=false;
	}
	
}


//�����̣߳�������Ϣ���û��ȵĲ���
class ServerThread extends Thread{
	public void run() {
		while(ServerService.isStart) {
			//���ո�����Ϣ
			//�������ݰ�������������Ϣ
			byte[] cache = new byte[1024*64];
			DatagramPacket receivePacket = new DatagramPacket(cache, cache.length);
			try {
				ServerService.socket.receive(receivePacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			/*
			 * 1.�����������Ϣ����Ҫ���û���ӽ�map��
			 * �յ���Ϣ֮��ip��Ϊ����user��Ϊֵ
			 */
			//1.�õ��ͻ��˵�ip
			String clientIP = receivePacket.getAddress().getHostAddress();
			/*
			 * 2.�жϵ�ǰ��map���Ƿ��дμ������û�У���˵����������Ϣ
			 *  ����Ҫ������Ϣ�ַ��������û�
			 */
			Set allKeys = ServerService.allUsers.keySet();
			if(allKeys == null || !allKeys.contains(clientIP)) {
				//��ǰ��ϢΪ������Ϣ�������û�����map��
				byte[] userData = receivePacket.getData();
			
				//���ֽ���ת��ΪUser����		
				try {
					ByteArrayInputStream in = new ByteArrayInputStream(userData,0,receivePacket.getLength());
					ObjectInputStream objectIn=new ObjectInputStream(in);
					User user = (User)objectIn.readObject();
					//�����ߵ��û��Ѿ���ӽ���map��
					ServerService.allUsers.put(clientIP, user);
				} catch (IOException e) {
					
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					
					e.printStackTrace();
				}
				
			}else {
				//2.�ַ���Ϣ�������ͻ���     3.�Ͽ����ӵ���Ϣ
				//step1.�õ���Ϣ����
				byte[] content = receivePacket.getData();
				String contentStr = new String(content,0,receivePacket.getLength());
				//��β�Ŀհ�ȥ��
				contentStr = contentStr.trim();
				if(contentStr.equals("over")) {
					//�Ͽ�����
					ServerService.allUsers.remove(clientIP);
					
				}else {
					//������Ϣ��Ϊ�����ͻ��˷ַ�������Ϣ
					//����map����ȡ�����ͻ��˵�ip�Ͷ˿ںţ�������Ϣ
					Iterator<Entry<String,User>> iterator = ServerService.allUsers.entrySet().iterator();
					//���û�������Ϣ����ƴ��
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
						//�ַ���Ϣ
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












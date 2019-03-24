package com.xalo.bbs.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.xalo.bbs.entity.User;

import java.io.*;

//处理客户端的业务
/*
 * 功能需求：
 * 1.连接服务器
 * 2.断开服务器
 * 3.发送消息
 * 4.接收服务端发送过来的消息
 * 5.显示在线用户的列表
 * 
 */
public class ClientService {

	private JFrame superFrame;
	
	public ClientService(JFrame superFrame) {
		this.superFrame=superFrame;
	}
	/*
	 * volatile： 当某个变量在多个线程中用到时，
	 * 可以用它来进行修饰，最大程度的做到同一时刻，
	 * 只有一个线程对该变量进行了修改
	 */
	protected static volatile boolean isConn=false;
	//接收和发送消息的socket
	private static DatagramSocket socket;
	private String userName;//拿到当前用户姓名
	/*
	 * 静态代码块：
	 * 1.其中的代码最先执行
	 * 2.所有代码只执行一次
	 * 
	 */
	static {
		try {
			socket=new DatagramSocket(5555);
		} catch (SocketException e) {
			System.out.println("");
			e.printStackTrace();
		}
	}
	
	//1.连接服务器
	//  给定一个状态，如果是未连接，不让发送消息
	//2.将当前用户信息交给服务器，服务器对信息进行存储
	public void connServer(User user, String port, String ip, JTextArea textArea, DefaultListModel listModel) {
		if(isConn) {
			//弹出提示框，提示用户已经建立连接
			JOptionPane.showMessageDialog(superFrame, "服务器已连接", "友情提示！", JOptionPane.PLAIN_MESSAGE);
			return;
		}else if(port==null || port.equals("")) {
			JOptionPane.showMessageDialog(superFrame, "请填写服务器的端口号", "友情提示！", JOptionPane.PLAIN_MESSAGE);
			return;
		}else if(ip==null || ip.equals("")) {
			JOptionPane.showMessageDialog(superFrame, "请填写服务器的ip地址", "友情提示！", JOptionPane.PLAIN_MESSAGE);
			return;
		}else if(user==null || user.name==null || user.name.equals("")) {
			JOptionPane.showMessageDialog(superFrame, "请填写用户名", "友情提示！", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		//将用户信息发送给服务端
		//将用户信息序列化为字节码
		try {
			ByteArrayOutputStream byOut = new ByteArrayOutputStream();
			ObjectOutputStream objectOut=new ObjectOutputStream(byOut);
			//序列化对象，将对象转化为字节流，以便使用；前提是user的类必须实现Serializable接口
			objectOut.writeObject(user);
			//获取当前已连接的用户的姓名
			this.userName=user.name;
			byte[] userByte = byOut.toByteArray();
			
			//构建要发送给服务器的数据包
			DatagramPacket packet=new DatagramPacket(userByte, userByte.length,
								 					InetAddress.getByName(ip),Integer.parseInt(port));
			//发送此数据包给服务端
			socket.send(packet);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		isConn = true;
	
		//接收消息
		this.receiveMsg(textArea);
		//显示在线用户
		this.showOnLineUsers(listModel);
		
	}

	//2.断开连接
	public void disConnServer() {
		if(!isConn) {
			//已经断开连接
			JOptionPane.showMessageDialog(superFrame, "已经断开连接", "提示！", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		isConn = false;
	}

	//3.发送消息
	/*
	 * 需要view层将服务器的端口号，ip，用户信息，发送内容
	 */
	public void sendMsg(JTextField sendTF, String port, String ip, String content, JTextArea textArea) {
		/*
		 * 判断是否连接，端口号，ip，是否右发送内容
		 * 如果都有，再发送
		 */
		//提示的信息
		
		/*****注意：此代码测试使用，正式必须删除******/
		isConn=true;
		/*****张航 2017-12-29******/
		
		String infoString =null;
		if(!isConn) {
			//未连接服务
			infoString="未连接服务器";
		}else if(port==null || port.equals("")) {
			infoString="请填写服务器端口号";
		}else if(ip==null || ip.equals("")) {
			infoString="请填写服务器ip地址";
		}else if(content==null||content.equals("")) {
			infoString="发送内容不能为空";
		}
		
		if(infoString!=null) {
			JOptionPane.showMessageDialog(this.superFrame, infoString, "友情提示！", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		//发送消息到服务器端
		try {
			DatagramPacket packet=new DatagramPacket(content.getBytes(), content.getBytes().length,
												  InetAddress.getByName(ip),Integer.parseInt(port));
			socket.send(packet);
			content=content.trim();
			if(content.equals("over")) {
				this.isConn=false;
			}
			//显示在当前的聊天内容的文本区
			textArea.append(this.userName+":\n");
			textArea.append("   "+content+"\n");
			//将发送消息的文本框清空
			sendTF.setText("");
		} catch (NumberFormatException e) {
	
			e.printStackTrace();
		} catch (UnknownHostException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	//4.接收消息
	public void receiveMsg(JTextArea textArea) {
		/*
		 * 由于接收消息是被动的，所以我们需要一直监听
		 * 服务端是否有消息发送过来，所以开辟一个线程，一直执行
		 * 直到退出客户端，或者断开链接为止
		 * 
		 */
		new ReceiveMsgThread(this.socket, textArea).start();
	}
	
	//5.显示在线用户
	public void showOnLineUsers(DefaultListModel listModel) {
//		//先将当前列表清空
//		listModel.removeAllElements();
//		//添加用户名
//		Iterator<User> iterator = ServerService.allUsers.values().iterator();
//		while(iterator.hasNext()) {
//			User user=iterator.next();
//			listModel.addElement(user.name);
//		}		
	}
}


//创建线程类，专门 用来处理接收消息的功能
class ReceiveMsgThread extends Thread{
	
	private DatagramSocket socket;
	private JTextArea textArea;
	//通过构造方法参数，拿到socket对象，进行数据处理
	public ReceiveMsgThread(DatagramSocket socket, JTextArea textArea) {
		this.socket=socket;
		this.textArea=textArea;
	}
	
	public void run() {
		//一直监测是否有发送过来消息
		while (ClientService.isConn) {
			//新建接收消息的数据包
			byte[] rByte = new byte[1024*64];
			DatagramPacket rPacket = new DatagramPacket(rByte, rByte.length);
			try {
				this.socket.receive(rPacket);
				//获取接收到的内容
				byte[] contentByte=rPacket.getData();
				String contentStr=new String(contentByte, 0, rPacket.getLength());
				//将消息内容显示在textArea中
				this.textArea.append(contentStr);
			} catch (IOException e) {
				System.out.println("接收消息失败");
				e.printStackTrace();
			}
			
		}
	}
}







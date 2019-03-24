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

//����ͻ��˵�ҵ��
/*
 * ��������
 * 1.���ӷ�����
 * 2.�Ͽ�������
 * 3.������Ϣ
 * 4.���շ���˷��͹�������Ϣ
 * 5.��ʾ�����û����б�
 * 
 */
public class ClientService {

	private JFrame superFrame;
	
	public ClientService(JFrame superFrame) {
		this.superFrame=superFrame;
	}
	/*
	 * volatile�� ��ĳ�������ڶ���߳����õ�ʱ��
	 * �����������������Σ����̶ȵ�����ͬһʱ�̣�
	 * ֻ��һ���̶߳Ըñ����������޸�
	 */
	protected static volatile boolean isConn=false;
	//���պͷ�����Ϣ��socket
	private static DatagramSocket socket;
	private String userName;//�õ���ǰ�û�����
	/*
	 * ��̬����飺
	 * 1.���еĴ�������ִ��
	 * 2.���д���ִֻ��һ��
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
	
	//1.���ӷ�����
	//  ����һ��״̬�������δ���ӣ����÷�����Ϣ
	//2.����ǰ�û���Ϣ����������������������Ϣ���д洢
	public void connServer(User user, String port, String ip, JTextArea textArea, DefaultListModel listModel) {
		if(isConn) {
			//������ʾ����ʾ�û��Ѿ���������
			JOptionPane.showMessageDialog(superFrame, "������������", "������ʾ��", JOptionPane.PLAIN_MESSAGE);
			return;
		}else if(port==null || port.equals("")) {
			JOptionPane.showMessageDialog(superFrame, "����д�������Ķ˿ں�", "������ʾ��", JOptionPane.PLAIN_MESSAGE);
			return;
		}else if(ip==null || ip.equals("")) {
			JOptionPane.showMessageDialog(superFrame, "����д��������ip��ַ", "������ʾ��", JOptionPane.PLAIN_MESSAGE);
			return;
		}else if(user==null || user.name==null || user.name.equals("")) {
			JOptionPane.showMessageDialog(superFrame, "����д�û���", "������ʾ��", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		//���û���Ϣ���͸������
		//���û���Ϣ���л�Ϊ�ֽ���
		try {
			ByteArrayOutputStream byOut = new ByteArrayOutputStream();
			ObjectOutputStream objectOut=new ObjectOutputStream(byOut);
			//���л����󣬽�����ת��Ϊ�ֽ������Ա�ʹ�ã�ǰ����user�������ʵ��Serializable�ӿ�
			objectOut.writeObject(user);
			//��ȡ��ǰ�����ӵ��û�������
			this.userName=user.name;
			byte[] userByte = byOut.toByteArray();
			
			//����Ҫ���͸������������ݰ�
			DatagramPacket packet=new DatagramPacket(userByte, userByte.length,
								 					InetAddress.getByName(ip),Integer.parseInt(port));
			//���ʹ����ݰ��������
			socket.send(packet);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		isConn = true;
	
		//������Ϣ
		this.receiveMsg(textArea);
		//��ʾ�����û�
		this.showOnLineUsers(listModel);
		
	}

	//2.�Ͽ�����
	public void disConnServer() {
		if(!isConn) {
			//�Ѿ��Ͽ�����
			JOptionPane.showMessageDialog(superFrame, "�Ѿ��Ͽ�����", "��ʾ��", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		
		isConn = false;
	}

	//3.������Ϣ
	/*
	 * ��Ҫview�㽫�������Ķ˿ںţ�ip���û���Ϣ����������
	 */
	public void sendMsg(JTextField sendTF, String port, String ip, String content, JTextArea textArea) {
		/*
		 * �ж��Ƿ����ӣ��˿ںţ�ip���Ƿ��ҷ�������
		 * ������У��ٷ���
		 */
		//��ʾ����Ϣ
		
		/*****ע�⣺�˴������ʹ�ã���ʽ����ɾ��******/
		isConn=true;
		/*****�ź� 2017-12-29******/
		
		String infoString =null;
		if(!isConn) {
			//δ���ӷ���
			infoString="δ���ӷ�����";
		}else if(port==null || port.equals("")) {
			infoString="����д�������˿ں�";
		}else if(ip==null || ip.equals("")) {
			infoString="����д������ip��ַ";
		}else if(content==null||content.equals("")) {
			infoString="�������ݲ���Ϊ��";
		}
		
		if(infoString!=null) {
			JOptionPane.showMessageDialog(this.superFrame, infoString, "������ʾ��", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		//������Ϣ����������
		try {
			DatagramPacket packet=new DatagramPacket(content.getBytes(), content.getBytes().length,
												  InetAddress.getByName(ip),Integer.parseInt(port));
			socket.send(packet);
			content=content.trim();
			if(content.equals("over")) {
				this.isConn=false;
			}
			//��ʾ�ڵ�ǰ���������ݵ��ı���
			textArea.append(this.userName+":\n");
			textArea.append("   "+content+"\n");
			//��������Ϣ���ı������
			sendTF.setText("");
		} catch (NumberFormatException e) {
	
			e.printStackTrace();
		} catch (UnknownHostException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	//4.������Ϣ
	public void receiveMsg(JTextArea textArea) {
		/*
		 * ���ڽ�����Ϣ�Ǳ����ģ�����������Ҫһֱ����
		 * ������Ƿ�����Ϣ���͹��������Կ���һ���̣߳�һֱִ��
		 * ֱ���˳��ͻ��ˣ����߶Ͽ�����Ϊֹ
		 * 
		 */
		new ReceiveMsgThread(this.socket, textArea).start();
	}
	
	//5.��ʾ�����û�
	public void showOnLineUsers(DefaultListModel listModel) {
//		//�Ƚ���ǰ�б����
//		listModel.removeAllElements();
//		//����û���
//		Iterator<User> iterator = ServerService.allUsers.values().iterator();
//		while(iterator.hasNext()) {
//			User user=iterator.next();
//			listModel.addElement(user.name);
//		}		
	}
}


//�����߳��࣬ר�� �������������Ϣ�Ĺ���
class ReceiveMsgThread extends Thread{
	
	private DatagramSocket socket;
	private JTextArea textArea;
	//ͨ�����췽���������õ�socket���󣬽������ݴ���
	public ReceiveMsgThread(DatagramSocket socket, JTextArea textArea) {
		this.socket=socket;
		this.textArea=textArea;
	}
	
	public void run() {
		//һֱ����Ƿ��з��͹�����Ϣ
		while (ClientService.isConn) {
			//�½�������Ϣ�����ݰ�
			byte[] rByte = new byte[1024*64];
			DatagramPacket rPacket = new DatagramPacket(rByte, rByte.length);
			try {
				this.socket.receive(rPacket);
				//��ȡ���յ�������
				byte[] contentByte=rPacket.getData();
				String contentStr=new String(contentByte, 0, rPacket.getLength());
				//����Ϣ������ʾ��textArea��
				this.textArea.append(contentStr);
			} catch (IOException e) {
				System.out.println("������Ϣʧ��");
				e.printStackTrace();
			}
			
		}
	}
}







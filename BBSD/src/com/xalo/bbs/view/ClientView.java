package com.xalo.bbs.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.xalo.bbs.entity.User;
import com.xalo.bbs.service.ClientService;

public class ClientView {

	public static void main(String[] args) {
		
		ClientFrame c1=new ClientFrame();
	}

}

//创建客户端界面，frame使用的布局凡是为边界布局
//分为北，中，南三部分

class ClientFrame extends JFrame{
	//将要用到的控件写成私有的成员变量
	//北部部分
	/*
	 * 采用的是网格布局,一行八列
	*/
	private JPanel northPanel;//北部最底层容器
	private JLabel portLabel;//显示端口号的标签
	private JTextField portTF;//显示端口号
	private JLabel serverIPLabel;//显示服务器ip标签
	private JTextField serverIPTF;//显示服务器ip
	private JLabel nameLabel;//显示当前用户的姓名标签
	private JTextField nameTF;//显示当前用户的姓名
	private JButton connButton;//连接服务器按钮
	private JButton disconnButton;//断开服务器按钮

	//中部部分
	private JSplitPane splitPane;//承载中部所有组件的底层容器
	private JScrollPane leftScrollPane;//显示在线用户容器
	private JList userListView;//显示在线用户的组件
	private DefaultListModel userModel;//管理list上的数据
	private JScrollPane rightScrollPane;//显示聊天内容容器
	private JTextArea contentTextArea;//显示聊天内容的组件
	
	//底部部分
	private JPanel southPanel;//底部的底层容器
	private JTextField inputTF;//输入消息框
	private JButton sendBtn;//发送按钮
	
	
	private ClientService clientService;
	
	//自定义构造方法,在构造方法中队控件进行初始化布局
	public ClientFrame () {
		this.clientService=new ClientService(this);
		
		//北部部分
		this.northPanel=new JPanel();
		this.northPanel.setBorder(new TitledBorder("连接信息"));
		this.portLabel=new JLabel("端口");
		this.portTF=new JTextField("6666");
		this.serverIPLabel=new JLabel("服务器IP");
		this.serverIPTF=new JTextField("127.0.0.1");
		this.nameLabel=new JLabel("姓名");
		this.nameTF=new JTextField("迪克牛仔");
		this.connButton=new JButton("连接");
		this.disconnButton=new JButton("断开");
		//设置布局管理器
		this.northPanel.setLayout(new GridLayout(1, 8));
		this.northPanel.add(this.portLabel);
		this.northPanel.add(this.portTF);
		this.northPanel.add(this.serverIPLabel);
		this.northPanel.add(this.serverIPTF);
		this.northPanel.add(this.nameLabel);
		this.northPanel.add(this.nameTF);
		this.northPanel.add(this.connButton);
		this.northPanel.add(this.disconnButton);
		
		//中部部分
		//中左部分
		this.userModel=new DefaultListModel();
		//创建list对象，并将管理数据传入
		this.userListView=new JList(this.userModel);		
		this.leftScrollPane=new JScrollPane(this.userListView);
		//设置标题
		this.leftScrollPane.setBorder(new TitledBorder("在线用户"));
		
		//中右部分
		this.contentTextArea=new JTextArea();
		//设置是否允许被编辑
		this.contentTextArea.setEnabled(false);
		this.rightScrollPane=new JScrollPane(this.contentTextArea);
		this.rightScrollPane.setBorder(new TitledBorder("这是聊天信息显示框："));
		
		//中间部分最底层容器
		this.splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,this.leftScrollPane,this.rightScrollPane);
		//中间分割线的位置
		this.splitPane.setDividerLocation(100);
		
		//底部部分
		this.southPanel=new JPanel();
		this.southPanel.setBorder(new TitledBorder("发送消息"));
		this.southPanel.setLayout(new BorderLayout());
		this.inputTF=new JTextField();
		this.sendBtn=new JButton("发送");
		this.southPanel.add(this.inputTF,BorderLayout.CENTER);
		this.southPanel.add(this.sendBtn,BorderLayout.EAST);
		
		//最底层容器
		this.setLayout(new BorderLayout());
		this.setTitle("客户机");
		this.setSize(750, 400);
		this.setLocationRelativeTo(null);
		this.add(this.northPanel,BorderLayout.NORTH);
		this.add(this.splitPane,BorderLayout.CENTER);
		this.add(this.southPanel, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
		//添加监听事件
		//连接
		this.connButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//连接服务器
				/*
				 * 调用service层中连接服务器的方法即可
				 */
				System.out.println("连接服务器");
				User user=new User();
				user.name=nameTF.getText();
				try {
					user.uIP=InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				user.uPort="5555";
				
				clientService.connServer(user, portTF.getText(), serverIPTF.getText(),contentTextArea, userModel);
			}
		});
		
		//断开连接
		this.disconnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				System.out.println("断开连接调用的方法");
				
				clientService.disConnServer();
			}
		});
		
		//添加输入消息框的enter事件
		this.inputTF.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("点击了回车键发送消息");
				clientService.sendMsg(inputTF, 
						  portTF.getText(), 
						  serverIPTF.getText(), 
						  inputTF.getText(), contentTextArea);
			}
		});
		
		//发送按钮
		this.sendBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				System.out.println("点击了发送按钮");
				clientService.sendMsg(inputTF, 
									  portTF.getText(), 
									  serverIPTF.getText(), 
									  inputTF.getText(), contentTextArea);
			}
		});
	}
}




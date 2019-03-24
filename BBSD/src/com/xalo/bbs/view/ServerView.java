package com.xalo.bbs.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.xalo.bbs.service.ServerService;

public class ServerView {

	public static void main(String[] args) {
		ServerJFrame s1=new ServerJFrame();
	}

}

class ServerJFrame extends JFrame implements ActionListener{
	//上层
	private JPanel northPanel;
	private JLabel personLabel;
	private JTextField personTF;
	private JLabel portLabel;
	private JTextField portTF;
	private JButton startbtn;
	private JButton endbtn;
	
	//中部
	private JSplitPane splitPane;
	private JScrollPane onlineNum;
	private JList personlist;
	private DefaultListModel listModel;
	private JScrollPane message;
	private JTextArea mesgTF;
	
	//底部
	private JPanel southPanel;
	private JTextField writeMesgTF;
	private JButton sendMesg;
	
	public ServerJFrame() {
		//上部
		this.northPanel=new JPanel();
		this.northPanel.setBorder(new TitledBorder("配置信息"));
		this.personLabel=new JLabel("人数上限");
		this.personTF=new JTextField("30");
		this.portLabel=new JLabel("端口");
		this.portTF=new JTextField("6666");
		this.startbtn=new JButton("启动");
		this.startbtn.addActionListener(this);
		this.endbtn=new JButton("停止");
		this.endbtn.addActionListener(this);
		
		this.northPanel.setLayout(new GridLayout(1,6));
		this.northPanel.add(this.personLabel);
		this.northPanel.add(this.personTF);
		this.northPanel.add(this.portLabel);
		this.northPanel.add(this.portTF);
		this.northPanel.add(this.startbtn);
		this.northPanel.add(this.endbtn);
		
		//中间部分
		//中左
		this.listModel=new DefaultListModel();
		this.personlist=new JList(this.listModel);
		this.onlineNum=new JScrollPane(this.personlist);
		this.onlineNum.setBorder(new TitledBorder("在线用户"));
		//中右
		this.mesgTF=new JTextArea("这是一条测试消息");
		this.message=new JScrollPane(this.mesgTF);
		this.mesgTF.setEnabled(false);
		this.message.setBorder(new TitledBorder("消息显示区"));
		//中部最底层容器
		this.splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,this.onlineNum,this.message);
		this.splitPane.setDividerLocation(100);
		
		//底部
		this.southPanel=new JPanel();
		this.southPanel.setLayout(new BorderLayout());
		this.southPanel.setBorder(new TitledBorder("发送消息"));
		this.writeMesgTF=new JTextField("这是测试语句");
		this.sendMesg=new JButton("发送");
		this.sendMesg.addActionListener(this);
		this.southPanel.add(this.writeMesgTF,BorderLayout.CENTER);
		this.southPanel.add(this.sendMesg,BorderLayout.EAST);
		
		//最底层容器设置
		this.setLayout(new BorderLayout());
		this.setSize(600,400);
		this.setTitle("服务器");
		this.setLocationRelativeTo(null);
		this.add(this.northPanel,BorderLayout.NORTH);
		this.add(this.splitPane, BorderLayout.CENTER);
		this.add(this.southPanel, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
//		//事件
//		//启动
//		this.startbtn.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//
//				System.out.println("服务器已启动");
//			}
//		});
//		
//		//停止
//		this.endbtn.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//		
//				System.out.println("服务器已停止");
//			}
//		});
//		
//		//发送
//		this.sendMesg.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				
//				System.out.println("点击发送键，消息正在发送");
//			}
//		});
//		
//		//添加输入消息框的enter事件
//		this.writeMesgTF.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				
//				System.out.println("点击回车键以发送消息");
//			}
//		});
	}

	//按钮的响应事件
	ServerService ss=new ServerService();
	public void actionPerformed(ActionEvent event) {
		
		if(event.getSource() instanceof JButton) {
			JButton btn = (JButton)event.getSource();
			if(btn.getText().equals("启动")) {
				//启动按钮
				ss.startServer();
			}else if(btn.getText().equals("停止")){
				//停止按钮
				ss.stopServer();
			}else if(btn.getText().equals("发送")) {
				//发送按钮
				
			}
			
		}
		
		
	}
}






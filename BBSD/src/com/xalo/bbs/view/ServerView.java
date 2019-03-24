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
	//�ϲ�
	private JPanel northPanel;
	private JLabel personLabel;
	private JTextField personTF;
	private JLabel portLabel;
	private JTextField portTF;
	private JButton startbtn;
	private JButton endbtn;
	
	//�в�
	private JSplitPane splitPane;
	private JScrollPane onlineNum;
	private JList personlist;
	private DefaultListModel listModel;
	private JScrollPane message;
	private JTextArea mesgTF;
	
	//�ײ�
	private JPanel southPanel;
	private JTextField writeMesgTF;
	private JButton sendMesg;
	
	public ServerJFrame() {
		//�ϲ�
		this.northPanel=new JPanel();
		this.northPanel.setBorder(new TitledBorder("������Ϣ"));
		this.personLabel=new JLabel("��������");
		this.personTF=new JTextField("30");
		this.portLabel=new JLabel("�˿�");
		this.portTF=new JTextField("6666");
		this.startbtn=new JButton("����");
		this.startbtn.addActionListener(this);
		this.endbtn=new JButton("ֹͣ");
		this.endbtn.addActionListener(this);
		
		this.northPanel.setLayout(new GridLayout(1,6));
		this.northPanel.add(this.personLabel);
		this.northPanel.add(this.personTF);
		this.northPanel.add(this.portLabel);
		this.northPanel.add(this.portTF);
		this.northPanel.add(this.startbtn);
		this.northPanel.add(this.endbtn);
		
		//�м䲿��
		//����
		this.listModel=new DefaultListModel();
		this.personlist=new JList(this.listModel);
		this.onlineNum=new JScrollPane(this.personlist);
		this.onlineNum.setBorder(new TitledBorder("�����û�"));
		//����
		this.mesgTF=new JTextArea("����һ��������Ϣ");
		this.message=new JScrollPane(this.mesgTF);
		this.mesgTF.setEnabled(false);
		this.message.setBorder(new TitledBorder("��Ϣ��ʾ��"));
		//�в���ײ�����
		this.splitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,this.onlineNum,this.message);
		this.splitPane.setDividerLocation(100);
		
		//�ײ�
		this.southPanel=new JPanel();
		this.southPanel.setLayout(new BorderLayout());
		this.southPanel.setBorder(new TitledBorder("������Ϣ"));
		this.writeMesgTF=new JTextField("���ǲ������");
		this.sendMesg=new JButton("����");
		this.sendMesg.addActionListener(this);
		this.southPanel.add(this.writeMesgTF,BorderLayout.CENTER);
		this.southPanel.add(this.sendMesg,BorderLayout.EAST);
		
		//��ײ���������
		this.setLayout(new BorderLayout());
		this.setSize(600,400);
		this.setTitle("������");
		this.setLocationRelativeTo(null);
		this.add(this.northPanel,BorderLayout.NORTH);
		this.add(this.splitPane, BorderLayout.CENTER);
		this.add(this.southPanel, BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
//		//�¼�
//		//����
//		this.startbtn.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//
//				System.out.println("������������");
//			}
//		});
//		
//		//ֹͣ
//		this.endbtn.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//		
//				System.out.println("��������ֹͣ");
//			}
//		});
//		
//		//����
//		this.sendMesg.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				
//				System.out.println("������ͼ�����Ϣ���ڷ���");
//			}
//		});
//		
//		//���������Ϣ���enter�¼�
//		this.writeMesgTF.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				
//				System.out.println("����س����Է�����Ϣ");
//			}
//		});
	}

	//��ť����Ӧ�¼�
	ServerService ss=new ServerService();
	public void actionPerformed(ActionEvent event) {
		
		if(event.getSource() instanceof JButton) {
			JButton btn = (JButton)event.getSource();
			if(btn.getText().equals("����")) {
				//������ť
				ss.startServer();
			}else if(btn.getText().equals("ֹͣ")){
				//ֹͣ��ť
				ss.stopServer();
			}else if(btn.getText().equals("����")) {
				//���Ͱ�ť
				
			}
			
		}
		
		
	}
}






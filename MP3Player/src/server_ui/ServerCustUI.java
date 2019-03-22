package server_ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import database.CustDatabase;
import domain.CustBase;
import domain.Customer;
import server_net.ServerCustNet;

public class ServerCustUI {
	// ʵ�ַ������û������ڵĵ�ʵ��
	private static ServerCustUI servercustUI;
	// ��ǰѡ�е��û�
	private Customer cust_selected;
	// ��ǰѡ�е��û����б��е����
	private int cust_index;
	// ����˵�������
	private JFrame frame;
	// �û��б��а�����Ĭ��ģ��
	private static DefaultListModel<String> listModel;

	// ������
	private ServerCustUI(ServerCustNet cust_connect) {
		ui_init(cust_connect);
	}

	// ����������ʱֻ�ܵ��ô˷���
	public static ServerCustUI getServerCustUI(ServerCustNet cust_connect) {
		if (servercustUI == null) {
			servercustUI = new ServerCustUI(cust_connect);
		}
		servercustUI.frame.setVisible(true);
		return servercustUI;
	}

	// �����ʼ��
	private void ui_init(ServerCustNet cust_connect) {
		// ���Ľ���UI���
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		// �������������
		frame = new JFrame("Customer Management");
		frame.setLayout(new GridLayout(1, 2));

		// 1
		// ���ô�������û��б���ʾ����
		listModel = new DefaultListModel<String>();
		JList<String> customerList = new JList<String>(listModel);
		customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		customerList.setBorder(BorderFactory.createEtchedBorder());
		customerList.setFont(new Font("������κ", Font.BOLD, 14));

		fresh_custList();

		// �û��б�����Ϊ��������
		JScrollPane custPanel = new JScrollPane(customerList);
		custPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

		// 2
		// �û���Ϣ��ʾ����������
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2, 1));

		// 2.1
		// �û���Ϣ��ʾ����
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new GridLayout(6, 2));
		displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel number1 = new JLabel("��ǰ����������"), number2 = new JLabel(), username1 = new JLabel("�û�����"),
				password1 = new JLabel("���룺"), log_status1 = new JLabel("��¼״̬��"),
				time_register1 = new JLabel("�˻�����ʱ�䣺"), time1 = new JLabel("�ϴε�½ʱ�䣺"), username2 = new JLabel(),
				password2 = new JLabel(), log_status2 = new JLabel(), time_register2 = new JLabel(),
				time2 = new JLabel();

		// ����һ���߳�
		// ʵ��ÿ��ˢ�µ�ǰ��������
		// ͬʱʵ��ÿ��ˢ����Ϣ��ʾ�����û��б�
		new Thread(new Runnable() {
			@Override
			public void run() {
				int custs_num = 0, sub_custs_num = 0;
				while (true) {
					number2.setText("" + fresh_custNum());
					if (sub_custs_num != custs_num) {
						fresh_custList();
					}
					custs_num = CustBase.getCustBase().getCusts().size();
					if (cust_selected != null) {
						username2.setText(cust_selected.getName());
						password2.setText(cust_selected.getPassword());
						log_info(log_status2, cust_selected.getLogin());
						time_register2.setText(cust_selected.getRegister_time());
						time2.setText(cust_selected.getLastLogin());
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						System.err.println("Number Freshing Error!!");
						return;
					}
					sub_custs_num = CustBase.getCustBase().getCusts().size();
				}
			}
		}).start();

		number1.setHorizontalAlignment(JLabel.CENTER);
		number2.setHorizontalAlignment(JLabel.CENTER);
		username1.setHorizontalAlignment(JLabel.CENTER);
		username2.setHorizontalAlignment(JLabel.CENTER);
		password1.setHorizontalAlignment(JLabel.CENTER);
		password2.setHorizontalAlignment(JLabel.CENTER);
		log_status1.setHorizontalAlignment(JLabel.CENTER);
		log_status2.setHorizontalAlignment(JLabel.CENTER);
		time_register1.setHorizontalAlignment(JLabel.CENTER);
		time_register2.setHorizontalAlignment(JLabel.CENTER);
		time1.setHorizontalAlignment(JLabel.CENTER);
		time2.setHorizontalAlignment(JLabel.CENTER);

		number2.setFont(new Font("������κ", Font.BOLD, 14));
		username2.setFont(new Font("������κ", Font.BOLD, 14));
		password2.setFont(new Font("������κ", Font.BOLD, 14));
		log_status2.setFont(new Font("������κ", Font.BOLD, 14));
		time_register2.setFont(new Font("������κ", Font.BOLD, 14));
		time2.setFont(new Font("������κ", Font.BOLD, 14));

		displayPanel.add(number1);
		displayPanel.add(number2);
		displayPanel.add(username1);
		displayPanel.add(username2);
		displayPanel.add(password1);
		displayPanel.add(password2);
		displayPanel.add(log_status1);
		displayPanel.add(log_status2);
		displayPanel.add(time_register1);
		displayPanel.add(time_register2);
		displayPanel.add(time1);
		displayPanel.add(time2);

		// ѡ��ĳһ�û�����ʾ�û���Ϣ
		customerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				cust_index = customerList.getSelectedIndex();
				if (cust_index != -1) {
					cust_selected = CustBase.getCustBase().getCusts().get(cust_index);
					username2.setText(cust_selected.getName());
					password2.setText(cust_selected.getPassword());
					log_info(log_status2, cust_selected.getLogin());
					time_register2.setText(cust_selected.getRegister_time());
					time2.setText(cust_selected.getLastLogin());
				}
			}
		});

		// 2.2
		// �û�������ز�������
		JPanel opPanel = new JPanel();
		opPanel.setLayout(new GridLayout(2, 2, 30, 30));
		opPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

		JButton add = new JButton("����û�"), delete = new JButton("ɾ����ǰ�û�"), modify = new JButton("�޸ĵ�ǰ�˻���Ϣ"),
				exit = new JButton("�˳�");

		delete.setForeground(Color.RED);
		exit.setForeground(Color.RED);

		// Ϊ����˻���ť��Ӽ�����
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String username, password, sub_password;

				// �û������û���
				while (true) {
					username = JOptionPane.showInputDialog("��������Ҫע����û�����������Ϊ�գ�");
					// �����Դ�ȡ����ť
					if (username == null) {
						return;
					}
					// ȷ���û����Ƿ��Ѵ���
					if (CustBase.getCustBase().findCust(username) != null) {
						JOptionPane.showMessageDialog(null, "���û����Ѵ���", "Error", JOptionPane.ERROR_MESSAGE);
						continue;
					}
					// �û�������Ϊ��
					if (!username.equals("")) {
						break;
					} else {
						JOptionPane.showMessageDialog(null, "�û�������Ϊ��", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				// �û���������
				while (true) {
					password = JOptionPane.showInputDialog("�������������룺������Ϊ�գ�");
					// �����Դ�ȡ����ť
					if (password == null) {
						return;
					}
					// ���벻��Ϊ��
					if (!password.equals("")) {
						break;
					} else {
						JOptionPane.showMessageDialog(null, "���벻��Ϊ��", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				// �û��ٶ�ȷ������
				while (true) {
					sub_password = JOptionPane.showInputDialog("���������������룺������Ϊ�գ�");
					// �����Դ�ȡ����ť
					if (sub_password == null) {
						return;
					}
					// �ٴ���������
					if (sub_password.equals(password)) {
						break;
					} else {
						JOptionPane.showMessageDialog(null, "�������β���ͬ", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				Customer cust = new Customer(username, sub_password);
				// ����ע��ʱ��
				cust.setRegister_time(ServerCustNet.getTime());
				// д�����ݿ���
				try {
					CustDatabase.addToDatabase(cust);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "�˻�����ʧ��", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				CustBase.getCustBase().addCust(cust);
				// �����û��б�
				fresh_custList();
				username2.setText("");
				password2.setText("");
				log_status2.setText("");
				time_register2.setText("");
				time2.setText("");
				cust_selected = null;
			}
		});

		// Ϊɾ���˻���ť��Ӽ�����
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cust_selected == null) {
					JOptionPane.showMessageDialog(null, "δѡ���κ��û�", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// �����ݿ���ɾ���û�
				try {
					CustDatabase.deleteToDatabase(cust_selected);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "�û�ɾ��ʧ��", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				CustBase.getCustBase().getCusts().remove(cust_selected);
				// �����û��б�
				fresh_custList();
				username2.setText("");
				password2.setText("");
				log_status2.setText("");
				time_register2.setText("");
				time2.setText("");
				cust_selected = null;
			}
		});

		// Ϊ�޸��˻���ť��Ӽ�����
		modify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cust_selected == null) {
					JOptionPane.showMessageDialog(null, "δѡ���κ��û�", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				String[] options = { "�޸��û���", "�޸�����" };
				int index, counter = 3;
				String username;
				String oldpassword, password, sub_password;
				// ��¼����δ�޸ĵ��û���
				// ���������ݿ����
				String oldName = cust_selected.getName();

				// ȷ����Ҫ�޸ĵ���Ϣ����
				while (true) {
					index = JOptionPane.showOptionDialog(null, "ѡ��Ҫ�޸ĵ���Ϣ", "��Ϣ�޸�", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (index == 0 || index == 1) {
						break;
					} else {
						return;
					}
				}

				// �޸��û���
				if (index == 0) {
					while (true) {
						username = JOptionPane.showInputDialog("�µ��û�����������Ϊ�գ�");
						// �����Դ�ȡ����ť
						if (username == null) {
							return;
						}
						// ȷ���û����Ƿ��Ѵ���
						if (CustBase.getCustBase().findCust(username) != null) {
							JOptionPane.showMessageDialog(null, "���û����Ѵ���", "Error", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						// �û�������Ϊ��
						if (!username.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "�û�������Ϊ��", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					cust_selected.setName(username);
					// �޸��û�����
				} else if (index == 1) {
					// ��֤������
					while (true) {
						oldpassword = JOptionPane.showInputDialog("����������룺");
						// �����Դ�ȡ����ť
						if (oldpassword == null) {
							return;
						}
						// ��֤������
						if (oldpassword.equals(cust_selected.getPassword())) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "�������, ����" + --counter + "�λ���", "Error",
									JOptionPane.ERROR_MESSAGE);
							if (counter == 0) {
								return;
							}
						}
					}
					// �û�����������
					while (true) {
						password = JOptionPane.showInputDialog("�����������룺������Ϊ�գ�");
						// �����Դ�ȡ����ť
						if (password == null) {
							return;
						}
						// ���벻��Ϊ��
						if (!password.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "���벻��Ϊ��", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}

					// �û��ٶ�ȷ������
					while (true) {
						sub_password = JOptionPane.showInputDialog("�������������룺������Ϊ�գ�");
						// �����Դ�ȡ����ť
						if (sub_password == null) {
							return;
						}
						// �ٴ���������
						if (sub_password.equals(password)) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "�������β���ͬ", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					cust_selected.setPassword(sub_password);
				}
				// ����Ϣ�޸�д�����ݿ���
				try {
					CustDatabase.updateToDatabase(oldName, cust_selected, index);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "��Ϣ�޸�ʧ��", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// ˢ���û��б�
				fresh_custList();
				username2.setText(cust_selected.getName());
				password2.setText(cust_selected.getPassword());
				cust_selected = null;
			}
		});

		// Ϊ�˳���ť��Ӽ�����
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});

		opPanel.add(add);
		opPanel.add(delete);
		opPanel.add(modify);
		opPanel.add(exit);

		infoPanel.add(displayPanel);
		infoPanel.add(opPanel);

		frame.add(custPanel);
		frame.add(infoPanel);

		// ������ҳ���������
		// ����ͼ��
		ImageIcon APPIcon = new ImageIcon("./res/image/app_icon.png");
		frame.setIconImage(APPIcon.getImage());
		// �����ս�
		// frame.pack();
		frame.setSize(new Dimension(700, 500));
		// ���ھ���
		frame.setLocationRelativeTo(null);
		// ���ڹرպ󲻿ɼ�
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// ���ڲ��ɸ���
		frame.setResizable(false);
		// ���ڿɼ�
		frame.setVisible(true);
	}

	// ˢ���û��б�
	public static void fresh_custList() {
		listModel.removeAllElements();
		for (Customer cust : CustBase.getCustBase().getCusts()) {
			listModel.addElement(cust.getName());
		}
	}

	// �û���½״̬
	// ����Ϊ��ɫ ����Ϊ��ɫ
	private void log_info(JLabel jtf, boolean log_status) {
		if (log_status) {
			jtf.setText("OnLine");
			jtf.setForeground(new Color(0, 204, 51));
		} else {
			jtf.setText("OFFLine");
			jtf.setForeground(Color.RED);
		}
	}

	// ȷ�ϵ�ǰ�û���������
	// ����ѭ��ʵ�ֱ���
	// ������������û�����ʮ��ʱ�����ܽ�������
	// ����ʱ��һ������1 - 2s֮��
	// �˴�û���Ż������㷨
	// Ҳû�и�Ϊ����ʵ�ַ���
	private int fresh_custNum() {
		int number = 0;
		// long pt1 = System.currentTimeMillis();
		for (Customer cust : CustBase.getCustBase().getCusts()) {
			if (cust.getLogin()) {
				number++;
			}
		}
		// long pt2 = System.currentTimeMillis();
		// System.out.println("<" + (pt2 - pt1) + ">");
		return number;
	}
}

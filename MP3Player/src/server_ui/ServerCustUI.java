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
	// 实现服务器用户管理窗口的单实例
	private static ServerCustUI servercustUI;
	// 当前选中的用户
	private Customer cust_selected;
	// 当前选中的用户在列表中的序号
	private int cust_index;
	// 管理端的主窗口
	private JFrame frame;
	// 用户列表中包含的默认模型
	private static DefaultListModel<String> listModel;

	// 构造器
	private ServerCustUI(ServerCustNet cust_connect) {
		ui_init(cust_connect);
	}

	// 创建管理窗口时只能调用此方法
	public static ServerCustUI getServerCustUI(ServerCustNet cust_connect) {
		if (servercustUI == null) {
			servercustUI = new ServerCustUI(cust_connect);
		}
		servercustUI.frame.setVisible(true);
		return servercustUI;
	}

	// 界面初始化
	private void ui_init(ServerCustNet cust_connect) {
		// 更改界面UI风格
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

		// 管理界面主窗口
		frame = new JFrame("Customer Management");
		frame.setLayout(new GridLayout(1, 2));

		// 1
		// 设置窗口左边用户列表显示部分
		listModel = new DefaultListModel<String>();
		JList<String> customerList = new JList<String>(listModel);
		customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		customerList.setBorder(BorderFactory.createEtchedBorder());
		customerList.setFont(new Font("华文新魏", Font.BOLD, 14));

		fresh_custList();

		// 用户列表设置为滚动窗口
		JScrollPane custPanel = new JScrollPane(customerList);
		custPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

		// 2
		// 用户信息显示及操作部分
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2, 1));

		// 2.1
		// 用户信息显示部分
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new GridLayout(6, 2));
		displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel number1 = new JLabel("当前在线人数："), number2 = new JLabel(), username1 = new JLabel("用户名："),
				password1 = new JLabel("密码："), log_status1 = new JLabel("登录状态："),
				time_register1 = new JLabel("账户创建时间："), time1 = new JLabel("上次登陆时间："), username2 = new JLabel(),
				password2 = new JLabel(), log_status2 = new JLabel(), time_register2 = new JLabel(),
				time2 = new JLabel();

		// 增加一个线程
		// 实现每秒刷新当前在线人数
		// 同时实现每秒刷新信息显示面板和用户列表
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

		number2.setFont(new Font("华文新魏", Font.BOLD, 14));
		username2.setFont(new Font("华文新魏", Font.BOLD, 14));
		password2.setFont(new Font("华文新魏", Font.BOLD, 14));
		log_status2.setFont(new Font("华文新魏", Font.BOLD, 14));
		time_register2.setFont(new Font("华文新魏", Font.BOLD, 14));
		time2.setFont(new Font("华文新魏", Font.BOLD, 14));

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

		// 选定某一用户后显示用户信息
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
		// 用户管理相关操作部分
		JPanel opPanel = new JPanel();
		opPanel.setLayout(new GridLayout(2, 2, 30, 30));
		opPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

		JButton add = new JButton("添加用户"), delete = new JButton("删除当前用户"), modify = new JButton("修改当前账户信息"),
				exit = new JButton("退出");

		delete.setForeground(Color.RED);
		exit.setForeground(Color.RED);

		// 为添加账户按钮添加监听器
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String username, password, sub_password;

				// 用户输入用户名
				while (true) {
					username = JOptionPane.showInputDialog("请输入您要注册的用户名：（不能为空）");
					// 窗口自带取消按钮
					if (username == null) {
						return;
					}
					// 确认用户名是否已存在
					if (CustBase.getCustBase().findCust(username) != null) {
						JOptionPane.showMessageDialog(null, "该用户名已存在", "Error", JOptionPane.ERROR_MESSAGE);
						continue;
					}
					// 用户名不能为空
					if (!username.equals("")) {
						break;
					} else {
						JOptionPane.showMessageDialog(null, "用户名不能为空", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				// 用户输入密码
				while (true) {
					password = JOptionPane.showInputDialog("请输入您的密码：（不能为空）");
					// 窗口自带取消按钮
					if (password == null) {
						return;
					}
					// 密码不能为空
					if (!password.equals("")) {
						break;
					} else {
						JOptionPane.showMessageDialog(null, "密码不能为空", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				// 用户再度确认密码
				while (true) {
					sub_password = JOptionPane.showInputDialog("请再输入您的密码：（不能为空）");
					// 窗口自带取消按钮
					if (sub_password == null) {
						return;
					}
					// 再次输入密码
					if (sub_password.equals(password)) {
						break;
					} else {
						JOptionPane.showMessageDialog(null, "密码两次不相同", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

				Customer cust = new Customer(username, sub_password);
				// 设置注册时间
				cust.setRegister_time(ServerCustNet.getTime());
				// 写入数据库中
				try {
					CustDatabase.addToDatabase(cust);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "账户创建失败", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				CustBase.getCustBase().addCust(cust);
				// 更新用户列表
				fresh_custList();
				username2.setText("");
				password2.setText("");
				log_status2.setText("");
				time_register2.setText("");
				time2.setText("");
				cust_selected = null;
			}
		});

		// 为删除账户按钮添加监听器
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cust_selected == null) {
					JOptionPane.showMessageDialog(null, "未选中任何用户", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// 从数据库中删除用户
				try {
					CustDatabase.deleteToDatabase(cust_selected);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "用户删除失败", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				CustBase.getCustBase().getCusts().remove(cust_selected);
				// 更新用户列表
				fresh_custList();
				username2.setText("");
				password2.setText("");
				log_status2.setText("");
				time_register2.setText("");
				time2.setText("");
				cust_selected = null;
			}
		});

		// 为修改账户按钮添加监听器
		modify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cust_selected == null) {
					JOptionPane.showMessageDialog(null, "未选择任何用户", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				String[] options = { "修改用户名", "修改密码" };
				int index, counter = 3;
				String username;
				String oldpassword, password, sub_password;
				// 记录下尚未修改的用户名
				// 用以做数据库检索
				String oldName = cust_selected.getName();

				// 确认需要修改的信息类型
				while (true) {
					index = JOptionPane.showOptionDialog(null, "选择要修改的信息", "信息修改", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (index == 0 || index == 1) {
						break;
					} else {
						return;
					}
				}

				// 修改用户名
				if (index == 0) {
					while (true) {
						username = JOptionPane.showInputDialog("新的用户名：（不能为空）");
						// 窗口自带取消按钮
						if (username == null) {
							return;
						}
						// 确认用户名是否已存在
						if (CustBase.getCustBase().findCust(username) != null) {
							JOptionPane.showMessageDialog(null, "该用户名已存在", "Error", JOptionPane.ERROR_MESSAGE);
							continue;
						}
						// 用户名不能为空
						if (!username.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "用户名不能为空", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					cust_selected.setName(username);
					// 修改用户密码
				} else if (index == 1) {
					// 验证旧密码
					while (true) {
						oldpassword = JOptionPane.showInputDialog("请输入旧密码：");
						// 窗口自带取消按钮
						if (oldpassword == null) {
							return;
						}
						// 验证旧密码
						if (oldpassword.equals(cust_selected.getPassword())) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "密码错误, 还有" + --counter + "次机会", "Error",
									JOptionPane.ERROR_MESSAGE);
							if (counter == 0) {
								return;
							}
						}
					}
					// 用户输入新密码
					while (true) {
						password = JOptionPane.showInputDialog("请输入新密码：（不能为空）");
						// 窗口自带取消按钮
						if (password == null) {
							return;
						}
						// 密码不能为空
						if (!password.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "密码不能为空", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}

					// 用户再度确认密码
					while (true) {
						sub_password = JOptionPane.showInputDialog("请再输入新密码：（不能为空）");
						// 窗口自带取消按钮
						if (sub_password == null) {
							return;
						}
						// 再次输入密码
						if (sub_password.equals(password)) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "密码两次不相同", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					cust_selected.setPassword(sub_password);
				}
				// 将信息修改写入数据库中
				try {
					CustDatabase.updateToDatabase(oldName, cust_selected, index);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "信息修改失败", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// 刷新用户列表
				fresh_custList();
				username2.setText(cust_selected.getName());
				password2.setText(cust_selected.getPassword());
				cust_selected = null;
			}
		});

		// 为退出按钮添加监听器
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

		// 管理主页面基础设置
		// 窗口图标
		ImageIcon APPIcon = new ImageIcon("./res/image/app_icon.png");
		frame.setIconImage(APPIcon.getImage());
		// 窗口收紧
		// frame.pack();
		frame.setSize(new Dimension(700, 500));
		// 窗口居中
		frame.setLocationRelativeTo(null);
		// 窗口关闭后不可见
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// 窗口不可更改
		frame.setResizable(false);
		// 窗口可见
		frame.setVisible(true);
	}

	// 刷新用户列表
	public static void fresh_custList() {
		listModel.removeAllElements();
		for (Customer cust : CustBase.getCustBase().getCusts()) {
			listModel.addElement(cust.getName());
		}
	}

	// 用户登陆状态
	// 在线为绿色 离线为红色
	private void log_info(JLabel jtf, boolean log_status) {
		if (log_status) {
			jtf.setText("OnLine");
			jtf.setForeground(new Color(0, 204, 51));
		} else {
			jtf.setText("OFFLine");
			jtf.setForeground(Color.RED);
		}
	}

	// 确认当前用户在线人数
	// 暴力循环实现遍历
	// 这种情况下在用户数五十万时依旧能健康运行
	// 遍历时间一次是在1 - 2s之间
	// 此处没有优化遍历算法
	// 也没有改为其他实现方法
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

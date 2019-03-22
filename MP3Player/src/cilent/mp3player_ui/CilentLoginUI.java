package cilent.mp3player_ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import cilent.net.CilentNet;
import domain.Customer;

public class CilentLoginUI {
	// 客户网络通路
	private CilentNet connect;
	// 父窗口
	private JFrame superFrame;

	public CilentLoginUI(JFrame frame) {
		superFrame = frame;
		net_connect();
		ui_init();
	}

	public void ui_init() {
		// 登陆主页面窗口
		JDialog login = new JDialog(superFrame, "Login", true);
		// 关闭登陆窗口时返回主界面
		login.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				try {
					if(connect.getCustNet().getSocket() != null &&
							connect.getMP3Net().getSocket() != null) {
						connect.getCustNet().getSocket().close();
						connect.getMP3Net().getSocket().close();
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "退出失败", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				login.dispose();
			}
		});
		// 设置登陆窗口的背景图片
		ImageIcon picture = new ImageIcon("./res/image/image1.jpg");

		// 用户名和密码区域
		// 重写paintComponent方法设置背景
		JPanel panel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(picture.getImage(), 0, 0, null);
			}
		};

		JPanel[] sub_panel = new JPanel[3];
		for (int i = 0; i < sub_panel.length; i++) {
			sub_panel[i] = new JPanel();
			sub_panel[i].setOpaque(false);
		}

		// 实现三行的布局
		// 用户名：
		// 密码：
		// 登陆 注册
		panel.setLayout(new GridLayout(3, 1));
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
				BorderFactory.createEmptyBorder(50, 50, 100, 50)));
		JLabel username = new JLabel("用户名");
		JLabel password = new JLabel("密码   ");
		JTextField username_input = new JTextField(18);
		JPasswordField password_input = new JPasswordField(18);
		JButton login_button = new JButton("登陆");
		JButton register_button = new JButton("注册");

		// 第一行
		// 用户名：
		username.setHorizontalAlignment(JTextField.CENTER);
		username.setBackground(Color.WHITE);
		username.setFont(new Font("华文新魏", Font.BOLD, 15));

		username_input.setFont(new Font("华文新魏", Font.BOLD, 15));
		username_input.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		sub_panel[0].add(username);
		sub_panel[0].add(username_input);
		panel.add(sub_panel[0]);

		// 第二行
		// 密码：
		password.setHorizontalAlignment(JTextField.CENTER);
		password.setBackground(Color.RED);
		password.setFont(new Font("华文新魏", Font.BOLD, 15));

		password_input.setFont(new Font("华文新魏", Font.BOLD, 15));
		password_input.setEchoChar('*');
		password_input.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		sub_panel[1].add(password);
		sub_panel[1].add(password_input);
		panel.add(sub_panel[1]);

		// 第三行
		// 登陆 注册
		// 实现
		login_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String username = username_input.getText();
				String password = new String(password_input.getPassword());

				if (connect.getCustNet().getSocket() != null) {
					connect.getCustNet().login_cust(username, password);
					Customer logined = connect.getCustNet().login_back();
					if (logined != null) {
						login.setVisible(false);
						DownloadListUI.getContainer().setComponentAt(0, new DownloadListUI(logined, connect));
					} else {
						JOptionPane.showMessageDialog(null, "用户名或密码错误\n或该用户已登录！！", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// 添加Enter为登陆的快捷键
		// 当焦点在密码输入行时
		// 按下Enter键以实现选中效果
		password_input.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				if (connect.getCustNet().getSocket() != null) {
					if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
						String username = username_input.getText();
						String password = new String(password_input.getPassword());

						connect.getCustNet().login_cust(username, password);
						Customer logined = connect.getCustNet().login_back();
						if (logined != null) {
							login.setVisible(false);
							DownloadListUI.getContainer().setComponentAt(0, new DownloadListUI(logined, connect));
						} else {
							JOptionPane.showMessageDialog(null, "用户名或密码错误\n或者该用户已登录！！", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// 注册按钮的监听器
		register_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				// 检测是否以连上服务器
				if (connect.getCustNet().getSocket() != null) {
					String username, password1, password2;

					// 用户输入用户名
					while (true) {
						username = JOptionPane.showInputDialog("请输入您要注册的用户名：（不能为空）");
						if (username == null) {
							return;
						}
						if (!username.equals("")) {
							// 连接到服务器端实现检验用户名是否重复
							connect.getCustNet().register_cust(username, "");
							if (!connect.getCustNet().register_back()) {
								JOptionPane.showMessageDialog(null, "Customer: " + username + "\r\n" + "用户名已存在！！",
										"Error", JOptionPane.ERROR_MESSAGE);
							} else {
								break;
							}
						}
					}

					// 用户输入密码
					while (true) {
						password1 = JOptionPane.showInputDialog("请输入您的密码：（不能为空）");
						// 窗口自带取消按钮
						if (password1 == null) {
							return;
						}
						// 密码不能为空
						if (!password1.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "密码不能为空", "Error", JOptionPane.ERROR_MESSAGE);
							continue;
						}
					}

					// 用户再度确认密码
					while (true) {
						password2 = JOptionPane.showInputDialog("请再输入您的密码：（不能为空）");
						// 窗口自带取消按钮
						if (password2 == null) {
							return;
						}
						// 密码不能为空
						if (!password2.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "密码不能为空", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					// 连接到服务器端实现注册
					connect.getCustNet().register_cust(username, password2);

					// 弹窗提示
					if (connect.getCustNet().register_back()) {
						JOptionPane.showMessageDialog(null, "Customer: " + username + "\r\n" + "账号建立成功！！\n请记住自己的密码！！");
					} else {
						JOptionPane.showMessageDialog(null, "Customer: " + username + "\r\n" + "账号建立失败！！请重试！！", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		sub_panel[2].add(login_button);
		sub_panel[2].add(register_button);
		panel.add(sub_panel[2]);

		// 副容器放入主容器
		login.add(panel, BorderLayout.CENTER);

		// 登陆主页面基本设置
		// 窗口图标
		ImageIcon AppIcon = new ImageIcon("./res/image/app_icon.png");
		login.setIconImage(AppIcon.getImage());
		// 设置窗口的最小值
		login.setMinimumSize(new Dimension(picture.getIconWidth(), picture.getIconHeight()));
		// 收紧窗口
		login.pack();
		// 窗口居中
		login.setLocationRelativeTo(null);
		// 设置窗口关闭效果
		login.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// 设置窗口不可缩放
		login.setResizable(false);
		// 设置窗口可见
		login.setVisible(true);
	}

	// 登陆时的网络初始化
	public void net_connect() {
		String address = "127.0.0.1";
		int port_cust = 30000;
		int port_mp3 = 31000;
		// 建立登陆服务器的网络
		this.connect = new CilentNet(address, port_cust, port_mp3);
	}
}

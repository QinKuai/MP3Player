package cilent.mp3player_ui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cilent.net.CilentNet;
import domain.Customer;

public class CilentInfoUI {
	// 客户网络通路
	private CilentNet connect;
	// 父窗口
	private JFrame superFrame;
	// 用户对象
	private Customer cust;

	// 用户信息窗口构造器
	public CilentInfoUI(CilentNet connect, Customer cust, JFrame frame) {
		this.connect = connect;
		this.cust = cust;
		this.superFrame = frame;
		// 界面初始化
		ui_init();
	}

	// 初始化方法
	private void ui_init() {
		// 信息页面主窗口
		JDialog info = new JDialog(superFrame, "Infomation", true);
		// 关闭信息窗口时返回主界面
		info.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				info.dispose();
			}
		});

		JPanel panel = new JPanel();
		panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		panel.setLayout(new GridLayout(3, 1));

		// 第一行用于显示用户名
		JPanel firstLine = new JPanel();
		JLabel username_label = new JLabel("用户名");
		JTextField username = new JTextField(15);
		username.setFont(new Font("华文新魏", Font.BOLD, 15));
		username.setText(cust.getName());
		username.setEditable(false);
		JButton name_modify = new JButton("修改用户名");
		// 修改用户名
		name_modify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String oldName, newName;
				if (connect.getCustNet().getSocket() != null) {
					oldName = cust.getName();
					// 用户输入新用户名
					while (true) {
						newName = JOptionPane.showInputDialog("新用户名：（不能为空）");
						if (newName == null) {
							return;
						}
						if (!newName.equals("")) {
							// 连接到服务器端实现检验用户名是否重复
							connect.getCustNet().register_cust(newName, "");
							if (!connect.getCustNet().register_back()) {
								JOptionPane.showMessageDialog(null, "Customer: " + newName + "\r\n" + "用户名已存在！！",
										"Error", JOptionPane.ERROR_MESSAGE);
							} else {
								break;
							}
						}
					}

					// 连接服务器实现修改
					connect.getCustNet().modify_cust(newName, cust.getPassword(), oldName, 0);

					if (connect.getCustNet().modify_back() != null) {
						cust.setName(newName);
						username.setText(newName);
						JOptionPane.showMessageDialog(null, "Customer: " + newName + "\r\n" + "用户名修改成功！！！！");
					} else {
						JOptionPane.showMessageDialog(null, "Customer: " + newName + "\r\n" + "用户名修改失败！！请重试！！", "Error",
								JOptionPane.ERROR_MESSAGE);
					}

				} else {
					JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		firstLine.add(username_label);
		firstLine.add(username);
		firstLine.add(name_modify);
		firstLine.setAlignmentX(JPanel.LEFT_ALIGNMENT);

		// 第二行用于显示用户密码
		JPanel secondLine = new JPanel();
		JLabel password_label = new JLabel("密码");
		JTextField password = new JTextField(15);
		password.setFont(new Font("华文新魏", Font.BOLD, 15));
		password.setText(cust.getPassword());
		password.setEditable(false);
		JButton password_modify = new JButton("修改密码");
		// 修改用户密码
		password_modify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String oldpassword, password1, password2;
				// 允许密码错误输入的次数
				int counter = 3;
				if (connect.getCustNet().getSocket() != null) {
					// 验证旧密码
					while (true) {
						oldpassword = JOptionPane.showInputDialog("请输入旧密码：");
						// 窗口自带取消按钮
						if (oldpassword == null) {
							return;
						}
						// 验证旧密码
						if (oldpassword.equals(cust.getPassword())) {
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
						password1 = JOptionPane.showInputDialog("请输入新密码：（不能为空）");
						// 窗口自带取消按钮
						if (password1 == null) {
							return;
						}
						// 密码不能为空
						if (!password1.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "密码不能为空", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}

					// 用户再度确认密码
					while (true) {
						password2 = JOptionPane.showInputDialog("请再输入新密码：（不能为空）");
						// 窗口自带取消按钮
						if (password2 == null) {
							return;
						}
						// 再次输入密码
						if (password2.equals(password1)) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "密码两次不相同", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}

					// 连接服务器实现修改
					connect.getCustNet().modify_cust(cust.getName(), password2, null, 5);

					if (connect.getCustNet().modify_back() != null) {
						cust.setPassword(password2);
						password.setText(password2);
						JOptionPane.showMessageDialog(null, "Customer: " + cust.getName() + "\r\n" + "密码修改成功！！！！");
					} else {
						JOptionPane.showMessageDialog(null, "Customer: " + cust.getName() + "\r\n" + "密码修改失败！！请重试！！",
								"Error", JOptionPane.ERROR_MESSAGE);
					}

				} else {
					JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		secondLine.add(password_label);
		secondLine.add(password);
		secondLine.add(password_modify);
		secondLine.setAlignmentX(JPanel.LEFT_ALIGNMENT);

		// 第三行用于退出
		JPanel thirdLine = new JPanel();
		JButton exit = new JButton("返回");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				info.dispose();
			}
		});
		thirdLine.add(exit);

		panel.add(firstLine);
		panel.add(secondLine);
		panel.add(thirdLine);
		info.add(panel);
		// 信息主页面基本设置
		// 窗口图标
		ImageIcon AppIcon = new ImageIcon("./res/image/app_icon.png");
		info.setIconImage(AppIcon.getImage());
		// 收紧窗口
		info.pack();
		// 窗口居中
		info.setLocationRelativeTo(null);
		// 设置窗口关闭效果
		info.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// 设置窗口不可缩放
		info.setResizable(false);
		// 设置窗口可见
		info.setVisible(true);
	}
}

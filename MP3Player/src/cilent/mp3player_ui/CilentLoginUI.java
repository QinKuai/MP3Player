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
	// �ͻ�����ͨ·
	private CilentNet connect;
	// ������
	private JFrame superFrame;

	public CilentLoginUI(JFrame frame) {
		superFrame = frame;
		net_connect();
		ui_init();
	}

	public void ui_init() {
		// ��½��ҳ�洰��
		JDialog login = new JDialog(superFrame, "Login", true);
		// �رյ�½����ʱ����������
		login.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				try {
					if(connect.getCustNet().getSocket() != null &&
							connect.getMP3Net().getSocket() != null) {
						connect.getCustNet().getSocket().close();
						connect.getMP3Net().getSocket().close();
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "�˳�ʧ��", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				login.dispose();
			}
		});
		// ���õ�½���ڵı���ͼƬ
		ImageIcon picture = new ImageIcon("./res/image/image1.jpg");

		// �û�������������
		// ��дpaintComponent�������ñ���
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

		// ʵ�����еĲ���
		// �û�����
		// ���룺
		// ��½ ע��
		panel.setLayout(new GridLayout(3, 1));
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
				BorderFactory.createEmptyBorder(50, 50, 100, 50)));
		JLabel username = new JLabel("�û���");
		JLabel password = new JLabel("����   ");
		JTextField username_input = new JTextField(18);
		JPasswordField password_input = new JPasswordField(18);
		JButton login_button = new JButton("��½");
		JButton register_button = new JButton("ע��");

		// ��һ��
		// �û�����
		username.setHorizontalAlignment(JTextField.CENTER);
		username.setBackground(Color.WHITE);
		username.setFont(new Font("������κ", Font.BOLD, 15));

		username_input.setFont(new Font("������κ", Font.BOLD, 15));
		username_input.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		sub_panel[0].add(username);
		sub_panel[0].add(username_input);
		panel.add(sub_panel[0]);

		// �ڶ���
		// ���룺
		password.setHorizontalAlignment(JTextField.CENTER);
		password.setBackground(Color.RED);
		password.setFont(new Font("������κ", Font.BOLD, 15));

		password_input.setFont(new Font("������κ", Font.BOLD, 15));
		password_input.setEchoChar('*');
		password_input.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		sub_panel[1].add(password);
		sub_panel[1].add(password_input);
		panel.add(sub_panel[1]);

		// ������
		// ��½ ע��
		// ʵ��
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
						JOptionPane.showMessageDialog(null, "�û������������\n����û��ѵ�¼����", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// ���EnterΪ��½�Ŀ�ݼ�
		// ������������������ʱ
		// ����Enter����ʵ��ѡ��Ч��
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
							JOptionPane.showMessageDialog(null, "�û������������\n���߸��û��ѵ�¼����", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// ע�ᰴť�ļ�����
		register_button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				// ����Ƿ������Ϸ�����
				if (connect.getCustNet().getSocket() != null) {
					String username, password1, password2;

					// �û������û���
					while (true) {
						username = JOptionPane.showInputDialog("��������Ҫע����û�����������Ϊ�գ�");
						if (username == null) {
							return;
						}
						if (!username.equals("")) {
							// ���ӵ���������ʵ�ּ����û����Ƿ��ظ�
							connect.getCustNet().register_cust(username, "");
							if (!connect.getCustNet().register_back()) {
								JOptionPane.showMessageDialog(null, "Customer: " + username + "\r\n" + "�û����Ѵ��ڣ���",
										"Error", JOptionPane.ERROR_MESSAGE);
							} else {
								break;
							}
						}
					}

					// �û���������
					while (true) {
						password1 = JOptionPane.showInputDialog("�������������룺������Ϊ�գ�");
						// �����Դ�ȡ����ť
						if (password1 == null) {
							return;
						}
						// ���벻��Ϊ��
						if (!password1.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "���벻��Ϊ��", "Error", JOptionPane.ERROR_MESSAGE);
							continue;
						}
					}

					// �û��ٶ�ȷ������
					while (true) {
						password2 = JOptionPane.showInputDialog("���������������룺������Ϊ�գ�");
						// �����Դ�ȡ����ť
						if (password2 == null) {
							return;
						}
						// ���벻��Ϊ��
						if (!password2.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "���벻��Ϊ��", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					// ���ӵ���������ʵ��ע��
					connect.getCustNet().register_cust(username, password2);

					// ������ʾ
					if (connect.getCustNet().register_back()) {
						JOptionPane.showMessageDialog(null, "Customer: " + username + "\r\n" + "�˺Ž����ɹ�����\n���ס�Լ������룡��");
					} else {
						JOptionPane.showMessageDialog(null, "Customer: " + username + "\r\n" + "�˺Ž���ʧ�ܣ��������ԣ���", "Error",
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

		// ����������������
		login.add(panel, BorderLayout.CENTER);

		// ��½��ҳ���������
		// ����ͼ��
		ImageIcon AppIcon = new ImageIcon("./res/image/app_icon.png");
		login.setIconImage(AppIcon.getImage());
		// ���ô��ڵ���Сֵ
		login.setMinimumSize(new Dimension(picture.getIconWidth(), picture.getIconHeight()));
		// �ս�����
		login.pack();
		// ���ھ���
		login.setLocationRelativeTo(null);
		// ���ô��ڹر�Ч��
		login.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// ���ô��ڲ�������
		login.setResizable(false);
		// ���ô��ڿɼ�
		login.setVisible(true);
	}

	// ��½ʱ�������ʼ��
	public void net_connect() {
		String address = "127.0.0.1";
		int port_cust = 30000;
		int port_mp3 = 31000;
		// ������½������������
		this.connect = new CilentNet(address, port_cust, port_mp3);
	}
}

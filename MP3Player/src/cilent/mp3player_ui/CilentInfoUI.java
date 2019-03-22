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
	// �ͻ�����ͨ·
	private CilentNet connect;
	// ������
	private JFrame superFrame;
	// �û�����
	private Customer cust;

	// �û���Ϣ���ڹ�����
	public CilentInfoUI(CilentNet connect, Customer cust, JFrame frame) {
		this.connect = connect;
		this.cust = cust;
		this.superFrame = frame;
		// �����ʼ��
		ui_init();
	}

	// ��ʼ������
	private void ui_init() {
		// ��Ϣҳ��������
		JDialog info = new JDialog(superFrame, "Infomation", true);
		// �ر���Ϣ����ʱ����������
		info.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				info.dispose();
			}
		});

		JPanel panel = new JPanel();
		panel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		panel.setLayout(new GridLayout(3, 1));

		// ��һ��������ʾ�û���
		JPanel firstLine = new JPanel();
		JLabel username_label = new JLabel("�û���");
		JTextField username = new JTextField(15);
		username.setFont(new Font("������κ", Font.BOLD, 15));
		username.setText(cust.getName());
		username.setEditable(false);
		JButton name_modify = new JButton("�޸��û���");
		// �޸��û���
		name_modify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String oldName, newName;
				if (connect.getCustNet().getSocket() != null) {
					oldName = cust.getName();
					// �û��������û���
					while (true) {
						newName = JOptionPane.showInputDialog("���û�����������Ϊ�գ�");
						if (newName == null) {
							return;
						}
						if (!newName.equals("")) {
							// ���ӵ���������ʵ�ּ����û����Ƿ��ظ�
							connect.getCustNet().register_cust(newName, "");
							if (!connect.getCustNet().register_back()) {
								JOptionPane.showMessageDialog(null, "Customer: " + newName + "\r\n" + "�û����Ѵ��ڣ���",
										"Error", JOptionPane.ERROR_MESSAGE);
							} else {
								break;
							}
						}
					}

					// ���ӷ�����ʵ���޸�
					connect.getCustNet().modify_cust(newName, cust.getPassword(), oldName, 0);

					if (connect.getCustNet().modify_back() != null) {
						cust.setName(newName);
						username.setText(newName);
						JOptionPane.showMessageDialog(null, "Customer: " + newName + "\r\n" + "�û����޸ĳɹ���������");
					} else {
						JOptionPane.showMessageDialog(null, "Customer: " + newName + "\r\n" + "�û����޸�ʧ�ܣ��������ԣ���", "Error",
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

		// �ڶ���������ʾ�û�����
		JPanel secondLine = new JPanel();
		JLabel password_label = new JLabel("����");
		JTextField password = new JTextField(15);
		password.setFont(new Font("������κ", Font.BOLD, 15));
		password.setText(cust.getPassword());
		password.setEditable(false);
		JButton password_modify = new JButton("�޸�����");
		// �޸��û�����
		password_modify.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String oldpassword, password1, password2;
				// ���������������Ĵ���
				int counter = 3;
				if (connect.getCustNet().getSocket() != null) {
					// ��֤������
					while (true) {
						oldpassword = JOptionPane.showInputDialog("����������룺");
						// �����Դ�ȡ����ť
						if (oldpassword == null) {
							return;
						}
						// ��֤������
						if (oldpassword.equals(cust.getPassword())) {
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
						password1 = JOptionPane.showInputDialog("�����������룺������Ϊ�գ�");
						// �����Դ�ȡ����ť
						if (password1 == null) {
							return;
						}
						// ���벻��Ϊ��
						if (!password1.equals("")) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "���벻��Ϊ��", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}

					// �û��ٶ�ȷ������
					while (true) {
						password2 = JOptionPane.showInputDialog("�������������룺������Ϊ�գ�");
						// �����Դ�ȡ����ť
						if (password2 == null) {
							return;
						}
						// �ٴ���������
						if (password2.equals(password1)) {
							break;
						} else {
							JOptionPane.showMessageDialog(null, "�������β���ͬ", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}

					// ���ӷ�����ʵ���޸�
					connect.getCustNet().modify_cust(cust.getName(), password2, null, 5);

					if (connect.getCustNet().modify_back() != null) {
						cust.setPassword(password2);
						password.setText(password2);
						JOptionPane.showMessageDialog(null, "Customer: " + cust.getName() + "\r\n" + "�����޸ĳɹ���������");
					} else {
						JOptionPane.showMessageDialog(null, "Customer: " + cust.getName() + "\r\n" + "�����޸�ʧ�ܣ��������ԣ���",
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

		// �����������˳�
		JPanel thirdLine = new JPanel();
		JButton exit = new JButton("����");
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
		// ��Ϣ��ҳ���������
		// ����ͼ��
		ImageIcon AppIcon = new ImageIcon("./res/image/app_icon.png");
		info.setIconImage(AppIcon.getImage());
		// �ս�����
		info.pack();
		// ���ھ���
		info.setLocationRelativeTo(null);
		// ���ô��ڹر�Ч��
		info.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// ���ô��ڲ�������
		info.setResizable(false);
		// ���ô��ڿɼ�
		info.setVisible(true);
	}
}

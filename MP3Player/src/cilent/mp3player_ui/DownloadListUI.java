package cilent.mp3player_ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;

import cilent.net.CilentNet;
import domain.Customer;

public class DownloadListUI extends JComponent {

	private static final long serialVersionUID = 1L;
	// 用户的登陆状态
	private static boolean login_status = false;
	// 主界面
	private static JFrame superFrame;
	// 主界面下载列表
	private static JTabbedPane container;
	// 下载的音乐列表
	private static JList<String> musicList;
	// 下载音乐列表默认模型
	private static DefaultListModel<String> listModel;
	// 音乐控制图标文件存放地
	private String marks_filename = "./res/marks/";

	// 初始用户登陆界面
	public DownloadListUI(JFrame frame, JTabbedPane music) {
		super();
		superFrame = frame;
		container = music;
		setLogin_status(false);
		ui_init();
	}

	// 登陆完成后用户界面
	public DownloadListUI(Customer cust, CilentNet connect) {
		super();
		setLogin_status(true);
		ui_init(cust, connect);
	}

	private void ui_init() {
		setLayout(new BorderLayout());

		// 用户登录前
		JButton login = new JButton("登陆/注册");
		login.setContentAreaFilled(false);
		login.setFocusable(false);

		labelButton(login);

		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new CilentLoginUI(superFrame);
			}
		});

		add(login, BorderLayout.NORTH);
	}

	private void ui_init(Customer cust, CilentNet connect) {
		setLayout(new BorderLayout());

		// 用户登陆后
		JPanel afterLog = new JPanel();
		JButton user = new JButton(cust.getName());
		new Thread(new Runnable() {
			@Override
			public void run() {
				String oldName, newName;
				oldName = newName = cust.getName();
				while (true) {
					if (!oldName.equals(newName)) {
						user.setText(newName);
						oldName = cust.getName();
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						JOptionPane.showMessageDialog(null, "用户名修改失败！！", "Error", JOptionPane.ERROR_MESSAGE);
					}
					newName = cust.getName();
				}
			}
		}).start();
		IconButton fresh = new IconButton(new ImageIcon(marks_filename + "fresh.png"));
		IconButton logout = new IconButton(new ImageIcon(marks_filename + "logout.png"));

		user.setContentAreaFilled(false);
		user.setFocusable(false);
		user.setFont(new Font("华文新魏", Font.PLAIN, 16));
		user.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new CilentInfoUI(connect, cust, superFrame);
			}
		});
		
		fresh.setPreferredSize(new Dimension(15, 15));
		fresh.setToolTipText("刷新");
		fresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.removeAllElements();
				connect.getMP3Net().fresh();
				connect.getMP3Net().receive_list();
			}
		});

		logout.setPreferredSize(new Dimension(15, 15));
		logout.setToolTipText("登出");
		logout.addActionListener(new ActionListener() {
			CilentNet net_logout = connect;

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					//不处在下载状态允许退出
					if(!net_logout.getMP3Net().isDownload()) {
						net_logout.getCustNet().logout_cust(cust.getName(), cust.getPassword());
						if (net_logout.getCustNet().logout_back() != null) {
							JOptionPane.showMessageDialog(null, "您已登出，再见");
							net_logout.getCustNet().getSocket().close();
							net_logout.getMP3Net().getSocket().close();
						} else {
							JOptionPane.showMessageDialog(null, "网络问题，退出失败！！", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}else {
						JOptionPane.showMessageDialog(null, "还有下载文件，退出失败！！", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "网络问题，退出失败！！", "Error", JOptionPane.ERROR_MESSAGE);
				}
				container.setComponentAt(0, new DownloadListUI(superFrame, container));
			}
		});

		DownloadListUI.labelButton(user);
		DownloadListUI.labelButton(fresh);
		DownloadListUI.labelButton(logout);

		afterLog.add(user);
		afterLog.add(fresh);
		afterLog.add(logout);

		// 下载的歌曲列表
		listModel = new DefaultListModel<String>();
		musicList = new JList<String>(listModel);
		musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		musicList.setBorder(BorderFactory.createEtchedBorder());
		// 刷新音乐列表
		connect.getMP3Net().fresh();
		connect.getMP3Net().receive_list();
		
		JScrollPane musicPanel = new JScrollPane(musicList);
		
		//双击下载
		musicList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getClickCount() == 2) {
					if(!connect.getMP3Net().isDownload()) {
						String musicName = listModel.getElementAt(musicList.getSelectedIndex());
						connect.getMP3Net().download(musicName);
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								connect.getMP3Net().download_back(musicName + ".mp3");
							}
						}).start();
					}else {
						JOptionPane.showMessageDialog(null, "只支持单次下载一首音乐", "Message", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
			}
		});
		
		add(afterLog, BorderLayout.NORTH);
		add(musicPanel);
	}

	public static void labelButton(JButton button) {
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setForeground(Color.BLUE);
				button.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setForeground(Color.BLACK);
			}

			@Override
			public void mousePressed(MouseEvent event) {
				button.setForeground(Color.BLACK);
			}
		});
	}

	public static JTabbedPane getContainer() {
		return container;
	}

	public static boolean isLogin_status() {
		return login_status;
	}

	public static void setLogin_status(boolean login_status) {
		DownloadListUI.login_status = login_status;
	}

	public static JList<String> getMusicList() {
		return musicList;
	}
	
	public static DefaultListModel<String> getListModel() {
		return listModel;
	}
}

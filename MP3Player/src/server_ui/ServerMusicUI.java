package server_ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import cilent.mp3player_ui.CilentMainUI;
import database.MP3Database;
import domain.Music;
import domain.MusicBase;
import server_net.ServerMP3Net;

public class ServerMusicUI {
	// 音乐管理端的单实例化
	private static ServerMusicUI serverMusicUI;
	// 当前选中的音乐文件
	private Music music_selected;
	// 当前选中的文件索引
	private int music_index;
	// 管理界面的主窗口
	private JFrame frame;
	// 音乐文件列表的默认列表模型
	private static DefaultListModel<String> listModel;
	// 当前音乐文件选择路径
	private File fileDirectory = new File("C:\\Users\\asus\\Desktop");

	// 构造器
	private ServerMusicUI(ServerMP3Net music_connect) {
		ui_init(music_connect);
	}

	// 创建管理窗口时只能调用此方法
	public static ServerMusicUI getServerMusicUI(ServerMP3Net music_connect) {
		if (serverMusicUI == null) {
			serverMusicUI = new ServerMusicUI(music_connect);
		}
		serverMusicUI.frame.setVisible(true);
		return serverMusicUI;
	}

	// 界面初始化
	private void ui_init(ServerMP3Net music_connect) {
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

		// 音乐管理界面
		frame = new JFrame("Music Management");
		frame.setLayout(new GridLayout(1, 2));

		// 1
		// 设置窗口左边音乐列表显示部分
		listModel = new DefaultListModel<String>();
		JList<String> musicList = new JList<String>(listModel);
		String fontName = musicList.getFont().getFontName();
		musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		musicList.setBorder(BorderFactory.createEtchedBorder());
		musicList.setFont(new Font(fontName, Font.BOLD, 14));

		fresh_musicList();

		// 音乐列表设置为滚动列表
		JScrollPane musicPanel = new JScrollPane(musicList);
		musicPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

		// 2
		// 音乐信息显示及操作部分
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2, 1));

		// 2.1
		// 用户信息显示部分
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new GridLayout(5, 2));
		displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel number1 = new JLabel("音乐数量："), number2 = new JLabel(), musicName1 = new JLabel("曲名_作者："),
				musicName2 = new JLabel(), directory1 = new JLabel("存储路径："), directory2 = new JLabel(),
				header1 = new JLabel("头信息："), header2 = new JLabel(), storage1 = new JLabel("文件大小"),
				storage2 = new JLabel();

		number1.setHorizontalAlignment(JLabel.CENTER);
		number2.setHorizontalAlignment(JLabel.CENTER);
		musicName1.setHorizontalAlignment(JLabel.CENTER);
		musicName2.setHorizontalAlignment(JLabel.CENTER);
		directory1.setHorizontalAlignment(JLabel.CENTER);
		directory2.setHorizontalAlignment(JLabel.CENTER);
		header1.setHorizontalAlignment(JLabel.CENTER);
		header2.setHorizontalAlignment(JLabel.CENTER);
		storage1.setHorizontalAlignment(JLabel.CENTER);
		storage2.setHorizontalAlignment(JLabel.CENTER);

		number2.setFont(new Font(fontName, Font.BOLD, 14));
		musicName2.setFont(new Font(fontName, Font.BOLD, 14));
		directory2.setFont(new Font(fontName, Font.BOLD, 14));
		header2.setFont(new Font(fontName, Font.BOLD, 14));
		storage2.setFont(new Font(fontName, Font.BOLD, 14));

		displayPanel.add(number1);
		displayPanel.add(number2);
		displayPanel.add(musicName1);
		displayPanel.add(musicName2);
		displayPanel.add(directory1);
		displayPanel.add(directory2);
		displayPanel.add(header1);
		displayPanel.add(header2);
		displayPanel.add(storage1);
		displayPanel.add(storage2);

		musicList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				music_index = musicList.getSelectedIndex();
				if (music_index != -1) {
					music_selected = MusicBase.getMusicBase().getMusics().get(music_index);
					musicName2.setText(music_selected.getMusicName());
					musicName2.setToolTipText(musicName2.getText());
					directory2.setText(music_selected.getDirectory());
					directory2.setToolTipText(directory2.getText());
					header2.setText(music_selected.getHeader());
					header2.setToolTipText(header2.getText());
					storage2.setText(String.format("%.2fMB", music_selected.getStorage()));
				}
			}
		});

		// 2.2
		// 用户管理相关操作部分
		JPanel opPanel = new JPanel();
		opPanel.setLayout(new GridLayout(3, 1, 30, 30));
		opPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

		JButton add = new JButton("添加音乐文件"), delete = new JButton("删除选中音乐文件"), exit = new JButton("退出");

		delete.setForeground(Color.RED);
		exit.setForeground(Color.RED);

		// 为添加音乐按钮添加监听器
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setMultiSelectionEnabled(true);
				jfc.removeChoosableFileFilter(jfc.getChoosableFileFilters()[0]);

				// 设置可选MP3文件
				FileNameExtensionFilter filterMP3 = new FileNameExtensionFilter("MP3文件(*.mp3)", "mp3");
				jfc.addChoosableFileFilter(filterMP3);

				// 默认为MP3
				jfc.setFileFilter(filterMP3);

				jfc.setCurrentDirectory(fileDirectory);
				int f = jfc.showOpenDialog(frame);
				if (f == JFileChooser.APPROVE_OPTION) {
					File[] files = jfc.getSelectedFiles();
					if (jfc.getFileFilter().equals(filterMP3)) {
						for (File file : files) {
							String musicName = file.getName().split(".mp3")[0];
							String directory = file.getAbsolutePath();
							System.out.println(directory);
							Music music = new Music(musicName, directory.replaceAll("\\\\", "\\\\\\\\"));
							try {
								MP3Database.addToDatabase(music);
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(null, "音乐添加失败", "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							MusicBase.getMusicBase().addMusic(music);
						}
						// 更新信息列表
						fresh_musicList();
						musicName2.setText("");
						directory2.setText("");
						header2.setText("");
						storage2.setText("");
						music_selected = null;
					}
					fileDirectory = jfc.getSelectedFile();
				}
			}
		});

		// 为删除音乐按钮添加监听器
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (music_selected == null) {
					JOptionPane.showMessageDialog(null, "未选中任何音乐", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// 从数据库中删除音乐
				try {
					MP3Database.deleteDatabase(music_selected);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "用户删除失败", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				MusicBase.getMusicBase().getMusics().remove(music_selected);
				// 更新信息列表
				fresh_musicList();
				musicName2.setText("");
				directory2.setText("");
				header2.setText("");
				storage2.setText("");
				music_selected = null;
			}
		});

		// 为退出按钮添加监听器
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
		});

		opPanel.add(add);
		opPanel.add(delete);
		opPanel.add(exit);

		infoPanel.add(displayPanel);
		infoPanel.add(opPanel);

		frame.add(musicPanel);
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

	// 刷新音乐列表
	private void fresh_musicList() {
		listModel.removeAllElements();
		for (Music music : MusicBase.getMusicBase().getMusics()) {
			listModel.addElement(music.getMusicName());
		}
	}
}

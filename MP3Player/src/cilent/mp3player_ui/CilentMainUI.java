package cilent.mp3player_ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import jmp123.gui.AudioGUI;
import jmp123.gui.PlayList;
import jmp123.gui.PlayListThread;

public class CilentMainUI {
	// 窗口的总宽度
	private final int totalWeight = 900;
	// 频谱部分的长度
	private final int viewWeight = 650;
	// 播放列表的总长度
	private final int playListWeight = totalWeight - viewWeight;
	// 用户选择下载和本地的折叠页
	private JTabbedPane music;
	// 静态化便于音乐播放窗口调用修改
	// 显示当前播放时间
	private static JLabel time1;
	// 显示当前音乐的总时间
	private static JLabel time3;
	// 音乐播放进度条
	private static JSlider slider;
	// 当前播放的音乐头信息
	private static JCheckBoxMenuItem musicMsg;
	// 音乐功能栏的图片
	//private ImageIcon collect_icon, not_collect_icon;
	// 音乐功能栏的按钮
	private IconButton backward, forward; //collect;
	// 静态化以供播放列表变更时调用
	private static IconButton play_pause, play_model;
	// 静态化以供静态方法调用
	private static ImageIcon play_icon, pause_icon, list_loop_icon, single_loop_icon;
	// 当前文件保存路径
	// 和文件打开路径
	// private static File currentDirectory = new File("D:\\Music\\BGM(Fate)");
	private static File currentDirectory = new File("C:\\Users\\asus\\Desktop");
	// 音乐控制图标文件存放地
	private String marks_filename = "./res/marks/";
	// 本地音乐播放列表
	private PlayList playList;
	// 当前音乐播放线程
	private PlayListThread playListThread;
	// 音乐播放可视化窗口
	private AudioGUI theAudioGUI;
	// 当前外观样式
	// 默认为Windows风格
	private String preLAF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	public static void main(String[] args) {
		new CilentMainUI();
	}

	public CilentMainUI() {
		// 初始化界面风格
		try {
			UIManager.setLookAndFeel(preLAF);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "界面初始化失败", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		ui_init();
	}

	public void ui_init() {
		// 音乐播放平台的总窗口
		JFrame main = new JFrame("Music From QK");

		// 设置主窗口的退出功能
		main.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (DownloadListUI.isLogin_status()) {
					JOptionPane.showMessageDialog(main, "账户未退出，窗口无法关闭", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				} else {
					System.exit(-1);
				}
			}
		});

		// Part 1
		// 总窗口菜单部分
		JMenuBar menuBar = new JMenuBar();
		// 1.1 "文件"菜单项
		JMenu file = new JMenu("文件");
		file.setMnemonic('F');
		JMenuItem open = new JMenuItem("打开");
		open.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.ALT_MASK));
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileOpen(main);
				if (!play_pause.getMusic_Status()) {
					play_pause.setIcon(pause_icon);
					play_pause.setToolTipText("暂停");
					play_pause.setMusic_Status(true);
					music.setSelectedIndex(1);
				}
			}
		});
		JMenuItem exit = new JMenuItem("退出");
		exit.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.ALT_MASK));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(-1);
			}
		});

		file.add(open);
		file.addSeparator();
		file.add(exit);
		menuBar.add(file);

		// 1.2 "视图"菜单项
		JMenu view = new JMenu("视图");
		view.setMnemonic('V');
		JCheckBoxMenuItem spectrum = new JCheckBoxMenuItem("频谱");
		JMenu bandNumber = createBandNumberMenu("频谱数量");
		spectrum.setSelected(true);
		spectrum.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.ALT_MASK));
		spectrum.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				theAudioGUI.setVisible(spectrum.isSelected());
				bandNumber.setEnabled(spectrum.isSelected());
			}
		});
		musicMsg = new JCheckBoxMenuItem("音乐信息");
		musicMsg.setSelected(true);
		musicMsg.setAccelerator(KeyStroke.getKeyStroke('M', KeyEvent.ALT_MASK));

		view.add(spectrum);
		view.add(bandNumber);
		view.add(musicMsg);
		view.addSeparator();
		view.add(createLookAndFeelMenu("外观形式", main));
		menuBar.add(view);

		// 1.3"帮助"菜单项
		JMenu help = new JMenu("帮助");
		help.setMnemonic('H');
		JMenuItem about = new JMenuItem("关于...");
		about.setAccelerator(KeyStroke.getKeyStroke('A', KeyEvent.ALT_MASK));
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"播放器制作者：QinKuai\nDecoder提供：JMP123（网址：http://jmp123.sf.net/）\n" + "感谢开源解码器的支持\n2018/12/9",
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		help.add(about);
		menuBar.add(help);

		// Part 2
		// 总窗口音乐列表部分
		music = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		music.setPreferredSize(new Dimension(playListWeight, 500));
		music.setBorder(BorderFactory.createEtchedBorder());
		music.setFocusable(false);
		// 2.1 下载选择页
		ImageIcon download_icon = new ImageIcon(marks_filename + "download.png");
		music.addTab("下载", download_icon, null, "在线下载");
		// 2.2 本地播放选择页
		ImageIcon local_icon = new ImageIcon(marks_filename + "local.png");
		music.addTab("本地", local_icon, null, "本地播放");

		// 2.1.1 下载
		music.setComponentAt(0, new DownloadListUI(main, music));
		// 2.2.1 本地播放
		playList = new PlayList();
		JScrollPane scrollPane = new JScrollPane(playList);
		music.setComponentAt(1, scrollPane);
		playList.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					playListThread.startPlay(playList.locationToIndex(e.getPoint()));
					startPlaylistThread();
				}
			}
		});

		// 播放列表的邮件菜单
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem delete = new JMenuItem("删除歌曲");
		JMenuItem deleteAll = new JMenuItem("清除列表");

		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playListThread.removeSelectedItem();
			}
		});

		deleteAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playListThread.removeAllItems();
				playListThread.interrupt();
			}
		});

		popupMenu.add(delete);
		popupMenu.add(deleteAll);

		playList.setComponentPopupMenu(popupMenu);

		// Part 3
		// 总窗口频谱显示及音频播放部分
		JPanel display = new JPanel();
		display.setBorder(BorderFactory.createEtchedBorder());
		display.setPreferredSize(new Dimension(viewWeight, 500));
		theAudioGUI = new AudioGUI(41000);
		theAudioGUI.setPreferredSize(new Dimension(viewWeight, 500));
		display.add(theAudioGUI);
		theAudioGUI.setVisible(true);

		// Part 4
		// 总窗口音乐控制部分
		JPanel control = new JPanel();
		control.setBorder(BorderFactory.createEtchedBorder());
		control.setBackground(new Color(125, 125, 125));

		// 音乐播放图标
		play_icon = new ImageIcon(marks_filename + "play.png");
		pause_icon = new ImageIcon(marks_filename + "pause.png");
		list_loop_icon = new ImageIcon(marks_filename + "list_loop.png");
		single_loop_icon = new ImageIcon(marks_filename + "single_loop.png");
		// collect_icon = new ImageIcon(marks_filename + "red_heart.png");
		// not_collect_icon = new ImageIcon(marks_filename + "white_heart.png");

		// 用按钮做音乐播放组件
		play_model = new IconButton(list_loop_icon);
		play_model.setToolTipText("顺序播放");
		backward = new IconButton(new ImageIcon(marks_filename + "step-backward.png"));
		backward.setToolTipText("上一曲");
		play_pause = new IconButton(play_icon);
		play_pause.setToolTipText("播放");
		forward = new IconButton(new ImageIcon(marks_filename + "step-forward.png"));
		forward.setToolTipText("下一曲");
		// collect = new IconButton(not_collect_icon);
		// collect.setToolTipText("收藏");
		play_model.setHandCursor();
		backward.setHandCursor();
		play_pause.setHandCursor();
		forward.setHandCursor();
		// collect.setHandCursor();

		// 默认播放条
		slider = new JSlider();
		slider.setValue(slider.getMinimum());
		slider.setFocusable(false);
		slider.setPreferredSize(new Dimension(500, 30));
		slider.setBackground(new Color(125, 125, 125));

		// 盛装实现显示标签的容器
		// 音乐播放时间显示

		time1 = new JLabel("00:00");
		Font nowFont = time1.getFont();
		time1.setFont(new Font(nowFont.getFontName(), Font.BOLD, 10));

		JLabel time2 = new JLabel("/");
		time2.setFont(new Font(nowFont.getFontName(), Font.BOLD, 10));

		time3 = new JLabel("00:00");
		time3.setFont(new Font(nowFont.getFontName(), Font.BOLD, 10));

//		int fulltime = 365;
//		slider.setMaximum(fulltime);
//
//		time3.setText(timeTransform(fulltime));

//		slider.addChangeListener(new ChangeListener() {
//
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				JSlider source = (JSlider) e.getSource();
//				int nowtime = source.getValue();
//
//				time.setText(timeTransform(nowtime));
//			}
//		});

//		slider.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent e) {
//				int nowValue = slider.getValue();
//				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
//					if (nowValue > 0) {
//						slider.setValue(nowValue--);
//					}
//				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
//					if (nowValue < slider.getMaximum()) {
//						slider.setValue(nowValue++);
//					}
//				}
//			}
//		});

		// 播放模式的切换
		// false -> list_loop
		// true -> single_loop
		play_model.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aEvent) {
				play_model();
			}
		});

		// 暂停和播放
		// false -> pause
		// true -> play
		play_pause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aEvent) {
				if (playList.getCount() != 0) {
					play_pause();
					playListThread.pause();
				}
			}
		});

		// 对播放列表添加空格键暂停/播放功能
		main.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (playList.getCount() != 0 && e.getKeyCode() == KeyEvent.VK_SPACE) {
					play_pause();
					playListThread.pause();
				}
			}
		});

		// 对播放列表添加空格键暂停/播放功能
		playList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (playList.getCount() != 0 && e.getKeyCode() == KeyEvent.VK_SPACE) {
					play_pause();
					playListThread.pause();
				}
			}
		});

		// 收藏
		// false -> not_collect
		// ture -> collect
//		collect.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent aEvent) {
//				boolean status = collect.getMusic_Status();
//				collect.setIcon(status ? not_collect_icon : collect_icon);
//				collect.setToolTipText(status ? "收藏" : "取消收藏");
//				collect.setMusic_Status(status ? false : true);
//			}
//		});

		// 下一曲
		forward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playListThread.playNext();
			}
		});

		// 上一曲
		backward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playListThread.playBack();
			}
		});

		control.add(play_model);
		control.add(backward);
		control.add(play_pause);
		control.add(forward);
		control.add(slider);
		control.add(time1);
		control.add(time2);
		control.add(time3);
//		control.add(collect);

		main.setJMenuBar(menuBar);
		main.add(display);
		main.add(control, BorderLayout.SOUTH);
		main.add(music, BorderLayout.WEST);

		// 主页面基本设置
		// 窗口图标
		ImageIcon AppIcon = new ImageIcon("./res/image/app_icon.png");
		main.setIconImage(AppIcon.getImage());
		// 总窗口缩紧
		main.pack();
		// 总窗口居中
		main.setLocationRelativeTo(null);
		// 总窗口自带关闭键不可用
		main.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// 总窗口不可缩放
		main.setResizable(false);
		// 总窗口可见
		main.setVisible(true);
	}

	// 创建更改窗口风格的菜单栏
	private JMenu createLookAndFeelMenu(String title, JFrame main) {
		JMenu lafMenu = new JMenu(title);
		ButtonGroup buttonGroup = new ButtonGroup();
		// 获取系统中现有的外观模式
		UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();

		for (UIManager.LookAndFeelInfo laf : lafInfo) {
			JRadioButtonMenuItem lafItem = new JRadioButtonMenuItem(laf.getName(), preLAF == laf.getClassName());

			lafItem.addActionListener(new ActionListener() {
				String lafClassName = laf.getClassName();

				@Override
				public void actionPerformed(ActionEvent e) {
					if (preLAF != lafClassName) {
						setLookAndFeel(lafClassName, main);
					}
				}
			});

			lafMenu.add(lafItem);
			buttonGroup.add(lafItem);
		}
		return lafMenu;
	}

	// 设置外观形式并刷新窗口
	private void setLookAndFeel(String lafClassName, JFrame frame) {
		preLAF = lafClassName;
		try {
			UIManager.setLookAndFeel(lafClassName);
		} catch (Exception e) {
			preLAF = UIManager.getCrossPlatformLookAndFeelClassName();
		}
		SwingUtilities.updateComponentTreeUI(frame);
	}

	// 创建更改频谱的方法
	private JMenu createBandNumberMenu(String title) {
		JMenu bandMenu = new JMenu(title);
		ButtonGroup buttonGroup = new ButtonGroup();
		JRadioButtonMenuItem manyBand = new JRadioButtonMenuItem("多");
		JRadioButtonMenuItem middleBand = new JRadioButtonMenuItem("中等", true);
		JRadioButtonMenuItem littleBand = new JRadioButtonMenuItem("少");
		JRadioButtonMenuItem lessBand = new JRadioButtonMenuItem("极少");

		manyBand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				theAudioGUI.setBandNumber(AudioGUI.MANY_BAND);
			}
		});

		middleBand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				theAudioGUI.setBandNumber(AudioGUI.MIDDLE_BAND);
			}
		});

		littleBand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				theAudioGUI.setBandNumber(AudioGUI.LITTLE_BAND);
			}
		});

		lessBand.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				theAudioGUI.setBandNumber(AudioGUI.LESS_BAND);
			}
		});

		bandMenu.add(manyBand);
		bandMenu.add(middleBand);
		bandMenu.add(littleBand);
		bandMenu.add(lessBand);
		buttonGroup.add(manyBand);
		buttonGroup.add(middleBand);
		buttonGroup.add(littleBand);
		buttonGroup.add(lessBand);

		return bandMenu;
	}

	// 文件 -> 打开
	private void fileOpen(JFrame frame) {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(true);
		jfc.removeChoosableFileFilter(jfc.getChoosableFileFilters()[0]);

		// 设置可选MP3文件
		FileNameExtensionFilter filterMP3 = new FileNameExtensionFilter("MP3文件(*.mp3)", "mp3");
		jfc.addChoosableFileFilter(filterMP3);

		// 默认为MP3
		jfc.setFileFilter(filterMP3);

		jfc.setCurrentDirectory(currentDirectory);

		int f = jfc.showOpenDialog(frame);
		if (f == JFileChooser.APPROVE_OPTION) {
			File[] files = jfc.getSelectedFiles();
			// String strPath = jfc.getSelectedFile().getPath();
			if (jfc.getFileFilter().equals(filterMP3)) {
				for (File file : files) {
					playList.append(file.getName(), file.getPath());
				}
			}
		}
		currentDirectory = jfc.getCurrentDirectory();

		startPlaylistThread();
	}

	// 开始音乐播放线程
	private void startPlaylistThread() {
		if (playList.getCount() == 0) {
			return;
		}

		if (playListThread == null || !playListThread.isAlive()) {
			playListThread = new PlayListThread(playList, theAudioGUI);
			playListThread.start();
		}

	}
//
//	private String timeTransform(int fulltime) {
//		int minutes = fulltime / 60;
//		int seconds = fulltime - 60 * minutes;
//		String result = "";
//
//		result += (minutes / 10 == 0) ? ("0" + minutes) : minutes;
//		result += ":";
//		result += (seconds / 10 == 0) ? ("0" + seconds) : seconds;
//
//		return result;
//	}

	// 专供播放/暂停按钮调用
	// 实现按钮状态的转换
	public static void play_pause() {
		boolean status = play_pause.getMusic_Status();
		play_pause.setIcon(status ? play_icon : pause_icon);
		play_pause.setToolTipText(status ? "播放" : "暂停");
		play_pause.setMusic_Status(status ? false : true);
	}

	// 专供播放模式调用
	// 实现播放模式的转换
	public static void play_model() {
		boolean status = play_model.getMusic_Status();
		play_model.setIcon(status ? list_loop_icon : single_loop_icon);
		play_model.setToolTipText(status ? "顺序播放" : "单曲循环");
		play_model.setMusic_Status(status ? false : true);
	}

	public static JLabel getTime3() {
		return time3;
	}

	public static JLabel getTime1() {
		return time1;
	}

	public static JSlider getSlider() {
		return slider;
	}

	public static JCheckBoxMenuItem getMusicMsg() {
		return musicMsg;
	}

	public static IconButton getPlay_pause() {
		return play_pause;
	}

	public static IconButton getPlay_model() {
		return play_model;
	}

	public static File getCurrentDirectory() {
		return currentDirectory;
	}
	
	public static void setCurrentDirectory(File currentDirectory) {
		CilentMainUI.currentDirectory = currentDirectory;
	}
}

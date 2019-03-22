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
	// ���ڵ��ܿ��
	private final int totalWeight = 900;
	// Ƶ�ײ��ֵĳ���
	private final int viewWeight = 650;
	// �����б���ܳ���
	private final int playListWeight = totalWeight - viewWeight;
	// �û�ѡ�����غͱ��ص��۵�ҳ
	private JTabbedPane music;
	// ��̬���������ֲ��Ŵ��ڵ����޸�
	// ��ʾ��ǰ����ʱ��
	private static JLabel time1;
	// ��ʾ��ǰ���ֵ���ʱ��
	private static JLabel time3;
	// ���ֲ��Ž�����
	private static JSlider slider;
	// ��ǰ���ŵ�����ͷ��Ϣ
	private static JCheckBoxMenuItem musicMsg;
	// ���ֹ�������ͼƬ
	//private ImageIcon collect_icon, not_collect_icon;
	// ���ֹ������İ�ť
	private IconButton backward, forward; //collect;
	// ��̬���Թ������б���ʱ����
	private static IconButton play_pause, play_model;
	// ��̬���Թ���̬��������
	private static ImageIcon play_icon, pause_icon, list_loop_icon, single_loop_icon;
	// ��ǰ�ļ�����·��
	// ���ļ���·��
	// private static File currentDirectory = new File("D:\\Music\\BGM(Fate)");
	private static File currentDirectory = new File("C:\\Users\\asus\\Desktop");
	// ���ֿ���ͼ���ļ���ŵ�
	private String marks_filename = "./res/marks/";
	// �������ֲ����б�
	private PlayList playList;
	// ��ǰ���ֲ����߳�
	private PlayListThread playListThread;
	// ���ֲ��ſ��ӻ�����
	private AudioGUI theAudioGUI;
	// ��ǰ�����ʽ
	// Ĭ��ΪWindows���
	private String preLAF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

	public static void main(String[] args) {
		new CilentMainUI();
	}

	public CilentMainUI() {
		// ��ʼ��������
		try {
			UIManager.setLookAndFeel(preLAF);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "�����ʼ��ʧ��", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		ui_init();
	}

	public void ui_init() {
		// ���ֲ���ƽ̨���ܴ���
		JFrame main = new JFrame("Music From QK");

		// ���������ڵ��˳�����
		main.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (DownloadListUI.isLogin_status()) {
					JOptionPane.showMessageDialog(main, "�˻�δ�˳��������޷��ر�", "Warning", JOptionPane.WARNING_MESSAGE);
					return;
				} else {
					System.exit(-1);
				}
			}
		});

		// Part 1
		// �ܴ��ڲ˵�����
		JMenuBar menuBar = new JMenuBar();
		// 1.1 "�ļ�"�˵���
		JMenu file = new JMenu("�ļ�");
		file.setMnemonic('F');
		JMenuItem open = new JMenuItem("��");
		open.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.ALT_MASK));
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileOpen(main);
				if (!play_pause.getMusic_Status()) {
					play_pause.setIcon(pause_icon);
					play_pause.setToolTipText("��ͣ");
					play_pause.setMusic_Status(true);
					music.setSelectedIndex(1);
				}
			}
		});
		JMenuItem exit = new JMenuItem("�˳�");
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

		// 1.2 "��ͼ"�˵���
		JMenu view = new JMenu("��ͼ");
		view.setMnemonic('V');
		JCheckBoxMenuItem spectrum = new JCheckBoxMenuItem("Ƶ��");
		JMenu bandNumber = createBandNumberMenu("Ƶ������");
		spectrum.setSelected(true);
		spectrum.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.ALT_MASK));
		spectrum.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				theAudioGUI.setVisible(spectrum.isSelected());
				bandNumber.setEnabled(spectrum.isSelected());
			}
		});
		musicMsg = new JCheckBoxMenuItem("������Ϣ");
		musicMsg.setSelected(true);
		musicMsg.setAccelerator(KeyStroke.getKeyStroke('M', KeyEvent.ALT_MASK));

		view.add(spectrum);
		view.add(bandNumber);
		view.add(musicMsg);
		view.addSeparator();
		view.add(createLookAndFeelMenu("�����ʽ", main));
		menuBar.add(view);

		// 1.3"����"�˵���
		JMenu help = new JMenu("����");
		help.setMnemonic('H');
		JMenuItem about = new JMenuItem("����...");
		about.setAccelerator(KeyStroke.getKeyStroke('A', KeyEvent.ALT_MASK));
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"�����������ߣ�QinKuai\nDecoder�ṩ��JMP123����ַ��http://jmp123.sf.net/��\n" + "��л��Դ��������֧��\n2018/12/9",
						"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		help.add(about);
		menuBar.add(help);

		// Part 2
		// �ܴ��������б���
		music = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		music.setPreferredSize(new Dimension(playListWeight, 500));
		music.setBorder(BorderFactory.createEtchedBorder());
		music.setFocusable(false);
		// 2.1 ����ѡ��ҳ
		ImageIcon download_icon = new ImageIcon(marks_filename + "download.png");
		music.addTab("����", download_icon, null, "��������");
		// 2.2 ���ز���ѡ��ҳ
		ImageIcon local_icon = new ImageIcon(marks_filename + "local.png");
		music.addTab("����", local_icon, null, "���ز���");

		// 2.1.1 ����
		music.setComponentAt(0, new DownloadListUI(main, music));
		// 2.2.1 ���ز���
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

		// �����б���ʼ��˵�
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem delete = new JMenuItem("ɾ������");
		JMenuItem deleteAll = new JMenuItem("����б�");

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
		// �ܴ���Ƶ����ʾ����Ƶ���Ų���
		JPanel display = new JPanel();
		display.setBorder(BorderFactory.createEtchedBorder());
		display.setPreferredSize(new Dimension(viewWeight, 500));
		theAudioGUI = new AudioGUI(41000);
		theAudioGUI.setPreferredSize(new Dimension(viewWeight, 500));
		display.add(theAudioGUI);
		theAudioGUI.setVisible(true);

		// Part 4
		// �ܴ������ֿ��Ʋ���
		JPanel control = new JPanel();
		control.setBorder(BorderFactory.createEtchedBorder());
		control.setBackground(new Color(125, 125, 125));

		// ���ֲ���ͼ��
		play_icon = new ImageIcon(marks_filename + "play.png");
		pause_icon = new ImageIcon(marks_filename + "pause.png");
		list_loop_icon = new ImageIcon(marks_filename + "list_loop.png");
		single_loop_icon = new ImageIcon(marks_filename + "single_loop.png");
		// collect_icon = new ImageIcon(marks_filename + "red_heart.png");
		// not_collect_icon = new ImageIcon(marks_filename + "white_heart.png");

		// �ð�ť�����ֲ������
		play_model = new IconButton(list_loop_icon);
		play_model.setToolTipText("˳�򲥷�");
		backward = new IconButton(new ImageIcon(marks_filename + "step-backward.png"));
		backward.setToolTipText("��һ��");
		play_pause = new IconButton(play_icon);
		play_pause.setToolTipText("����");
		forward = new IconButton(new ImageIcon(marks_filename + "step-forward.png"));
		forward.setToolTipText("��һ��");
		// collect = new IconButton(not_collect_icon);
		// collect.setToolTipText("�ղ�");
		play_model.setHandCursor();
		backward.setHandCursor();
		play_pause.setHandCursor();
		forward.setHandCursor();
		// collect.setHandCursor();

		// Ĭ�ϲ�����
		slider = new JSlider();
		slider.setValue(slider.getMinimum());
		slider.setFocusable(false);
		slider.setPreferredSize(new Dimension(500, 30));
		slider.setBackground(new Color(125, 125, 125));

		// ʢװʵ����ʾ��ǩ������
		// ���ֲ���ʱ����ʾ

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

		// ����ģʽ���л�
		// false -> list_loop
		// true -> single_loop
		play_model.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent aEvent) {
				play_model();
			}
		});

		// ��ͣ�Ͳ���
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

		// �Բ����б���ӿո����ͣ/���Ź���
		main.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (playList.getCount() != 0 && e.getKeyCode() == KeyEvent.VK_SPACE) {
					play_pause();
					playListThread.pause();
				}
			}
		});

		// �Բ����б���ӿո����ͣ/���Ź���
		playList.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (playList.getCount() != 0 && e.getKeyCode() == KeyEvent.VK_SPACE) {
					play_pause();
					playListThread.pause();
				}
			}
		});

		// �ղ�
		// false -> not_collect
		// ture -> collect
//		collect.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent aEvent) {
//				boolean status = collect.getMusic_Status();
//				collect.setIcon(status ? not_collect_icon : collect_icon);
//				collect.setToolTipText(status ? "�ղ�" : "ȡ���ղ�");
//				collect.setMusic_Status(status ? false : true);
//			}
//		});

		// ��һ��
		forward.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				playListThread.playNext();
			}
		});

		// ��һ��
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

		// ��ҳ���������
		// ����ͼ��
		ImageIcon AppIcon = new ImageIcon("./res/image/app_icon.png");
		main.setIconImage(AppIcon.getImage());
		// �ܴ�������
		main.pack();
		// �ܴ��ھ���
		main.setLocationRelativeTo(null);
		// �ܴ����Դ��رռ�������
		main.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// �ܴ��ڲ�������
		main.setResizable(false);
		// �ܴ��ڿɼ�
		main.setVisible(true);
	}

	// �������Ĵ��ڷ��Ĳ˵���
	private JMenu createLookAndFeelMenu(String title, JFrame main) {
		JMenu lafMenu = new JMenu(title);
		ButtonGroup buttonGroup = new ButtonGroup();
		// ��ȡϵͳ�����е����ģʽ
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

	// ���������ʽ��ˢ�´���
	private void setLookAndFeel(String lafClassName, JFrame frame) {
		preLAF = lafClassName;
		try {
			UIManager.setLookAndFeel(lafClassName);
		} catch (Exception e) {
			preLAF = UIManager.getCrossPlatformLookAndFeelClassName();
		}
		SwingUtilities.updateComponentTreeUI(frame);
	}

	// ��������Ƶ�׵ķ���
	private JMenu createBandNumberMenu(String title) {
		JMenu bandMenu = new JMenu(title);
		ButtonGroup buttonGroup = new ButtonGroup();
		JRadioButtonMenuItem manyBand = new JRadioButtonMenuItem("��");
		JRadioButtonMenuItem middleBand = new JRadioButtonMenuItem("�е�", true);
		JRadioButtonMenuItem littleBand = new JRadioButtonMenuItem("��");
		JRadioButtonMenuItem lessBand = new JRadioButtonMenuItem("����");

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

	// �ļ� -> ��
	private void fileOpen(JFrame frame) {
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(true);
		jfc.removeChoosableFileFilter(jfc.getChoosableFileFilters()[0]);

		// ���ÿ�ѡMP3�ļ�
		FileNameExtensionFilter filterMP3 = new FileNameExtensionFilter("MP3�ļ�(*.mp3)", "mp3");
		jfc.addChoosableFileFilter(filterMP3);

		// Ĭ��ΪMP3
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

	// ��ʼ���ֲ����߳�
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

	// ר������/��ͣ��ť����
	// ʵ�ְ�ť״̬��ת��
	public static void play_pause() {
		boolean status = play_pause.getMusic_Status();
		play_pause.setIcon(status ? play_icon : pause_icon);
		play_pause.setToolTipText(status ? "����" : "��ͣ");
		play_pause.setMusic_Status(status ? false : true);
	}

	// ר������ģʽ����
	// ʵ�ֲ���ģʽ��ת��
	public static void play_model() {
		boolean status = play_model.getMusic_Status();
		play_model.setIcon(status ? list_loop_icon : single_loop_icon);
		play_model.setToolTipText(status ? "˳�򲥷�" : "����ѭ��");
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

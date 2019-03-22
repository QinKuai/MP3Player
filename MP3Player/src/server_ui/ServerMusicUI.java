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
	// ���ֹ���˵ĵ�ʵ����
	private static ServerMusicUI serverMusicUI;
	// ��ǰѡ�е������ļ�
	private Music music_selected;
	// ��ǰѡ�е��ļ�����
	private int music_index;
	// ��������������
	private JFrame frame;
	// �����ļ��б��Ĭ���б�ģ��
	private static DefaultListModel<String> listModel;
	// ��ǰ�����ļ�ѡ��·��
	private File fileDirectory = new File("C:\\Users\\asus\\Desktop");

	// ������
	private ServerMusicUI(ServerMP3Net music_connect) {
		ui_init(music_connect);
	}

	// ����������ʱֻ�ܵ��ô˷���
	public static ServerMusicUI getServerMusicUI(ServerMP3Net music_connect) {
		if (serverMusicUI == null) {
			serverMusicUI = new ServerMusicUI(music_connect);
		}
		serverMusicUI.frame.setVisible(true);
		return serverMusicUI;
	}

	// �����ʼ��
	private void ui_init(ServerMP3Net music_connect) {
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

		// ���ֹ������
		frame = new JFrame("Music Management");
		frame.setLayout(new GridLayout(1, 2));

		// 1
		// ���ô�����������б���ʾ����
		listModel = new DefaultListModel<String>();
		JList<String> musicList = new JList<String>(listModel);
		String fontName = musicList.getFont().getFontName();
		musicList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		musicList.setBorder(BorderFactory.createEtchedBorder());
		musicList.setFont(new Font(fontName, Font.BOLD, 14));

		fresh_musicList();

		// �����б�����Ϊ�����б�
		JScrollPane musicPanel = new JScrollPane(musicList);
		musicPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

		// 2
		// ������Ϣ��ʾ����������
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(2, 1));

		// 2.1
		// �û���Ϣ��ʾ����
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new GridLayout(5, 2));
		displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JLabel number1 = new JLabel("����������"), number2 = new JLabel(), musicName1 = new JLabel("����_���ߣ�"),
				musicName2 = new JLabel(), directory1 = new JLabel("�洢·����"), directory2 = new JLabel(),
				header1 = new JLabel("ͷ��Ϣ��"), header2 = new JLabel(), storage1 = new JLabel("�ļ���С"),
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
		// �û�������ز�������
		JPanel opPanel = new JPanel();
		opPanel.setLayout(new GridLayout(3, 1, 30, 30));
		opPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

		JButton add = new JButton("��������ļ�"), delete = new JButton("ɾ��ѡ�������ļ�"), exit = new JButton("�˳�");

		delete.setForeground(Color.RED);
		exit.setForeground(Color.RED);

		// Ϊ������ְ�ť��Ӽ�����
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				jfc.setMultiSelectionEnabled(true);
				jfc.removeChoosableFileFilter(jfc.getChoosableFileFilters()[0]);

				// ���ÿ�ѡMP3�ļ�
				FileNameExtensionFilter filterMP3 = new FileNameExtensionFilter("MP3�ļ�(*.mp3)", "mp3");
				jfc.addChoosableFileFilter(filterMP3);

				// Ĭ��ΪMP3
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
								JOptionPane.showMessageDialog(null, "�������ʧ��", "Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							MusicBase.getMusicBase().addMusic(music);
						}
						// ������Ϣ�б�
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

		// Ϊɾ�����ְ�ť��Ӽ�����
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (music_selected == null) {
					JOptionPane.showMessageDialog(null, "δѡ���κ�����", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// �����ݿ���ɾ������
				try {
					MP3Database.deleteDatabase(music_selected);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "�û�ɾ��ʧ��", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				MusicBase.getMusicBase().getMusics().remove(music_selected);
				// ������Ϣ�б�
				fresh_musicList();
				musicName2.setText("");
				directory2.setText("");
				header2.setText("");
				storage2.setText("");
				music_selected = null;
			}
		});

		// Ϊ�˳���ť��Ӽ�����
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

	// ˢ�������б�
	private void fresh_musicList() {
		listModel.removeAllElements();
		for (Music music : MusicBase.getMusicBase().getMusics()) {
			listModel.addElement(music.getMusicName());
		}
	}
}

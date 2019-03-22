package cilent.net;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cilent.mp3player_ui.CilentMainUI;
import cilent.mp3player_ui.DownloadListUI;
import cilent.mp3player_ui.ProgressFrame;
import domain.Music;

public class CilentMP3Net {
	// ������MP3ͨ·�Ķ˿�
	private int port;
	// ��������IP��ַ
	private String address;
	// �û����������������
	private Socket socket;
	// ͨ��������ͻ�������
	private ObjectInputStream ois;
	// ͨ����������������
	private ObjectOutputStream oos;
	// ȷ�����ڵ�����״̬
	// Ĭ��Ϊ����������
	private boolean download = false;

	// �ͻ����������ӹ�����
	public CilentMP3Net(String address, int port) {
		this.address = address;
		this.port = port;

		try {
			MP3Net_init();
		} catch (IOException e) {
		}
	}

	// �����ʼ������
	public void MP3Net_init() throws IOException {
		// ��������
		socket = new Socket(address, port);
		// �������
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}

	public Socket getSocket() {
		return this.socket;
	}

	// ������������������
	public void download(String musicName) {
		if (this.socket.isConnected()) {
			File newFile = null;
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.setCurrentDirectory(CilentMainUI.getCurrentDirectory());
			jfc.showSaveDialog(null);
			newFile = jfc.getSelectedFile();
			if(newFile != null) {
				CilentMainUI.setCurrentDirectory(newFile);
			}else {
				JOptionPane.showMessageDialog(null, "��������", "Message", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			try {
				oos.writeUnshared(musicName);
				oos.reset();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// �������������ص������ļ�
	public void download_back(String musicFileName) {
		String directory = CilentMainUI.getCurrentDirectory().getAbsolutePath() + "\\" + musicFileName;
		//�����������ļ���·��
		File file = new File(directory);
		try {
			int count = 0;
			byte[] buf = new byte[1024];
			BufferedOutputStream bufos = new BufferedOutputStream(new FileOutputStream(file));
			setDownload(true);
			ProgressFrame progressFrame = new ProgressFrame(directory);
			
			//�����з�����д�����������ļ���С
			float storage = (float)ois.readUnshared();
			int max = (int)(storage * 1024) + 1;
			int counter = 0;
			progressFrame.setMax(max);
			
			//�����д�������ļ�
			count = ois.read(buf);
			while (count > 0) {
				bufos.write(buf, 0, count);
				progressFrame.setValue(++counter);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
				if (ois.available() != 0) {
					count = ois.read(buf);
				} else {
					progressFrame.setValue(max);
					progressFrame.setVisible(false);
					break;
				}
			}
			
			bufos.close();
			setDownload(false);
			JOptionPane.showMessageDialog(null, musicFileName.split(".mp3")[0] + "�����سɹ�����");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "������!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ˢ�µ�ǰ�������б�
	public void fresh() {
		if (this.socket.isConnected()) {
			try {
				oos.writeUnshared("<<fresh>>");
				oos.reset();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// ���ܷ����������������б�
	public void receive_list() {
		int amount = 0;
		Music music = null;
		try {
			amount = (int) ois.readUnshared();
			for(int i = 0; i < amount; i++) {
				music = (Music) ois.readUnshared();
				DownloadListUI.getListModel().addElement(music.getMusicName());
			}
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public  boolean isDownload() {
		return download;
	}
	
	public void setDownload(boolean download) {
		this.download = download;
	}
}

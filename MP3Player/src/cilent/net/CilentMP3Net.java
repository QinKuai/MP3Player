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
	// 服务器MP3通路的端口
	private int port;
	// 服务器的IP地址
	private String address;
	// 用户端与服务器的连接
	private Socket socket;
	// 通过网络向客户端输入
	private ObjectInputStream ois;
	// 通过网络向服务器输出
	private ObjectOutputStream oos;
	// 确认现在的下载状态
	// 默认为不在下载中
	private boolean download = false;

	// 客户端网络连接构造器
	public CilentMP3Net(String address, int port) {
		this.address = address;
		this.port = port;

		try {
			MP3Net_init();
		} catch (IOException e) {
		}
	}

	// 网络初始化方法
	public void MP3Net_init() throws IOException {
		// 网络连接
		socket = new Socket(address, port);
		// 输入输出
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}

	public Socket getSocket() {
		return this.socket;
	}

	// 向服务器提出下载需求
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
				JOptionPane.showMessageDialog(null, "结束下载", "Message", JOptionPane.INFORMATION_MESSAGE);
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

	// 服务器返回下载的音乐文件
	public void download_back(String musicFileName) {
		String directory = CilentMainUI.getCurrentDirectory().getAbsolutePath() + "\\" + musicFileName;
		//创建该音乐文件的路径
		File file = new File(directory);
		try {
			int count = 0;
			byte[] buf = new byte[1024];
			BufferedOutputStream bufos = new BufferedOutputStream(new FileOutputStream(file));
			setDownload(true);
			ProgressFrame progressFrame = new ProgressFrame(directory);
			
			//读到有服务器写过来的音乐文件大小
			float storage = (float)ois.readUnshared();
			int max = (int)(storage * 1024) + 1;
			int counter = 0;
			progressFrame.setMax(max);
			
			//读入加写出音乐文件
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
			JOptionPane.showMessageDialog(null, musicFileName.split(".mp3")[0] + "已下载成功！！");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "错误类!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// 刷新当前的下载列表
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
	
	// 接受服务器发出的下载列表
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

package domain;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JOptionPane;

import jmp123.gui.PlayBack;

public class Music implements Serializable{
	//歌曲名
	private String musicName;
	//歌曲的头信息
	private String header;
	//歌曲的绝对路径
	private String directory;
	//歌曲所占的存储空间
	//MB
	private float storage;
	
	//歌曲对象默认构造器
	public Music(String musicName, String directory) {
		this.directory = directory;
		this.musicName = musicName;
		
		//获取音乐文件的文件大小
		File file = new File(this.directory);
		storage = (float)(file.length() / (1024 * 1024.0));
		
		//获取音乐文件的头信息
		PlayBack playBack = new PlayBack(null);
		try {
			playBack.open(this.directory, this.musicName);
		}catch (IOException e) {
			JOptionPane.showMessageDialog(null, "音乐文件读取失败", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		header = playBack.getHeader().printHeaderInfo();
	}
	
	public String getMusicName() {
		return musicName;
	}
	
	public String getHeader() {
		return header;
	}
	
	public float getStorage() {
		return storage;
	}
	
	public String getDirectory() {
		return directory;
	}
	
}

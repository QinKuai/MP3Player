package domain;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JOptionPane;

import jmp123.gui.PlayBack;

public class Music implements Serializable{
	//������
	private String musicName;
	//������ͷ��Ϣ
	private String header;
	//�����ľ���·��
	private String directory;
	//������ռ�Ĵ洢�ռ�
	//MB
	private float storage;
	
	//��������Ĭ�Ϲ�����
	public Music(String musicName, String directory) {
		this.directory = directory;
		this.musicName = musicName;
		
		//��ȡ�����ļ����ļ���С
		File file = new File(this.directory);
		storage = (float)(file.length() / (1024 * 1024.0));
		
		//��ȡ�����ļ���ͷ��Ϣ
		PlayBack playBack = new PlayBack(null);
		try {
			playBack.open(this.directory, this.musicName);
		}catch (IOException e) {
			JOptionPane.showMessageDialog(null, "�����ļ���ȡʧ��", "Error", JOptionPane.ERROR_MESSAGE);
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

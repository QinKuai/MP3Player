package domain;

import java.util.ArrayList;

public class MusicBase {
	//���ֿ�ĵ�ʵ��
	private static MusicBase musicInstance;
	//���������ļ��ı�
	private ArrayList<Music> musics;
	
	//˽�й�����
	private MusicBase() {
		musics = new ArrayList<Music>();
	}
	
	//���ʵ���ķ���
	public static MusicBase getMusicBase() {
		if(musicInstance == null) {
			musicInstance = new MusicBase();
		}
		
		return musicInstance;
	}
	
	//���ص�ǰ�������б�
	public ArrayList<Music> getMusics() {
		return musics;
	}
	
	// ͨ�������͵�ַ������ֶ���
	public void addMusic(String musicName, String directory) {
		musics.add(new Music(musicName, directory));
	}
	
	//ͨ��ֱ�Ӽ������ֶ���
	public void addMusic(Music music) {
		musics.add(music);
	}
	
	//ͨ�������ҵ�����
	//�ҵ��������ֶ���
	//δ�ҵ�����NULL
	public Music findMusic(String musicName) {
		for (Music music : musics) {
			if(music.getMusicName().equals(musicName)) {
				return music;
			}
		}
		
		return null;
	}
}

package domain;

import java.util.ArrayList;

public class MusicBase {
	//音乐库的单实例
	private static MusicBase musicInstance;
	//所有音乐文件的表
	private ArrayList<Music> musics;
	
	//私有构造器
	private MusicBase() {
		musics = new ArrayList<Music>();
	}
	
	//获得实例的方法
	public static MusicBase getMusicBase() {
		if(musicInstance == null) {
			musicInstance = new MusicBase();
		}
		
		return musicInstance;
	}
	
	//返回当前的音乐列表
	public ArrayList<Music> getMusics() {
		return musics;
	}
	
	// 通过曲名和地址添加音乐对象
	public void addMusic(String musicName, String directory) {
		musics.add(new Music(musicName, directory));
	}
	
	//通过直接加入音乐对象
	public void addMusic(Music music) {
		musics.add(music);
	}
	
	//通过曲名找到音乐
	//找到返回音乐对象
	//未找到返回NULL
	public Music findMusic(String musicName) {
		for (Music music : musics) {
			if(music.getMusicName().equals(musicName)) {
				return music;
			}
		}
		
		return null;
	}
}

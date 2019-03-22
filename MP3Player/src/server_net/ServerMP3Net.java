package server_net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import domain.Music;
import domain.MusicBase;

public class ServerMP3Net { 
	// 服务器音乐文件通路的网络
	private ServerSocket ss;
		
	// 服务器音乐文件通路构造器
	public ServerMP3Net(int mp3_cust) {		
		try {
			mp3Net_init(mp3_cust);
		} catch (IOException e) {
			System.out.println("音乐文件网络初始化失败");
			System.exit(-1);
		}
	}
		
	// 服务器音乐文件通路网络初始化方法
	private void mp3Net_init(int mp3_cust) throws IOException {
		ss = new ServerSocket(mp3_cust);
		System.out.println("音乐文件网络已启动");
			
		
		while(true) {
			Socket s = ss.accept();
			
			new Thread(new Runnable() {
				Socket socket = s;
				Object obj;
				ObjectInputStream ois;
				ObjectOutputStream oos;
				
				@Override
				public void run() {
					System.out.println(socket.getInetAddress() + "音乐网络已连接");
					try {
						oos = new ObjectOutputStream(socket.getOutputStream());
						ois = new ObjectInputStream(socket.getInputStream());
					}catch(IOException e) {
						System.out.println(socket.getInetAddress() + "音乐网络失去连接");
						return;
					}
					
					while(true) {
						try {
							obj = ois.readUnshared();
							if(obj instanceof String) { 
								//用户在服务器上刷新音乐列表
								if(((String)obj).equals("<<fresh>>")){
									//向客户端输出曲目的数量
									oos.writeObject(MusicBase.getMusicBase().getMusics().size());
									oos.reset();
										
									for (Music music : MusicBase.getMusicBase().getMusics()) {
										oos.writeUnshared(music);
										oos.reset();
									}
								//用户在服务器上下载音乐
								}else {
									Music music = MusicBase.getMusicBase().findMusic((String)obj);
									//向用户输出该音乐文件的大小
									oos.writeUnshared(music.getStorage());
									oos.reset();
										
									download(music, oos);
									System.out.println(socket.getInetAddress() + "下载了" + (String)obj);
									oos.reset();
								}
							}else if(obj instanceof Music) {
								
							}
						}catch(IOException e) {
							System.out.println(socket.getInetAddress() + " 音乐网络失去连接!!");
							return;
						}catch (ClassNotFoundException e) {
							System.out.println(socket.getInetAddress() + " 类传输失败!!");
							return;
						}
					}
				}
			}).start();
		}
	}
	
	//用户在服务器上下载音乐
	private void download(Music music, ObjectOutputStream oos) throws IOException{
		File file = new File(music.getDirectory());
		
		BufferedInputStream bufis = new BufferedInputStream(new FileInputStream(file));
		int count = 0;
		byte[] buf = new byte[1024 * 10];
		count = bufis.read(buf);
		while(count > 0) {
			oos.write(buf, 0, count);
			if(bufis.available() != 0) {
				count = bufis.read(buf);
			}else {
				break;
			}
		}
		bufis.close();
	}	
}

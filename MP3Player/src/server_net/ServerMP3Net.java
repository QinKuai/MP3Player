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
	// �����������ļ�ͨ·������
	private ServerSocket ss;
		
	// �����������ļ�ͨ·������
	public ServerMP3Net(int mp3_cust) {		
		try {
			mp3Net_init(mp3_cust);
		} catch (IOException e) {
			System.out.println("�����ļ������ʼ��ʧ��");
			System.exit(-1);
		}
	}
		
	// �����������ļ�ͨ·�����ʼ������
	private void mp3Net_init(int mp3_cust) throws IOException {
		ss = new ServerSocket(mp3_cust);
		System.out.println("�����ļ�����������");
			
		
		while(true) {
			Socket s = ss.accept();
			
			new Thread(new Runnable() {
				Socket socket = s;
				Object obj;
				ObjectInputStream ois;
				ObjectOutputStream oos;
				
				@Override
				public void run() {
					System.out.println(socket.getInetAddress() + "��������������");
					try {
						oos = new ObjectOutputStream(socket.getOutputStream());
						ois = new ObjectInputStream(socket.getInputStream());
					}catch(IOException e) {
						System.out.println(socket.getInetAddress() + "��������ʧȥ����");
						return;
					}
					
					while(true) {
						try {
							obj = ois.readUnshared();
							if(obj instanceof String) { 
								//�û��ڷ�������ˢ�������б�
								if(((String)obj).equals("<<fresh>>")){
									//��ͻ��������Ŀ������
									oos.writeObject(MusicBase.getMusicBase().getMusics().size());
									oos.reset();
										
									for (Music music : MusicBase.getMusicBase().getMusics()) {
										oos.writeUnshared(music);
										oos.reset();
									}
								//�û��ڷ���������������
								}else {
									Music music = MusicBase.getMusicBase().findMusic((String)obj);
									//���û�����������ļ��Ĵ�С
									oos.writeUnshared(music.getStorage());
									oos.reset();
										
									download(music, oos);
									System.out.println(socket.getInetAddress() + "������" + (String)obj);
									oos.reset();
								}
							}else if(obj instanceof Music) {
								
							}
						}catch(IOException e) {
							System.out.println(socket.getInetAddress() + " ��������ʧȥ����!!");
							return;
						}catch (ClassNotFoundException e) {
							System.out.println(socket.getInetAddress() + " �ഫ��ʧ��!!");
							return;
						}
					}
				}
			}).start();
		}
	}
	
	//�û��ڷ���������������
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

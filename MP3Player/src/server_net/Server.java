package server_net;

import java.util.Scanner;

import database.CustDatabase;
import database.MP3Database;
import server_ui.ServerCustUI;
import server_ui.ServerMusicUI;

public class Server {
	// �������û�ͨ·�Ķ˿�
	private static final int port_cust = 30000;
	// �����������ļ��Ķ˿�
	private static final int port_mp3 = 31000;
	// �������û�ͨ·ʵ��
	private ServerCustNet custNet;
	// �����������ļ�ͨ·ʵ��
	private ServerMP3Net mp3Net;
	

	// ��������������
	public static void main(String[] args) {
		// ��ȡ���ݿ�����ݵ�������
		try {
			CustDatabase.DatabaseToCusts();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("�û����ݶ�ȡʧ�ܣ���");
			System.exit(-1);
		}
		
		//��ȡ���ݿ�������ļ���������
		try {
			MP3Database.DatabaseToMusics();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("�������ݶ�ȡʧ�ܣ���");
			System.exit(-1);
		}
		// ��ʼ������������
		Server server = new Server(port_cust, port_mp3);

		// �����е���������������
		System.out.println("Use command like:\ncustui : Open the Custmers Management Frame\n"
				+ "mp3ui : Open the Music Management Frame\nend : close the server");
		Scanner sc = new Scanner(System.in);

		while (true) {
			String command = sc.nextLine();
			switch (command) {
			case "custui":
				ServerCustUI.getServerCustUI(server.getCustNet());
				break;
			case "mp3ui":
				ServerMusicUI.getServerMusicUI(server.getMp3Net());
				break;
			case "end":
				sc.close();
				System.out.println("���������˳�");
				System.exit(-1);
				break;
			default:
				System.err.println("����ָ���");
				break;

			}
		}
	}

	// ������������
	public Server(int port_cust, int port_mp3) {
		net_init(port_cust, port_mp3);
	}

	// �����������ʼ��
	private void net_init(int port_cust, int port_mp3) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				custNet = new ServerCustNet(port_cust);
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				mp3Net = new ServerMP3Net(port_mp3);
			}
		}).start();
	}

	// ��ȡ�û���������
	public ServerCustNet getCustNet() {
		return custNet;
	}

	// ��ȡMP3��������
	public ServerMP3Net getMp3Net() {
		return mp3Net;
	}
}

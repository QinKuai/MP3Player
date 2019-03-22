package server_net;

import java.util.Scanner;

import database.CustDatabase;
import database.MP3Database;
import server_ui.ServerCustUI;
import server_ui.ServerMusicUI;

public class Server {
	// 服务器用户通路的端口
	private static final int port_cust = 30000;
	// 服务器音乐文件的端口
	private static final int port_mp3 = 31000;
	// 服务器用户通路实例
	private ServerCustNet custNet;
	// 服务器音乐文件通路实例
	private ServerMP3Net mp3Net;
	

	// 服务器启动方法
	public static void main(String[] args) {
		// 读取数据库的数据到链表中
		try {
			CustDatabase.DatabaseToCusts();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("用户数据读取失败！！");
			System.exit(-1);
		}
		
		//读取数据库的音乐文件到链表中
		try {
			MP3Database.DatabaseToMusics();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("音乐数据读取失败！！");
			System.exit(-1);
		}
		// 初始化服务器网络
		Server server = new Server(port_cust, port_mp3);

		// 命令行调出服务器管理工具
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
				System.out.println("服务器已退出");
				System.exit(-1);
				break;
			default:
				System.err.println("错误指令！！");
				break;

			}
		}
	}

	// 服务器构造器
	public Server(int port_cust, int port_mp3) {
		net_init(port_cust, port_mp3);
	}

	// 服务器网络初始化
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

	// 获取用户网络连接
	public ServerCustNet getCustNet() {
		return custNet;
	}

	// 获取MP3网络连接
	public ServerMP3Net getMp3Net() {
		return mp3Net;
	}
}

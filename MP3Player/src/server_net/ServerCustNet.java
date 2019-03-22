package server_net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

import database.CustDatabase;
import domain.CustBase;
import domain.Customer;
import server_ui.ServerCustUI;

public class ServerCustNet {
	// 服务器用户通路的网络
	private ServerSocket ss;

	// 服务器用户通路构造器
	public ServerCustNet(int port_cust) {
		try {
			custNet_init(port_cust);
		} catch (IOException e) {
			System.out.println("用户网络初始化失败");
			System.exit(-1);
		}
	}

	// 服务器用户通路网络初始化方法
	private void custNet_init(int port_cust) throws IOException {
		ss = new ServerSocket(port_cust);
		System.out.println("用户网络已启动");

		while (true) {
			Socket s = ss.accept();

			new Thread(new Runnable() {
				Customer cust;
				Socket socket = s;
				ObjectInputStream ois;
				ObjectOutputStream oos;

				@Override
				public void run() {
					System.out.println(socket.getInetAddress() + "用户网络已连接");
					try {
						oos = new ObjectOutputStream(socket.getOutputStream());
						ois = new ObjectInputStream(socket.getInputStream());
					} catch (IOException e) {
						System.out.println(socket.getInetAddress() + "用户网络失去连接");
						return;
					}

					while (true) {
						try {
							cust = (Customer) ois.readUnshared();
							int opcode = cust.getOpcode();
							switch (opcode) {
							// 实现用户修改用户名
							case 0:
								oos.writeUnshared(modify(cust.getOldName(), cust));
								break;
							// 实现用户登陆
							case 1:
								oos.writeUnshared(login(cust.getName(), cust.getPassword()));
								break;
							// 实现用户注册
							case 2:
								oos.writeUnshared(register(cust));
								break;
							// 实现用户登出
							case 4:
								oos.writeUnshared(logout(cust.getName()));
								break;
							// 实现用户修改密码
							case 5:
								oos.writeUnshared(modify(cust.getName(), cust));
								break;
							}
							oos.reset();
						} catch (IOException e) {
							System.out.println(socket.getInetAddress() + " 用户网络失去连接!!");
							if(cust != null) {
								Customer cust_exit = CustBase.getCustBase().findCust(cust.getName());
								if(cust_exit != null && !cust_exit.getLogin()) {
									cust_exit.setLogin(false);
									System.out.println(cust_exit.getName() + " 已退出");
								}	
							}
							return;
						} catch (ClassNotFoundException e) {
							System.out.println(socket.getInetAddress() + " 类传输失败!!");
							return;
						}
					}
				}
			}).start();
		}
	}

	// 用户在服务器上登陆
	private Customer login(String name, String password) {
		synchronized (CustBase.getCustBase().getCusts()) {
			Customer cust = CustBase.getCustBase().findCust(name, password);
			if (cust != null && !cust.getLogin()) {
				cust.setLastLogin(getTime());
				try {
					CustDatabase.updateToDatabase(cust.getName(), cust, 2);
				} catch (Exception e) {
					return null;
				}
				cust.setLogin(true);
				System.out.println(cust.getName() + " 已登入");
				return cust;
			}
			return null;
		}
	}

	// 用户在服务器上登出
	private Customer logout(String name) {
		synchronized (CustBase.getCustBase().getCusts()) {
			Customer cust = CustBase.getCustBase().findCust(name);
			cust.setLogin(false);
			System.out.println(name + " 已登出");
			return cust;
		}
	}

	// 用户在服务器上注册
	// 以及检验注册时用户名是否重名
	private Customer register(Customer cust) {
		synchronized (CustBase.getCustBase().getCusts()) {
			if (CustBase.getCustBase().findCust(cust.getName()) != null) {
				return new Customer(null, null, 3);
			} else {
				if (!cust.getPassword().equals("")) {
					cust.setRegister_time(getTime());
					// 向数据库添加用户
					try {
						CustDatabase.addToDatabase(cust);
					} catch (Exception e) {
						return new Customer(null, null, 3);
					}
					// 写入用户链表中
					CustBase.getCustBase().addCust(cust);
					return new Customer(null, null, 2);
				} else {
					// 此处表示用户名不重复
					// 但并没有注册进数据库
					return new Customer(null, null, 2);
				}
			}
		}
	}

	// 用户在服务器上修改信息
	private Customer modify(String oldName, Customer cust) {
		// 修改用户名
		if (cust.getOpcode() == 0) {
			try {
				CustDatabase.updateToDatabase(oldName, cust, 0);
			} catch (Exception e) {
				return null;
			}
			CustBase.getCustBase().findCust(oldName).setName(cust.getName());
			ServerCustUI.fresh_custList();
			return new Customer(null, null, 0);
			// 修改密码
		} else if (cust.getOpcode() == 5) {
			try {
				CustDatabase.updateToDatabase(oldName, cust, 1);
			} catch (Exception e) {
				return null;
			}
			CustBase.getCustBase().findCust(oldName).setPassword(cust.getPassword());
			ServerCustUI.fresh_custList();
			return new Customer(null, null, 5);
		}

		return null;
	}

	// 获取系统当前时间
	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);

		return (year + "/" + month + "/" + day + "  " + hour + ":" + minute + ":" + second);
	}

}

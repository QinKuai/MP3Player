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
	// �������û�ͨ·������
	private ServerSocket ss;

	// �������û�ͨ·������
	public ServerCustNet(int port_cust) {
		try {
			custNet_init(port_cust);
		} catch (IOException e) {
			System.out.println("�û������ʼ��ʧ��");
			System.exit(-1);
		}
	}

	// �������û�ͨ·�����ʼ������
	private void custNet_init(int port_cust) throws IOException {
		ss = new ServerSocket(port_cust);
		System.out.println("�û�����������");

		while (true) {
			Socket s = ss.accept();

			new Thread(new Runnable() {
				Customer cust;
				Socket socket = s;
				ObjectInputStream ois;
				ObjectOutputStream oos;

				@Override
				public void run() {
					System.out.println(socket.getInetAddress() + "�û�����������");
					try {
						oos = new ObjectOutputStream(socket.getOutputStream());
						ois = new ObjectInputStream(socket.getInputStream());
					} catch (IOException e) {
						System.out.println(socket.getInetAddress() + "�û�����ʧȥ����");
						return;
					}

					while (true) {
						try {
							cust = (Customer) ois.readUnshared();
							int opcode = cust.getOpcode();
							switch (opcode) {
							// ʵ���û��޸��û���
							case 0:
								oos.writeUnshared(modify(cust.getOldName(), cust));
								break;
							// ʵ���û���½
							case 1:
								oos.writeUnshared(login(cust.getName(), cust.getPassword()));
								break;
							// ʵ���û�ע��
							case 2:
								oos.writeUnshared(register(cust));
								break;
							// ʵ���û��ǳ�
							case 4:
								oos.writeUnshared(logout(cust.getName()));
								break;
							// ʵ���û��޸�����
							case 5:
								oos.writeUnshared(modify(cust.getName(), cust));
								break;
							}
							oos.reset();
						} catch (IOException e) {
							System.out.println(socket.getInetAddress() + " �û�����ʧȥ����!!");
							if(cust != null) {
								Customer cust_exit = CustBase.getCustBase().findCust(cust.getName());
								if(cust_exit != null && !cust_exit.getLogin()) {
									cust_exit.setLogin(false);
									System.out.println(cust_exit.getName() + " ���˳�");
								}	
							}
							return;
						} catch (ClassNotFoundException e) {
							System.out.println(socket.getInetAddress() + " �ഫ��ʧ��!!");
							return;
						}
					}
				}
			}).start();
		}
	}

	// �û��ڷ������ϵ�½
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
				System.out.println(cust.getName() + " �ѵ���");
				return cust;
			}
			return null;
		}
	}

	// �û��ڷ������ϵǳ�
	private Customer logout(String name) {
		synchronized (CustBase.getCustBase().getCusts()) {
			Customer cust = CustBase.getCustBase().findCust(name);
			cust.setLogin(false);
			System.out.println(name + " �ѵǳ�");
			return cust;
		}
	}

	// �û��ڷ�������ע��
	// �Լ�����ע��ʱ�û����Ƿ�����
	private Customer register(Customer cust) {
		synchronized (CustBase.getCustBase().getCusts()) {
			if (CustBase.getCustBase().findCust(cust.getName()) != null) {
				return new Customer(null, null, 3);
			} else {
				if (!cust.getPassword().equals("")) {
					cust.setRegister_time(getTime());
					// �����ݿ�����û�
					try {
						CustDatabase.addToDatabase(cust);
					} catch (Exception e) {
						return new Customer(null, null, 3);
					}
					// д���û�������
					CustBase.getCustBase().addCust(cust);
					return new Customer(null, null, 2);
				} else {
					// �˴���ʾ�û������ظ�
					// ����û��ע������ݿ�
					return new Customer(null, null, 2);
				}
			}
		}
	}

	// �û��ڷ��������޸���Ϣ
	private Customer modify(String oldName, Customer cust) {
		// �޸��û���
		if (cust.getOpcode() == 0) {
			try {
				CustDatabase.updateToDatabase(oldName, cust, 0);
			} catch (Exception e) {
				return null;
			}
			CustBase.getCustBase().findCust(oldName).setName(cust.getName());
			ServerCustUI.fresh_custList();
			return new Customer(null, null, 0);
			// �޸�����
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

	// ��ȡϵͳ��ǰʱ��
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

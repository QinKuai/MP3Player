package cilent.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import domain.Customer;

public class CilentCustNet {
	// �������û�ͨ·�Ķ˿�
	private int port;
	// ��������IP��ַ
	private String address;
	// �û����������������
	private Socket socket;
	// ͨ��������ͻ�������
	private ObjectInputStream ois;
	// ͨ����������������
	private ObjectOutputStream oos;

	// �ͻ����������ӹ�����
	public CilentCustNet(String address, int port) {
		this.address = address;
		this.port = port;

		try {
			CustNet_init();
		} catch (IOException e) {
		}
	}

	// �����ʼ������
	public void CustNet_init() throws IOException {
		// ��������
		socket = new Socket(address, port);
		// ���������
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}

	// �ͻ����������Ѱ��ע��
	public void register_cust(String username, String password) {
		if (this.socket.isConnected()) {
			Customer cust = new Customer(username, password, 2);
			try {
				oos.writeUnshared(cust);
				oos.reset();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "No Connection!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ����������ע�����
	public boolean register_back() {
		Customer cust_back = null;
		try {
			cust_back = (Customer) ois.readUnshared();
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Connection Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Connection Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		}

		return (cust_back.getOpcode() == 2) ? true : false;
	}

	// �ͻ����������Ѱ���½
	public void login_cust(String username, String password) {
		if (this.socket.isConnected()) {
			Customer cust = new Customer(username, password, 1);
			try {
				oos.writeUnshared(cust);
				oos.reset();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Connection Error!!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "No Connection!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ���������ص�½���
	public Customer login_back() {
		Customer cust_back = null;
		try {
			cust_back = (Customer) ois.readUnshared();
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Connection Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Connection Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		}

		return cust_back;
	}

	// �ͻ����������Ѱ��ǳ�
	public void logout_cust(String username, String password) {
		if (this.socket.isConnected()) {
			Customer cust = new Customer(username, password, 4);
			try {
				oos.writeUnshared(cust);
				oos.reset();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ���������صǳ����
	public Customer logout_back() {
		Customer cust_back = null;
		try {
			cust_back = (Customer) ois.readUnshared();
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		}

		return cust_back;
	}

	// �ͻ����������Ѱ���޸�
	public void modify_cust(String username, String password, String oldName, int modify_code) {
		if (this.socket.isConnected()) {
			Customer cust = new Customer(username, password, modify_code);
			cust.setOldName(oldName);
			try {
				oos.writeUnshared(cust);
				oos.reset();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Network isn't connected!!", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// �����������޸����
	public Customer modify_back() {
		Customer cust_back = null;
		try {
			cust_back = (Customer) ois.readUnshared();
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Network Error!!", "Error", JOptionPane.ERROR_MESSAGE);
		}

		return cust_back;
	}

	// ���ص�ǰ����������
	public Socket getSocket() {
		return this.socket;
	}
}

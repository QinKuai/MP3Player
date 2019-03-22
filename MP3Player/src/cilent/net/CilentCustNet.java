package cilent.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import domain.Customer;

public class CilentCustNet {
	// 服务器用户通路的端口
	private int port;
	// 服务器的IP地址
	private String address;
	// 用户端与服务器的连接
	private Socket socket;
	// 通过网络向客户端输入
	private ObjectInputStream ois;
	// 通过网络向服务器输出
	private ObjectOutputStream oos;

	// 客户端网络连接构造器
	public CilentCustNet(String address, int port) {
		this.address = address;
		this.port = port;

		try {
			CustNet_init();
		} catch (IOException e) {
		}
	}

	// 网络初始化方法
	public void CustNet_init() throws IOException {
		// 网络连接
		socket = new Socket(address, port);
		// 输入输出流
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}

	// 客户端向服务器寻求注册
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

	// 服务器返回注册情况
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

	// 客户端向服务器寻求登陆
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

	// 服务器返回登陆情况
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

	// 客户端向服务器寻求登出
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

	// 服务器返回登出情况
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

	// 客户端向服务器寻求修改
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

	// 服务器返回修改情况
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

	// 返回当前的网络连接
	public Socket getSocket() {
		return this.socket;
	}
}

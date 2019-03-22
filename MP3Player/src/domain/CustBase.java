package domain;

import java.util.ArrayList;

public class CustBase {
	// 用户库的单实例
	private static CustBase custInstance = null;
	// 所有用户的表
	private ArrayList<Customer> customers;

	// 私有构造器
	private CustBase() {
		customers = new ArrayList<Customer>();
	}

	// 获得实例的方法
	public static CustBase getCustBase() {
		if (custInstance == null) {
			custInstance = new CustBase();
		}

		return custInstance;
	}

	// 返回当前的用户列表
	public ArrayList<Customer> getCusts() {
		return customers;
	}

	// 通过用户名和密码向用户列表里添加用户
	public void addCust(String name, String password) {
		customers.add(new Customer(name, password));
	}

	// 直接添加用户对象
	public void addCust(Customer cust) {
		customers.add(cust);
	}

	// 通过用户名找寻用户对象
	public Customer findCust(String name) {
		for (Customer cust : customers) {
			if (cust.getName().equals(name)) {
				return cust;
			}
		}
		return null;
	}

	// 通过用户名和密码找寻用户对象
	public Customer findCust(String name, String password) {
		for (Customer cust : customers) {
			if (cust.getName().equals(name) && cust.getPassword().equals(password)) {
				return cust;
			}
		}
		return null;
	}

}

package domain;

import java.util.ArrayList;

public class CustBase {
	// �û���ĵ�ʵ��
	private static CustBase custInstance = null;
	// �����û��ı�
	private ArrayList<Customer> customers;

	// ˽�й�����
	private CustBase() {
		customers = new ArrayList<Customer>();
	}

	// ���ʵ���ķ���
	public static CustBase getCustBase() {
		if (custInstance == null) {
			custInstance = new CustBase();
		}

		return custInstance;
	}

	// ���ص�ǰ���û��б�
	public ArrayList<Customer> getCusts() {
		return customers;
	}

	// ͨ���û������������û��б�������û�
	public void addCust(String name, String password) {
		customers.add(new Customer(name, password));
	}

	// ֱ������û�����
	public void addCust(Customer cust) {
		customers.add(cust);
	}

	// ͨ���û�����Ѱ�û�����
	public Customer findCust(String name) {
		for (Customer cust : customers) {
			if (cust.getName().equals(name)) {
				return cust;
			}
		}
		return null;
	}

	// ͨ���û�����������Ѱ�û�����
	public Customer findCust(String name, String password) {
		for (Customer cust : customers) {
			if (cust.getName().equals(name) && cust.getPassword().equals(password)) {
				return cust;
			}
		}
		return null;
	}

}

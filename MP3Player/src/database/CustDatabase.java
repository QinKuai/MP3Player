package database;

import java.sql.Statement;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

import domain.CustBase;
import domain.Customer;

public class CustDatabase {
	// ���ݿ�����
	private static String driver;
	// ���ݿ�URL
	private static String cust_url;
	// �û���
	private static String user;
	// ����
	private static String password;

	// ʵ�ִ����ݿ��ж�ȡ�����û���Ϣ
	public static void DatabaseToCusts() throws Exception {
		// ִ�б������ݿ��SQL���
		// ��ʵ�ֽ����ݿ�������û�����ת����������
		initParam("./src/database/res/mysql.ini");
		String sql = "select * from customer_data";
		Class.forName(driver);

		try (Connection connection = DriverManager.getConnection(cust_url, user, password);
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql)) {
			while (rs.next()) {
				Customer cust = new Customer(rs.getString(1), rs.getString(2));
				cust.setRegister_time(rs.getString(3));
				cust.setLastLogin(rs.getString(4));
				CustBase.getCustBase().addCust(cust);
			}
		}

		System.out.println("�û����ݿ��ȡ��ɣ���");
	}

	// ʵ�������ݿ������û���Ϣ
	public static void addToDatabase(Customer cust) throws Exception {
		Class.forName(driver);

		try (Connection connection = DriverManager.getConnection(cust_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("insert into customer_data(cust_id, cust_password, cust_register_time,"
					+ " cust_login_time_latest_time)" + " values('" + cust.getName() + "', '" + cust.getPassword()
					+ "', '" + cust.getRegister_time() + "', '');");
		}

		System.out.println(cust.getName() + " �˻���ӳɹ�");
	}

	// ʵ�������ݿ�ɾ���û���Ϣ
	public static void deleteToDatabase(Customer cust) throws Exception {
		Class.forName(driver);

		try (Connection connection = DriverManager.getConnection(cust_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("delete from customer_data " + "where cust_id = '" + cust.getName() + "';");
		}

		System.out.println(cust.getName() + " �˻�ɾ���ɹ�");
	}

	// ʵ�������ݿ�����û���Ϣ
	public static void updateToDatabase(String oldName, Customer newCust, int opcode) throws Exception {
		Class.forName(driver);
		// �޸��û���Ϣ��SQL���
		// 0 -> �޸��û���
		// 1 -> �޸�����
		// 2 -> �޸������¼ʱ��
		String sql = "update customer_data ";
		sql += (opcode == 0) ? "set cust_id = '" + newCust.getName() + "'"
				: (opcode == 1) ? "set cust_password = '" + newCust.getPassword() + "'"
						: "set cust_login_time_latest_time = '" + newCust.getLastLogin() + "'";
		sql += "where cust_id = '" + oldName + "'";
		try (Connection connection = DriverManager.getConnection(cust_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate(sql);
		}

		System.out.println(newCust.getName() + "�û���Ϣ�޸ĳɹ�");
	}

	//

	public static void initParam(String paramFile) throws Exception {
		// ���������ļ�
		Properties props = new Properties();
		props.load(new FileInputStream(paramFile));
		driver = props.getProperty("driver");
		cust_url = props.getProperty("cust_url");
		user = props.getProperty("user");
		password = props.getProperty("password");
	}

}

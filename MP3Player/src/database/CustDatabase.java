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
	// 数据库驱动
	private static String driver;
	// 数据库URL
	private static String cust_url;
	// 用户名
	private static String user;
	// 密码
	private static String password;

	// 实现从数据库中读取所有用户信息
	public static void DatabaseToCusts() throws Exception {
		// 执行遍历数据库的SQL语句
		// 以实现将数据库的所有用户数据转化到链表中
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

		System.out.println("用户数据库读取完成！！");
	}

	// 实现向数据库新增用户信息
	public static void addToDatabase(Customer cust) throws Exception {
		Class.forName(driver);

		try (Connection connection = DriverManager.getConnection(cust_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("insert into customer_data(cust_id, cust_password, cust_register_time,"
					+ " cust_login_time_latest_time)" + " values('" + cust.getName() + "', '" + cust.getPassword()
					+ "', '" + cust.getRegister_time() + "', '');");
		}

		System.out.println(cust.getName() + " 账户添加成功");
	}

	// 实现向数据库删除用户信息
	public static void deleteToDatabase(Customer cust) throws Exception {
		Class.forName(driver);

		try (Connection connection = DriverManager.getConnection(cust_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("delete from customer_data " + "where cust_id = '" + cust.getName() + "';");
		}

		System.out.println(cust.getName() + " 账户删除成功");
	}

	// 实现向数据库更改用户信息
	public static void updateToDatabase(String oldName, Customer newCust, int opcode) throws Exception {
		Class.forName(driver);
		// 修改用户信息的SQL语句
		// 0 -> 修改用户名
		// 1 -> 修改密码
		// 2 -> 修改最近登录时间
		String sql = "update customer_data ";
		sql += (opcode == 0) ? "set cust_id = '" + newCust.getName() + "'"
				: (opcode == 1) ? "set cust_password = '" + newCust.getPassword() + "'"
						: "set cust_login_time_latest_time = '" + newCust.getLastLogin() + "'";
		sql += "where cust_id = '" + oldName + "'";
		try (Connection connection = DriverManager.getConnection(cust_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate(sql);
		}

		System.out.println(newCust.getName() + "用户信息修改成功");
	}

	//

	public static void initParam(String paramFile) throws Exception {
		// 加载属性文件
		Properties props = new Properties();
		props.load(new FileInputStream(paramFile));
		driver = props.getProperty("driver");
		cust_url = props.getProperty("cust_url");
		user = props.getProperty("user");
		password = props.getProperty("password");
	}

}

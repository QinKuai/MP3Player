package database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import domain.Music;
import domain.MusicBase;

public class MP3Database {
	// 数据库驱动
	private static String driver;
	// 数据库URL
	private static String music_url;
	// 用户名
	private static String user;
	// 密码
	private static String password;
	
	// 实现从数据库中读取所有用户信息
	public static void DatabaseToMusics() throws Exception {
		// 执行遍历数据库的SQL语句
		// 以实现将数据库的所有用户数据转化到链表中
		initParam("./src/database/res/mysql.ini");
		String sql = "select * from music_data";
		Class.forName(driver);

		try (Connection connection = DriverManager.getConnection(music_url, user, password);
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql)) {
			while (rs.next()) {
				MusicBase.getMusicBase().addMusic(rs.getString(1), rs.getString(2));
			}
		}

		System.out.println("音乐数据库读取完成！！");
	}
	
	//向数据库添加音乐文件
	public static void addToDatabase(Music music) throws Exception{
		Class.forName(driver);
		
		try (Connection connection = DriverManager.getConnection(music_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("insert into music_data(music_name, directory)" 
				+ " values('" + music.getMusicName() + "', '" + music.getDirectory()
					+ "');");
		}

		System.out.println(music.getMusicName() + " 音乐添加成功");
	}
	
	//向数据库删除音乐文件
	public static void deleteDatabase(Music music) throws Exception{
		Class.forName(driver);
		
		try (Connection connection = DriverManager.getConnection(music_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("delete from music_data" 
				+ " where music_name = '" + music.getMusicName() + "';");
		}

		System.out.println(music.getMusicName() + " 音乐删除成功");
	}
	
	public static void initParam(String paramFile) throws Exception {
		// 加载属性文件
		Properties props = new Properties();
		props.load(new FileInputStream(paramFile));
		driver = props.getProperty("driver");
		music_url = props.getProperty("music_url");
		user = props.getProperty("user");
		password = props.getProperty("password");
	}
}

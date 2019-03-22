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
	// ���ݿ�����
	private static String driver;
	// ���ݿ�URL
	private static String music_url;
	// �û���
	private static String user;
	// ����
	private static String password;
	
	// ʵ�ִ����ݿ��ж�ȡ�����û���Ϣ
	public static void DatabaseToMusics() throws Exception {
		// ִ�б������ݿ��SQL���
		// ��ʵ�ֽ����ݿ�������û�����ת����������
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

		System.out.println("�������ݿ��ȡ��ɣ���");
	}
	
	//�����ݿ���������ļ�
	public static void addToDatabase(Music music) throws Exception{
		Class.forName(driver);
		
		try (Connection connection = DriverManager.getConnection(music_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("insert into music_data(music_name, directory)" 
				+ " values('" + music.getMusicName() + "', '" + music.getDirectory()
					+ "');");
		}

		System.out.println(music.getMusicName() + " ������ӳɹ�");
	}
	
	//�����ݿ�ɾ�������ļ�
	public static void deleteDatabase(Music music) throws Exception{
		Class.forName(driver);
		
		try (Connection connection = DriverManager.getConnection(music_url, user, password);
				Statement statement = connection.createStatement();) {
			statement.executeUpdate("delete from music_data" 
				+ " where music_name = '" + music.getMusicName() + "';");
		}

		System.out.println(music.getMusicName() + " ����ɾ���ɹ�");
	}
	
	public static void initParam(String paramFile) throws Exception {
		// ���������ļ�
		Properties props = new Properties();
		props.load(new FileInputStream(paramFile));
		driver = props.getProperty("driver");
		music_url = props.getProperty("music_url");
		user = props.getProperty("user");
		password = props.getProperty("password");
	}
}

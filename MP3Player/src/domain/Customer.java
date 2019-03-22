package domain;

import java.io.Serializable;

public class Customer implements Serializable {
	// 用户名
	private String username;
	// 密码
	private String password;
	// 确定用户的登录状态
	private boolean login = false;
	// 客户端传输的操作码
	// 对应服务端的相应操作
	// 0 -> 修改用户名
	// 1 -> 登陆
	// 2 -> 注册
	// 3 -> 注册失败
	// 4 -> 登出
	// 5 -> 修改用户密码
	private int opcode = 0;
	// 用户上一次登陆的时间
	private String lastLogin = "";
	// 用户注册的时间
	private String register_time = "";
	// 用户前一次使用的用户名
	private String oldName = "";

	public Customer(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public Customer(String username, String password, int opcode) {
		this.username = username;
		this.password = password;
		this.opcode = opcode;
	}

	// 获取该用户的用户名
	public String getName() {
		return username;
	}

	// 设置该用户用户名
	public void setName(String username) {
		this.username = username;
	}

	// 获取该用户的密码
	public String getPassword() {
		return password;
	}

	// 设置该用户的密码
	public void setPassword(String password) {
		this.password = password;
	}

	// 设置该用户的登陆状态
	public void setLogin(boolean login) {
		this.login = login;
	}

	// 获取该用户登陆状态
	public boolean getLogin() {
		return login;
	}

	// 获取该用户对象包含的操作码
	public int getOpcode() {
		return opcode;
	}

	// 获取上一次登陆的时间
	public String getLastLogin() {
		return lastLogin;
	}

	// 设置上一次登陆的时间
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	// 获取注册时间
	public String getRegister_time() {
		return register_time;
	}

	// 设置注册时间
	public void setRegister_time(String register_time) {
		this.register_time = register_time;
	}

	// 获取用户的前一次用户名
	public String getOldName() {
		return oldName;
	}

	// 设置用户的前一次用户名
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
}

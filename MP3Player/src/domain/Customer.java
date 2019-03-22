package domain;

import java.io.Serializable;

public class Customer implements Serializable {
	// �û���
	private String username;
	// ����
	private String password;
	// ȷ���û��ĵ�¼״̬
	private boolean login = false;
	// �ͻ��˴���Ĳ�����
	// ��Ӧ����˵���Ӧ����
	// 0 -> �޸��û���
	// 1 -> ��½
	// 2 -> ע��
	// 3 -> ע��ʧ��
	// 4 -> �ǳ�
	// 5 -> �޸��û�����
	private int opcode = 0;
	// �û���һ�ε�½��ʱ��
	private String lastLogin = "";
	// �û�ע���ʱ��
	private String register_time = "";
	// �û�ǰһ��ʹ�õ��û���
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

	// ��ȡ���û����û���
	public String getName() {
		return username;
	}

	// ���ø��û��û���
	public void setName(String username) {
		this.username = username;
	}

	// ��ȡ���û�������
	public String getPassword() {
		return password;
	}

	// ���ø��û�������
	public void setPassword(String password) {
		this.password = password;
	}

	// ���ø��û��ĵ�½״̬
	public void setLogin(boolean login) {
		this.login = login;
	}

	// ��ȡ���û���½״̬
	public boolean getLogin() {
		return login;
	}

	// ��ȡ���û���������Ĳ�����
	public int getOpcode() {
		return opcode;
	}

	// ��ȡ��һ�ε�½��ʱ��
	public String getLastLogin() {
		return lastLogin;
	}

	// ������һ�ε�½��ʱ��
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	// ��ȡע��ʱ��
	public String getRegister_time() {
		return register_time;
	}

	// ����ע��ʱ��
	public void setRegister_time(String register_time) {
		this.register_time = register_time;
	}

	// ��ȡ�û���ǰһ���û���
	public String getOldName() {
		return oldName;
	}

	// �����û���ǰһ���û���
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
}

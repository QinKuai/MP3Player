package cilent.net;

public class CilentNet {
	// �û�����Ķ˿�
	private int port_cust;
	// ��������Ķ˿�
	private int port_mp3;
	// �������ĵ�ַ
	private String address;
	// �û��������
	private CilentCustNet custNet;
	// �����������
	private CilentMP3Net mp3Net;

	public CilentNet(String address, int port_cust, int port_mp3) {
		this.address = address;
		this.port_cust = port_cust;
		this.port_mp3 = port_mp3;

		new Thread(new Runnable() {
			@Override
			public void run() {
				custNet = new CilentCustNet(address, port_cust);
			}
		}).start();
		mp3Net = new CilentMP3Net(address, port_mp3);
	}

	public CilentCustNet getCustNet() {
		return custNet;
	}

	public CilentMP3Net getMP3Net() {
		return mp3Net;
	}
}

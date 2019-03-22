package cilent.net;

public class CilentNet {
	// 用户网络的端口
	private int port_cust;
	// 音乐网络的端口
	private int port_mp3;
	// 服务器的地址
	private String address;
	// 用户网络对象
	private CilentCustNet custNet;
	// 音乐网络对象
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

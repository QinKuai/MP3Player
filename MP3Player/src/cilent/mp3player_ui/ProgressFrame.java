package cilent.mp3player_ui;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ProgressFrame extends JFrame{
	// 该窗口只包含一个进度条
	private JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
	
	public ProgressFrame(String directory) {
		init(directory);
	}

	private void init(String directory) {
		setLayout(null);
		
		progressBar.setStringPainted(true);
		progressBar.setBorderPainted(true);
		progressBar.setBounds(25, 10, 300, 50);
		
		JLabel musicMsg = new JLabel();
		musicMsg.setBounds(25, 70, 300, 20);
		musicMsg.setText("下载至：" + directory);
		musicMsg.setHorizontalAlignment(JLabel.CENTER);
		musicMsg.setToolTipText("文件路径：" + directory);
		
		add(progressBar);
		add(musicMsg);
		// 主页面基本设置
		// 窗口图标
		ImageIcon AppIcon = new ImageIcon("./res/image/app_icon.png");
		setIconImage(AppIcon.getImage());
		//设置标题
		setTitle("下载进度");
		// 总窗口缩紧
		setSize(new Dimension(350, 150));
		// 总窗口居中
		setLocationRelativeTo(null);
		// 总窗口自带关闭键不可用
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// 总窗口不可缩放
		setResizable(false);
		// 总窗口可见
		setVisible(true);
	}
	
	public void setValue(int value) {
		progressBar.setValue(value);
	}
	
	public void setMax(int max) {
		progressBar.setMaximum(max);
	}
}

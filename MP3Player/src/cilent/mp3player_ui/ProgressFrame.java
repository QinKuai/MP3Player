package cilent.mp3player_ui;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ProgressFrame extends JFrame{
	// �ô���ֻ����һ��������
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
		musicMsg.setText("��������" + directory);
		musicMsg.setHorizontalAlignment(JLabel.CENTER);
		musicMsg.setToolTipText("�ļ�·����" + directory);
		
		add(progressBar);
		add(musicMsg);
		// ��ҳ���������
		// ����ͼ��
		ImageIcon AppIcon = new ImageIcon("./res/image/app_icon.png");
		setIconImage(AppIcon.getImage());
		//���ñ���
		setTitle("���ؽ���");
		// �ܴ�������
		setSize(new Dimension(350, 150));
		// �ܴ��ھ���
		setLocationRelativeTo(null);
		// �ܴ����Դ��رռ�������
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// �ܴ��ڲ�������
		setResizable(false);
		// �ܴ��ڿɼ�
		setVisible(true);
	}
	
	public void setValue(int value) {
		progressBar.setValue(value);
	}
	
	public void setMax(int max) {
		progressBar.setMaximum(max);
	}
}

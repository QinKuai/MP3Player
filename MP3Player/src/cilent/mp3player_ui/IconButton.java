package cilent.mp3player_ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class IconButton extends JButton {

	private static final long serialVersionUID = 1L;
	private boolean music_status = false;

	public IconButton(ImageIcon icon) {
		super();
		button_init(icon);
	}

	private void button_init(ImageIcon icon) {
		setPreferredSize(new Dimension(50, 50));
		setIcon(icon);
		setContentAreaFilled(false);
		setFocusable(false);
	}

	public boolean getMusic_Status() {
		return music_status;
	}

	public void setMusic_Status(boolean status) {
		music_status = status;
	}

	// 实现该类型按钮的鼠标变化
	public void setHandCursor() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		});
	}
}

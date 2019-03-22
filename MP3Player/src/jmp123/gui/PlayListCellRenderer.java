package jmp123.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * 涓�у寲鏄剧ず鎾斁鍒楄〃椤�
 */
@SuppressWarnings("serial")
class PlayListCellRenderer extends JLabel implements ListCellRenderer {
	private Font selFont;
	private Font plainFont;
	private Color selectionColor, foregroundColor, curColor;

	public PlayListCellRenderer() {
		super();
		int fontSize = getFont().getSize();
		String fontName = getFont().getFontName();
		selFont = new Font(fontName, Font.BOLD, 5 * fontSize / 4);
		plainFont = new Font(fontName, Font.PLAIN, fontSize);
		selectionColor = Color.white;
		foregroundColor = Color.black;
		curColor = Color.blue;
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		PlayListItem item = (PlayListItem) value;
		int curIndex = ((PlayList) list).getCurrentIndex();

		// 璁剧疆鍗曞厓鏍硷細 搴忓彿+鏍囬+鑹烘湳瀹�
		StringBuilder sbuf = new StringBuilder();
		sbuf.append(index + 1);
		sbuf.append(". ");
		String fullName = item.toString();
		String title = fullName.split(".mp3")[0];
		String musicName = title.split("_")[0];
		String authorName = title.split("_")[1];
		sbuf.append(musicName);
		sbuf.append("    " + authorName);

		setText(sbuf.toString());

		if (index == curIndex) {
			setForeground(curColor);
			setFont(selFont);
		} else if (isSelected) {
			setForeground(selectionColor);
			setFont(selFont);
		} else if (item.available() == false) {
			setForeground(Color.lightGray);
			setFont(plainFont);
		} else {
			setForeground(foregroundColor);
			setFont(plainFont);
		}

		return this;
	}
}

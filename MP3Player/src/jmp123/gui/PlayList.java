package jmp123.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import cilent.mp3player_ui.CilentMainUI;

@SuppressWarnings("serial")
public class PlayList extends JList {
	private Image bkImage;
	private DefaultListModel dataListModel;
	private int curIndex = -1; //褰撳墠姝ｅ湪鎾斁鐨勬枃浠�
	private int nextIndex; //涓嬩竴鎾斁鐨勬枃浠�

	public PlayList() {
		super();
		//bkImage = new ImageIcon(getClass().getResource("resources/image/bk2.jpg")).getImage();
		dataListModel = new DefaultListModel();
		setModel(dataListModel);
		int fontSize = getFont().getSize();
		setFixedCellHeight(3 * fontSize / 2);

		setCellRenderer(new PlayListCellRenderer());
		setOpaque(false); // 鏂囧瓧鑳屾櫙閫忔槑
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			public void valueChanged(javax.swing.event.ListSelectionEvent e) {
				// 鍨傜洿婊氬姩鏉¤嚜鍔ㄦ粴鍔�
				ensureIndexIsVisible(getSelectedIndex());
			}
		});
	}

	public void paint(Graphics g) {
		g.drawImage(bkImage, 0 - getX(), 0 - getY(), null); // 浣胯儗鏅綅缃浐瀹�
		super.paint(g);
	}

	public synchronized int getCount() {
		return dataListModel.getSize();
	}

	public synchronized void append(String title, String path) {
		dataListModel.addElement(new PlayListItem(title, path));
	}

	/**
	 * 鑾峰彇鎸囧畾鐨勫垪琛ㄩ」銆�
	 * @param index 鍒楄〃椤圭殑绱㈠紩銆�
	 * @return 鍒楄〃椤广��
	 */
	public synchronized PlayListItem getPlayListItem(int index) {
		return (PlayListItem) dataListModel.get(index);
	}

	/**
	 * 浠庡垪琛ㄤ腑鍒犻櫎鎸囧畾椤广��
	 * @param index 灏嗚鍒犻櫎鐨勫垪琛ㄩ」鐨勭储寮曘��
	 */
	public synchronized void removeItem(int index) {
		if(index < 0 || index >= dataListModel.getSize())
			return;
		dataListModel.remove(index);
		if(index == curIndex)
			curIndex = -1;

		if(index >= dataListModel.getSize())
			index = 0;
		nextIndex = index;
		setSelectedIndex(index);
	}

	/**
	 * 娓呯┖鍒楄〃銆�
	 */
	public synchronized void clear() {
		nextIndex = 0;
		curIndex = -1;
		dataListModel.clear();
	}

	/**
	 * 鑾峰彇褰撳墠姝ｅ湪鎾斁鐨勬枃浠剁殑鍒楄〃绱㈠紩銆�
	 * @return 褰撳墠姝ｅ湪鎾斁鐨勬枃浠剁殑鍒楄〃绱㈠紩銆�
	 */
	public synchronized int getCurrentIndex() {
		return curIndex;
	}

	/**
	 * 鑾峰彇涓嬩竴涓彲鐢ㄧ殑鍒楄〃绱㈠紩銆�
	 * 
	 * @return 涓�涓彲鐢ㄧ殑鍒楄〃绱㈠紩銆傚綋鍒楄〃涓殑鏂囦欢鍏ㄩ儴涓嶅彲鐢ㄦ椂杩斿洖鍊间负-1銆�
	 */
	public synchronized int getNextIndex() {
		int i, count = dataListModel.getSize();
		if (nextIndex == -1) {
			if (!CilentMainUI.getPlay_model().getMusic_Status()) {
				curIndex = (curIndex + 1 == count) ? 0 : curIndex + 1;
			}
		}
		else {
			curIndex = nextIndex;
			nextIndex = -1;
		}

		for (i = 0; i < count; i++) {
			PlayListItem item = (PlayListItem) dataListModel.get(curIndex);
			if (item.available()) {
				repaint();
				return curIndex;
			}
			curIndex = (curIndex + 1 == count) ? 0 : curIndex+1;
		}
		return -1;
	}

	/**
	 * 璁剧疆涓嬩竴涓嵆灏嗚鎾斁鐨勬枃浠躲�傚悓鏃惰涓柇褰撳墠鎾斁鐨勬枃浠舵椂璋冪敤姝ゆ柟娉曘��
	 * @param i 涓嬩竴涓嵆灏嗚鎾斁鐨勬枃浠剁储寮曘��
	 */
	public synchronized void setNextIndex(int i) {
		nextIndex = (i < 0 || i >= dataListModel.getSize()) ? 0 : i;
	}

	/**
	 * 鎾斁褰撳墠鏂囦欢鏃舵槸鍚﹁鐢ㄦ埛璋冪敤 {@link #setNextIndex(int)} 鏂规硶涓柇銆�
	 * @return 杩斿洖<b>true</b>琛ㄧず鎾斁褰撳墠鏂囦欢鏃舵槸鍚﹁鐢ㄦ埛涓柇锛屽惁鍒欒繑鍥�<b>false</b>銆�
	 */
	public synchronized boolean isInterrupted() {
		return nextIndex != -1;
	}

	/**
	 * 浠庢挱鏀惧垪琛�(*.m3u)鏂囦欢娣诲姞鍒板垪琛ㄣ�傚鏋滄枃浠惰鍙栨垚鍔燂紝鍏堟竻绌哄垪琛ㄥ啀娣诲姞銆�
	 * @param name 鎾斁鍒楄〃鏂囦欢鍚嶃��
	 */
	public void openM3U(String name) {
		BufferedReader br = null;
		java.io.InputStream instream = null;
		int idx;
		StringBuilder info = new StringBuilder("[open M3U] ");
		info.append(name);
		try {
			// 鎵撳紑浠TF-8鏍煎紡缂栫爜鐨勬挱鏀惧垪琛ㄦ枃浠�
			if (name.toLowerCase().startsWith("http://")) {
				URL url = new URL(name);
				HttpURLConnection huc = (HttpURLConnection) url.openConnection();
				huc.setConnectTimeout(5000);
				huc.setReadTimeout(10000);
				instream = huc.getInputStream();
			} else
				instream = new FileInputStream(name);
			br = new BufferedReader(new InputStreamReader(instream,"utf-8"));

			String path, title = br.readLine();
			// BOM: 0xfeff
			if(!"#EXTM3U".equals(title) && !"\ufeff#EXTM3U".equals(title)) {
				info.append("\nIllegal file format.");
				return;
			}
			clear();
			while ((title = br.readLine()) != null && (path = br.readLine()) != null) {
				if (!title.startsWith("#EXTINF")
						|| (idx = title.indexOf(',') + 1) == 0) {
					info.append("\nIllegal file format.");
					break;
				}
				this.append(title.substring(idx), path);
			}
			info.append("\n");
			info.append(getCount());
			info.append(" items");
		} catch (IOException e) {
			info.append("\nfalse: ");
			info.append(e.getMessage());
		} finally {
			try {
				if(instream != null)
					instream.close();
				if (br != null)
					br.close();
			} catch (IOException e) {
			}
			System.out.println(info.toString());
		}
	}

	/**
	 * 淇濆瓨鎾斁鍒楄〃(*.m3u)
	 * @param currentDirectory 褰撳墠鐩綍銆傚彲浠ユ寚瀹氫负null銆�
	 * @param description 鎾斁鍒楄〃鏂囦欢绫诲瀷绠�鐭弿杩般��
	 * @param message 濡傛灉鎸囧畾鐨勬枃浠跺凡缁忓瓨鍦紝鏄剧ず鐨勯敊璇璇濊瘬涓婄殑鎻忚堪淇℃伅銆�
	 * @return 褰撳墠鐩綍銆�
	 */
	public synchronized File saveM3U(File currentDirectory, String description,
			String message) {
		if (getCount() == 0) {
			JOptionPane.showMessageDialog(this.getParent(),
					"The current playlist is empty.", "jmp123 - Save playlist",
					JOptionPane.INFORMATION_MESSAGE);
			return currentDirectory;
		}

		JFileChooser jfc = new JFileChooser();
		jfc.removeChoosableFileFilter(jfc.getChoosableFileFilters()[0]);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(description, "m3u");
		jfc.addChoosableFileFilter(filter);
		jfc.setCurrentDirectory(currentDirectory);
		if (jfc.showSaveDialog(this.getParent()) == JFileChooser.APPROVE_OPTION) {
			java.io.File selectedFile = jfc.getSelectedFile();
			String path = selectedFile.getAbsolutePath();
			if (!path.toLowerCase().endsWith(".m3u"))
				path += ".m3u";
			if (selectedFile.exists()
					&& JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(
							this.getParent(), message,
							"jmp123 - Save playlist", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE))
				return currentDirectory;
			try {
				StringBuilder content = new StringBuilder("#EXTM3U\r\n");
				int i, j = getCount();
				for (i = 0; i < j; i++) {
					PlayListItem item = (PlayListItem) dataListModel.get(i);
					//if(!item.available()) continue;

					// title
					content.append("#EXTINF:-1,");
					content.append(item.toString());
					content.append("\r\n");
					
					// path
					content.append(item.getPath());
					content.append("\r\n");
				}
				
				// 浠TF-8缂栫爜鏍煎紡淇濆瓨鑷�.m3u鏂囦欢
				FileOutputStream fos = new FileOutputStream(path);
				Writer fw = new java.io.OutputStreamWriter(fos, "UTF-8");
				fw.write(content.toString());
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			currentDirectory = jfc.getCurrentDirectory();
			System.out.println("[Save as M3U] file.encoding: UTF-8");
		}
		return currentDirectory;
	}

}

package jmp123.gui;

import java.io.IOException;

import cilent.mp3player_ui.CilentMainUI;
import jmp123.gui.PlayBack;
import jmp123.decoder.IAudio;

public class PlayListThread extends Thread {
	private volatile boolean interrupted;
	private PlayBack playback;
	private PlayList playlist;
	//附加内容
	//便于在频谱窗口输出当前音乐信息
	private static String musicName = "";
	private static String headerMsg = "";

	public PlayListThread(PlayList playlist, IAudio audio) {
		this.playlist = playlist;
		playback = new PlayBack(audio);
		setName("playlist_thread");
	}

	public synchronized void pause() {
		playback.pause();
	}

	public synchronized void playNext() {
		playlist.setNextIndex(playlist.getCurrentIndex() + 1);
		playback.stop();
	}

	public synchronized void playBack() {
		playlist.setNextIndex(playlist.getCurrentIndex() - 1);
		playback.stop();
	}
	
	// 鍙屽嚮鍚庢挱鏀惧垪琛ㄦ煇涓�鏉＄洰鍚庤璋冪敤
	public synchronized void startPlay(int idx) {
		playlist.setNextIndex(idx);
		playback.stop();
	}

	/**
	 * 缁堟姝ゆ挱鏀剧嚎绋嬨��
	 */
	public synchronized void interrupt() {
		interrupted = true;
		super.interrupt();
		playback.stop();
	}

	public PlayList getPlayList() {
		return playlist;
	}

	public void removeSelectedItem() {
		playlist.removeItem(playlist.getSelectedIndex());
	}
	
	//添加
	public void removeAllItems() {
		playlist.clear();
	}

	@Override
	public void run() {
		Runtime rt = Runtime.getRuntime();
		String filename;
		int curIndex;
		float freeMemory, totalMemory; // VM

		while (!interrupted) {
			if((curIndex = playlist.getNextIndex()) == -1)
				break;

//			freeMemory = (float) rt.freeMemory();
//			totalMemory = (float) rt.totalMemory();
//			System.out.printf("\nMemory used: %dK [allocated %dK]\n",
//					(int) ((totalMemory - freeMemory) / 1024),
//					(int) (totalMemory / 1024));
			
			playlist.setSelectedIndex(curIndex);
			PlayListItem item = playlist.getPlayListItem(curIndex);
			filename = item.getPath();
//			System.out.println(item.toString());//##
//			System.out.println(filename);//##
			musicName = item.toString();

			try {
				if (playback.open(filename, item.toString())) {
					//playback.getID3Tag().printTag();
					headerMsg = playback.getHeader().printHeaderInfo();
					playback.start(false);
					if (!CilentMainUI.getPlay_pause().getMusic_Status()) {
						//如果列表选曲播放时
						//播放按钮为暂停阶段则表换为播放
						CilentMainUI.play_pause();
					}
				} else
					item.enable(playlist.isInterrupted());
			} catch (IOException e) {
				// 如果打开网络文件时被用户中断，会抛出异常。
				System.out.println(e.toString());
				item.enable(playlist.isInterrupted());
			} finally {
				playback.close();
			}
		}
		//System.out.println("jmp123.gui.PlayListThread.run() ret.");
	}
	
	//附加
	public static String getMusicName() {
		return musicName;
	}
	
	//附加
	public static String getHeaderMsg() {
		return headerMsg;
	}

}



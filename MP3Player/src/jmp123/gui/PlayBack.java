/*
 * PlayBack.java -- 鎾斁涓?涓枃浠?
 * Copyright (C) 2011
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you would like to negotiate alternate licensing terms, you may do
 * so by contacting the author: <http://jmp123.sourceforge.net/>
 */

package jmp123.gui;

import java.io.IOException;
import jmp123.decoder.Header;
import jmp123.decoder.IAudio;
import jmp123.decoder.ID3Tag;
import jmp123.decoder.Layer123;
import jmp123.decoder.Layer1;
import jmp123.decoder.Layer2;
import jmp123.decoder.Layer3;
import jmp123.instream.BuffRandReadFile;
import jmp123.instream.BuffRandReadURL;
import jmp123.instream.RandomRead;
import jmp123.instream.MultiplexAudio;

/**
 * 鎾斁涓?涓枃浠跺強鎾斁鏃舵殏鍋滅瓑鎺у埗銆傜敤PlayBack鎾斁涓?涓枃浠剁殑姝ラ涓猴細
 * <ol>
 * <li>鐢ㄦ瀯閫犲櫒 {@link #PlayBack(IAudio)} 鍒涘缓涓?涓狿layBack瀵硅薄锛?</li>
 * <li>璋冪敤PlayBack瀵硅薄鐨? {@link #open(String, String)} 鎵撳紑婧愭枃浠讹紱</li>
 * <ul>
 * <li>鍙互璋冪敤PlayBack瀵硅薄鐨? {@link #getHeader()} 鏂规硶鑾峰彇 {@link jmp123.decoder.Header} 瀵硅薄锛?</li>
 * <li>鍙互璋冪敤PlayBack瀵硅薄鐨? {@link #getID3Tag()} 鏂规硶鑾峰彇 {@link jmp123.decoder.ID3Tag} 瀵硅薄锛?</li>
 * </ul>
 * <li>璋冪敤PlayBack瀵硅薄鐨? {@link #start(boolean)} 寮?濮嬫挱鏀撅紱</li>
 * <ul>
 * <li>鍙皟鐢≒layBack瀵硅薄鐨? {@link #pause()} 鏂规硶鎺у埗鎾斁鏆傚仠鎴栫户缁紱</li>
 * <li>鍙皟鐢≒layBack瀵硅薄鐨? {@link #stop()} 鏂规硶缁堟鎾斁锛?</li>
 * </ul>
 * <li>鎾斁瀹屼竴涓枃浠讹紝璋冪敤PlayBack瀵硅薄鐨? {@link #close()} 浣滃繀瑕佺殑娓呯悊銆?</li>
 * </ol>
 * 
 */
public class PlayBack {
	private byte[] buf;
	private final int BUFLEN = 8192;
	private boolean eof, paused;
	private RandomRead instream;
	private ID3Tag id3tag;
	private int off, maxOff;
	private Header header;
	private IAudio audio;

	/**
	 * 鐢ㄦ寚瀹氱殑闊抽杈撳嚭瀵硅薄鏋勯?犱竴涓狿layBack瀵硅薄銆?
	 * 
	 * @param audio
	 *            鎸囧畾鐨勯煶棰戣緭鍑? {@link jmp123.decoder.IAudio} 瀵硅薄銆傝嫢鎸囧畾涓? <b>null</b> 鍒欏彧瑙ｇ爜涓嶆挱鏀捐緭鍑恒??
	 * @see jmp123.output.Audio
	 */
	public PlayBack(IAudio audio) {
		this.audio = audio;
		header = new Header();
		id3tag = new ID3Tag();
		buf = new byte[BUFLEN];
	}

	/**
	 * 鏆傚仠鎴栫户缁鏂囦欢鎾斁銆傝繖鐩稿綋浜庝竴涓崟绋虫?佺殑瑙﹀彂寮?鍏筹紝绗竴娆¤皟鐢ㄨ鏂规硶鏆傚仠鎾斁锛岀浜屾璋冪敤缁х画鎾斁锛屼互姝ょ被鎺ㄣ??
	 * @return 杩斿洖褰撳墠鐘舵?併?傚浜庢殏鍋滅姸鎬佽繑鍥瀟rue锛屽惁鍒欒繑鍥瀎alse銆?
	 */
	public boolean pause() {
		audio.start(paused);

		if (paused) {
			synchronized (this) {
				notify();
			}
		}
		paused = !paused;

		return paused;
	}

	/**
	 * 缁堟姝ゆ枃浠舵挱鏀俱??
	 */
	public void stop() {
		eof = true;
		synchronized (this) {
			notify();
		}

		if (instream != null)
			instream.close();
	}

	/**
	 * 鍏抽棴姝ゆ枃浠舵挱鏀惧苟娓呴櫎鍏宠仈鐨勮祫婧愩??
	 */
	public void close() {
		if (id3tag != null)
			id3tag.clear();
		if (audio != null)
			audio.close();

		// 鑻ユ璇诲彇缃戠粶鏂囦欢閫氳繃璋冪敤close鏂规硶涓柇涓嬭浇(缂撳啿)
		if (instream != null)
			instream.close();
		//System.out.println("jmp123.PlayBack.close() ret.");
	}

	/**
	 * 鎵撳紑鏂囦欢骞惰В鏋愭枃浠朵俊鎭??
	 * 
	 * @param name
	 *            鏂囦欢璺緞銆?
	 * @param title
	 *            姝屾洸鏍囬锛屽彲浠ヤ负null銆?
	 * @return 鎵撳紑澶辫触杩斿洖 <b>false</b>锛涘惁鍒欒繑鍥? <b>true</b> 銆?
	 * @throws IOException 鍙戠敓I/O閿欒銆?
	 */
	public boolean open(String name, String title) throws IOException {
		maxOff = off = 0;
		paused = eof = false;

		boolean id3v1 = false;
		String str = name.toLowerCase();
		if (str.startsWith("http://") && str.endsWith(".mp3")) {
			instream = new BuffRandReadURL(audio);
		} else if (str.endsWith(".mp3")) {
			instream = new BuffRandReadFile();
			id3v1 = true;
		} else if (str.endsWith(".dat") || str.endsWith(".vob")) {
			instream = new MultiplexAudio();
		} else {
			System.err.println("Invalid File Format.");
			return false;
		}

		if (instream.open(name, title) == false)
			return false;

		int tagSize = parseTag(id3v1);
		if (tagSize == -1)
			return false;

		// 鍒濆鍖杊eader. 璁剧疆鏂囦欢鐨勯煶涔愭暟鎹暱搴?,鐢ㄤ簬CBR鏍煎紡鐨勬枃浠惰绠楀抚鏁?
		header.initialize(instream.length() - tagSize, instream.getDuration());

		// 瀹氫綅鍒扮涓?甯у苟瀹屾垚甯уご瑙ｇ爜
		nextHeader();
		if (eof)
			return false;

		if (audio != null && title != null) {
			// 姝屾洸鐨勬爣棰樺拰鑹烘湳瀹讹紝浼樺厛浣跨敤鎾斁鍒楄〃(*.m3u)涓寚瀹氱殑鍙傛暟
			String[] strArray = title.split(" ");
			if (strArray.length >= 2) {
				// if (id3Tag.getTitle() == null)
				id3tag.settTitle(strArray[0]);
				// if (id3Tag.getArtist() == null)
				id3tag.settArtist(strArray[1]);

				/*StringBuilder strbuilder = new StringBuilder();
				strbuilder.append(id3Tag.getTitle());
				strbuilder.append('@');
				strbuilder.append(id3Tag.getArtist());
				audio.refreshMessage(strbuilder.toString());*/
			}
		}

		// 鎴愬姛瑙ｆ瀽甯уご鍚庡垵濮嬪寲闊抽杈撳嚭
		if (audio != null && audio.open(header, id3tag.getArtist()) == false)
			return false;

		return true;
	}

	private int parseTag(boolean id3v1) throws IOException {
		int tagSize = 0;

		// ID3 v1
		if (id3v1 && instream.seek(instream.length() - 128 - 32)) {
			if (instream.read(buf, 0, 128 + 32) == 128 + 32) {
				if (id3tag.checkID3V1(buf, 32)) {
					tagSize = 128;
					id3tag.parseID3V1(buf, 32);
				}
			} else
				return -1;
			instream.seek(0);
			tagSize += id3tag.checkAPEtagFooter(buf, 0); // APE tag footer
		}

		if ((maxOff = instream.read(buf, 0, BUFLEN)) <= 10) {
			eof = true;
			return -1;
		}

		// ID3 v2
		int sizev2 = id3tag.checkID3V2(buf, 0);
		tagSize += sizev2;
		if (sizev2 > maxOff) {
			byte[] b = new byte[sizev2];
			System.arraycopy(buf, 0, b, 0, maxOff);
			sizev2 -= maxOff;
			if ((maxOff = instream.read(b, maxOff, sizev2)) < sizev2) {
				eof = true;
				return -1;
			}
			id3tag.parseID3V2(b, 0, b.length);
			if ((maxOff = instream.read(buf, 0, BUFLEN)) <= 4)
				eof = true;
		} else if (sizev2 > 10) {
			id3tag.parseID3V2(buf, 0, sizev2);
			off = sizev2;
		}
		return tagSize;
	}

	/**
	 * 鑾峰彇甯уご淇℃伅銆?
	 * 
	 * @return 鍙栧抚 {@link jmp123.decoder.Header} 瀵硅薄銆?
	 * @see jmp123.decoder.Header
	 */
	public Header getHeader() {
		return header;
	}

	/**
	 * 鑾峰彇鏂囦欢鐨勬爣绛句俊鎭??
	 * 
	 * @return 鏂囦欢鐨勬爣绛句俊鎭? {@link jmp123.decoder.ID3Tag} 瀵硅薄銆?
	 * @see jmp123.decoder.ID3Tag
	 */
	public ID3Tag getID3Tag() {
		return id3tag;
	}

	/**
	 * 瑙ｇ爜宸叉墦寮?鐨勬枃浠躲??
	 * 
	 * @param verbose
	 *            鎸囧畾涓? <b>true</b> 鍦ㄦ帶鍒跺彴鎵撳嵃鎾斁杩涘害鏉°??
	 * @return 鎴愬姛鎾斁鎸囧畾鐨勬枃浠惰繑鍥瀟rue锛屽惁鍒欒繑鍥瀎alse銆?
	 */
	public boolean start(boolean verbose) {
		Layer123 layer = null;
		int frames = 0;
		paused = false;

		switch (header.getLayer()) {
		case 1:
			layer = new Layer1(header, audio);
			break;
		case 2:
			layer = new Layer2(header, audio);
			break;
		case 3:
			layer = new Layer3(header, audio);
			break;
		default:
			return false;
		}

		try {
			while (!eof) {
				// 1. 瑙ｇ爜涓?甯у苟杈撳嚭
				off = layer.decodeFrame(buf, off);

				if (verbose && (++frames & 0x7) == 0)
					header.printProgress();

				// 2. 瀹氫綅鍒颁笅涓?甯у苟瑙ｇ爜甯уご
				nextHeader();

				// 3. 妫?娴嬪苟澶勭悊鏆傚仠
				if (paused) {
					synchronized (this) {
						while (paused && !eof)
							wait();
					}
				}
			}

			if (verbose) {
				header.printProgress();
				System.out.println("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			// System.out.println("jmp123.PlayBack.start() interrupt.");
		} finally {
			if (layer != null)
				layer.close();
		}
		// System.out.println("jmp123.PlayBack.start() ret.");

		return true;
	}

	private void nextHeader() throws IOException {
		int len, chunk = 0;
		while (!eof && header.syncFrame(buf, off, maxOff) == false) {
			// buf鍐呭抚鍚屾澶辫触鎴栨暟鎹笉瓒充竴甯э紝鍒锋柊缂撳啿鍖篵uf
			off = header.offset();
			len = maxOff - off;
			System.arraycopy(buf, off, buf, 0, len);
			maxOff = len + instream.read(buf, len, off);
			off = 0;
			if( maxOff <= len || (chunk += BUFLEN) > 0x10000)
				eof = true;
		}
		off = header.offset();
	}

}
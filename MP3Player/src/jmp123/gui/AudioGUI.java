/*
* AudioGUI.java -- 闊抽杈撳嚭鍙婇璋辨樉绀�
* Copyright (C) 2010
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
* so by contacting the author: <http://jmp123.sf.net/>
*/
package jmp123.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import cilent.mp3player_ui.CilentMainUI;
import jmp123.decoder.Header;
import jmp123.decoder.IAudio;
import jmp123.output.Audio;
import jmp123.output.FFT;

/**
 * 闊抽杈撳嚭锛堟挱鏀撅級锛岄璋辨樉绀猴紝鍞辩墖闆嗘樉绀恒��
 */
public class AudioGUI extends JComponent implements IAudio {
	// FFT
	private float realIO[];
	private FFT fft;

	// 闊抽杈撳嚭
	private Audio theAudio;
	
	// 棰戣氨鏄剧ず
	private static final long serialVersionUID = 1L;
	private static final int maxColums = 128;
	private static final int Y0 = 1 << ((FFT.FFT_N_LOG + 3) << 1);
	private static final double logY0 = Math.log10(Y0); //lg((8*FFT_N)^2)
	private final int width, height;
	private int band;
	private int maxFs, histogramType, deltax;
	private int[] xplot, lastPeak, lastY;
	private long lastTimeMillis;
	private BufferedImage spectrumBufferdImage, barBufferedImage;
	private Graphics2D spectrumGraphics;
	private Image bkImage, albumImage;
	private Color crPeak;
	private Header heder;
	private boolean isEnable;
	
	public static final int MANY_BAND = 3;
	public static final int MIDDLE_BAND = 2;
	public static final int LITTLE_BAND = 1;
	public static final int LESS_BAND = 0;

	/**
	 * 鏋勯�燗udioGUI瀵硅薄銆�
	 * @param sampleRate 棰戣氨鏄剧ず鐨勬埅姝㈤鐜囥��
	 * @param p 瀹炰緥鍖栨湰绫荤殑瀵硅薄锛圙UI鎾斁鍙ｅ櫒锛夈��
	 */
	public AudioGUI(int sampleRate) {
		fft = new FFT();
		realIO = new float[FFT.FFT_N];
		
		width = 700;	// 棰戣氨绐楀彛700x500
		height = 500;
		band = 96;		// 64娈甸璋�
		isEnable = true;
		maxFs = sampleRate >> 1;
		lastTimeMillis = System.currentTimeMillis();
		xplot = new int[maxColums + 1];
		lastPeak = new int[maxColums];
		lastY = new int[maxColums];
		
		// 缁濆璺緞: /jmp123/gui/resources/image/bk1.jpg
		//bkImage = new ImageIcon("./res/image/image1.jpg").getImage();
		//if(bkImage.getWidth(null) == -1)
			//bkImage = null;
		//if (bkImage.getWidth(null) < ) {
			
		//}

		spectrumBufferdImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		spectrumGraphics = (Graphics2D)spectrumBufferdImage.getGraphics();
		//spectrumGraphics.drawImage(bkImage, 0, 0, null); // 鍒濈姸鎬�,鍙樉绀鸿儗鏅�

		setPlot(width);
		barBufferedImage = new BufferedImage(deltax - 1, height, BufferedImage.TYPE_3BYTE_BGR);
		render(0xe0e0ff, 0xff0000, 0xffff00, 0xb0b0ff);

		java.awt.Font f = spectrumGraphics.getFont();
		java.awt.Font myFont = new java.awt.Font(f.getName(), java.awt.Font.PLAIN, 18);
		spectrumGraphics.setFont(myFont);

		addMouseListener(new MouseAdapter() {
			
			public void mousePressed(MouseEvent e) {
				if(++histogramType == 4)
					histogramType = 0;
			}
			
		});
	}

	private void render(int rgbPeak, int rgbTop, int rgbMid, int rgbBot) {
		crPeak = new Color(rgbPeak);
		spectrumGraphics.setColor(crPeak);

		Graphics2D g2d = (Graphics2D) barBufferedImage.getGraphics();
		Color crTop = new Color(rgbTop);
		Color crMid = new Color(rgbMid);
		Color crBot = new Color(rgbBot);
		g2d.setPaint(new GradientPaint(0, 0, crTop, deltax - 1, height / 2, crMid));
		g2d.fillRect(0, 0, deltax - 1, height / 2);
		g2d.setPaint(new GradientPaint(0, height / 2, crMid, deltax - 1, height, crBot));
		g2d.fillRect(0, height / 2, deltax - 1, height);
	}

	/**
	 * 鍒掑垎棰戞銆�
	 */
	private void setPlot(int width) {
		deltax = (width - band + 1) / band + 1;

		// fsband涓娈佃惤閲嶆柊鍒嗗垝涓篵and涓娈碉紝鍚勯娈靛搴﹂潪绾挎�у垝鍒嗐��
		int fsband = FFT.FFT_N >> 1;
		if(maxFs > 20000) {
			float deltaFs = (float)maxFs / (FFT.FFT_N >> 1);
			fsband -= (maxFs - 20000) / deltaFs;
		}
		for (int i = 0; i <= band; i++) {
			xplot[i] = 0;
			xplot[i] = (int) (0.5 + Math.pow(fsband, (double) i / band));
			if (i > 0 && xplot[i] <= xplot[i - 1])
				xplot[i] = xplot[i - 1] + 1;
		}
	}

	/**
	 * 缁樺埗"棰戠巼-骞呭��"鐩存柟鍥惧苟鏄剧ず鍒板睆骞曘��
	 * @param amp amp[0..FFT_N/2-1]涓洪璋�"骞呭��"(澶嶆暟妯＄殑骞虫柟)銆�
	 */
	private void drawHistogram(float[] amp) {
		float maxAmp;
		int i = 0, x = 0, y, xi, peaki, w = deltax - 1;
		long t = System.currentTimeMillis();
		int speed = (int)(t - lastTimeMillis) / 40;	//宄板�间笅钀介�熷害
		lastTimeMillis = t;
		
		drawBackground();
		//spectrumGraphics.drawString(title, 10, 40);
		
		for (; i != band; i++, x += deltax) {
			// 鏌ユ壘褰撳墠棰戞鐨勬渶澶�"骞呭��"
			maxAmp = 0; xi = xplot[i]; y = xplot[i + 1];
			for (; xi < y; xi++) {
				if (amp[xi] > maxAmp)
					maxAmp = amp[xi];
			}

			/*
			 * maxAmp杞崲涓虹敤瀵规暟琛ㄧず鐨�"鍒嗚礉鏁�"y:
			 * y = (int) Math.sqrt(maxAmp);
			 * y /= FFT_N; //骞呭��
			 * y /= 8;	//璋冩暣
			 * if(y > 0) y = (int)(Math.log10(y) * 20 * 1.7);
			 * 
			 * 涓轰簡绐佸嚭骞呭�紋鏄剧ず鏃跺己寮辩殑"瀵规瘮搴�"锛岃绠楁椂浣滀簡璋冩暣銆倅涓嶆槸涓ユ牸鎰忎箟涓婄殑鍒嗚礉鏁帮紝骞朵笖鏈綔绛夊搷搴︿慨姝ｃ��
			 */
			y = (maxAmp > Y0) ? (int) ((Math.log10(maxAmp) - logY0) * 50) : 0;

			// 浣垮箙鍊煎寑閫熷害涓嬭惤
			lastY[i] -= speed << 2;
			if(y < lastY[i]) {
				y = lastY[i];
				if(y < 0) y = 0;
			}
			lastY[i] = y;

			if(y >= lastPeak[i]) {
				lastPeak[i] = y + 2;
			} else {
				// 浣垮嘲鍊煎寑閫熷害涓嬭惤
				peaki = lastPeak[i] - speed;
				if(peaki < 0)
					peaki = 0;
				lastPeak[i] = peaki;
				peaki = height - peaki;
				spectrumGraphics.drawLine(x, peaki, x + w - 1, peaki);
			}
			y = height - y;	//鍧愭爣杞崲

			// 鐢诲綋鍓嶉娈电殑鐩存柟鍥�
			switch (histogramType) {
			case 0:
				spectrumGraphics.drawImage(barBufferedImage, x, y, x + w,
						height, 0, y, w, height, null);
				break;
			case 1:
				spectrumGraphics.drawRect(x, y, w - 1, height);
				break;
			case 2:
				spectrumGraphics.fillRect(x, y, w, height);
				break;
			case 3:
				spectrumGraphics.drawImage(barBufferedImage, x, y, null);
				break;
			}
		}

		// 鏂囧瓧淇℃伅
		float dura = this.heder.getDuration();
		i = (int) (dura / 60);
		x = this.heder.getElapse();
		y = x / 60;
//		String strMsg = String.format("[%02d:%02d] %02d:%02d", i,
//				(int) (dura - i * 60 + 0.5), y, x - y * 60);
//		spectrumGraphics.drawString(strMsg, 10, 20);
		int fulltime = (int)dura;
		String time1 = String.format("%02d:%02d", y, x - y * 60);
		String time3 = String.format("%02d:%02d", i, (int)(dura - i * 60 + 0.5));
		CilentMainUI.getTime1().setText(time1);
		CilentMainUI.getTime3().setText(time3);
		CilentMainUI.getSlider().setMaximum(fulltime);
		CilentMainUI.getSlider().setValue(x);
		
		if(CilentMainUI.getMusicMsg().isSelected()) {
			spectrumGraphics.setFont(new Font(getFont().getFontName(), Font.BOLD, 15));
			spectrumGraphics.drawString(PlayListThread.getMusicName(), 10, 20);
			spectrumGraphics.drawString(PlayListThread.getHeaderMsg(), 10, 40);
		}
		// 鍒峰埌灞忓箷
		repaint();
	}
	
	private void drawBackground() {
		spectrumGraphics.clearRect(0, 0, width, height);
		//spectrumGraphics.drawImage(bkImage, 0, 0, null);
	}

	//-------------------------------------------------------------------------
	// 瀹炵幇IAudio鎺ュ彛鏂规硶

	@Override
	public boolean open(Header h, String artist) {
		this.heder = h;
		theAudio = new Audio();
		return theAudio.open(h, null);
	}

	@Override
	public int write(byte[] b, int size) {
		int len;

		//1. 闊抽杈撳嚭
		if((len = theAudio.write(b, size)) == 0)
			return 0;

		if (isEnable) {
			int i, j;

			//2. 鑾峰彇PCM鏁版嵁銆傚鏋滄槸鍙屽０閬擄紝杞崲涓哄崟澹伴亾
			if (heder.getChannels() == 2) {
				for (i = 0; i < FFT.FFT_N; i++) {
					j = i << 2;
					// (宸﹀０閬� + 鍙冲０閬�) / 2
					realIO[i] = (((b[j + 1] << 8) | (b[j] & 0xff))
							+ (b[j + 3] << 8) | (b[j + 2] & 0xff)) >> 1;
				}
			} else {
				for (i = 0; i < 512; i++) {
					j = i << 1;
					realIO[i] = ((b[j + 1] << 8) | (b[j] & 0xff));
				}
			}

			//3. PCM鍙樻崲鍒伴鍩熷苟鍙栧洖妯�
			fft.getModulus(realIO);

			//4. 缁樺埗
			drawHistogram(realIO);
		}

		return len;
	}
	
	public void start(boolean b) {
		theAudio.start(b);
	}

	@Override
	public void drain() {
		theAudio.drain();
	}

	@Override
	public void close() {
		if(theAudio != null)
			theAudio.close();
		
		setPlot(this.width);
		repaint(0, 0, width, height);
	}

	@Override
	public void refreshMessage(String msg) {
		if (msg.indexOf('@') != -1) {
			// if(!msg.startsWith("null"))
			// title = msg;
		} else {
			spectrumGraphics.drawString(msg, 10, 20);
			repaint(0, 0, width, height);
		}
	}
	
	//-------------------------------------------------------------------------
	// 閲嶈浇鐖剁被鐨�2涓柟娉�
	
	/**
	 * 鏍规嵁鍙傛暟 b 鐨勫�兼樉绀烘垨闅愯棌棰戣氨缁勪欢銆傞殣钘忛璋辩粍浠跺悗鑷姩鍋滄瀵筆CM鏁版嵁鐨凢FT杩愮畻骞跺仠姝㈣幏鍙栭煶棰慞CM鏁版嵁銆�
	 * @param b 濡傛灉涓� true锛屽垯鏄剧ず棰戣氨缁勪欢锛涘惁鍒欓殣钘忛璋辩粍浠躲��
	 */
	public void setVisible(boolean b) {
		super.setVisible(b);
		this.isEnable = b;
	}
	
	/**
	 * 缁樺埗棰戣氨缁勪欢銆�
	 * @param g 鐢ㄤ簬缁樺埗鐨勫浘鍍忎笂涓嬫枃銆�
	 */
	public void paint(Graphics g) {
		g.drawImage(spectrumBufferdImage, 0, 0, null);
	}
	
	//设置频谱数量
	public void setBandNumber(int BandNumber) {
		switch (BandNumber) {
		case MANY_BAND:
			band = 115;
			break;
		case MIDDLE_BAND:
			band = 96;
			break;
		case LITTLE_BAND:
			band = 65;
			break;
		case LESS_BAND:
			band = 32;
			break;
		}
		setPlot(width);
		barBufferedImage = new BufferedImage(deltax - 1, height, BufferedImage.TYPE_3BYTE_BGR);
		render(0xe0e0ff, 0xff0000, 0xffff00, 0xb0b0ff);
	}
}

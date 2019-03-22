/*
* Header.java --MPEG-1/2/2.5 Audio Layer I/II/III 甯у悓姝ュ拰甯уご淇℃伅瑙ｇ爜
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
* so by contacting the author: <http://jmp123.sourceforge.net/>.
*/

package jmp123.decoder;

/**
 * 甯у悓姝ュ強甯уご淇℃伅瑙ｇ爜銆�
 */
public final class Header {
	/**
	 * MPEG鐗堟湰MPEG-1銆�
	 */
	public static final int MPEG1 = 3;

	/**
	 * MPEG鐗堟湰MPEG-2銆�
	 */
	public static final int MPEG2 = 2;

	/**
	 * MPEG鐗堟湰MPEG-2.5锛堥潪瀹樻柟鐗堟湰锛夈��
	 */
	public static final int MPEG25 = 0;
	// public static final int MAX_FRAMESIZE = 1732; //MPEG 1.0/2.0/2.5, Layer 1/2/3

	/*
	 * bitrate[lsf][layer-1][bitrate_index]
	 */
	private int[][][] bitrate = { {
			// MPEG-1
			// Layer I
			{ 0, 32, 64, 96, 128, 160, 192, 224, 256, 288, 320, 352, 384, 416, 448 },
			// Layer II
			{ 0, 32, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320, 384 },
			// Layer III
			{ 0, 32, 40, 48, 56, 64, 80, 96, 112, 128, 160, 192, 224, 256, 320 } },
			{
					// MPEG-2/2.5
					// Layer I
					{ 0, 32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, 192, 224, 256 },
					// Layer II
					{ 0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160 },
					// Layer III
					{ 0, 8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160 } } };

	/*
	 * samplingRate[verID][sampling_frequency]
	 */
	private int[][] samplingRate = { { 11025, 12000, 8000, 0 }, // MPEG-2.5
			{ 0, 0, 0, 0, }, // reserved
			{ 22050, 24000, 16000, 0 }, // MPEG-2 (ISO/IEC 13818-3)
			{ 44100, 48000, 32000, 0 } // MPEG-1 (ISO/IEC 11172-3)
	};

	/*
	 * verID: 2-bit '00' MPEG-2.5 (unofficial extension of MPEG 2); '01' reserved;
	 * '10' MPEG-2 (ISO/IEC 13818-3); '11' MPEG-1 (ISO/IEC 11172-3).
	 */
	private int verID;

	/*
	 * layer: 2-bit '11' Layer I '10' Layer II '01' Layer III '00' reserved
	 * 
	 * 宸叉崲绠條ayer=4-layer: 1--Layer I; 2--Layer II; 3--Layer III; 4--reserved
	 */
	private int layer;

	/*
	 * protection_bit: 1-bit '1' no CRC; '0' protected by 16 bit CRC following
	 * header.
	 */
	private int protection_bit;

	/*
	 * bitrate_index: 4-bit
	 */
	private int bitrate_index;

	/*
	 * sampling_frequency: 2-bit '00' 44.1kHz '01' 48kHz '10' 32kHz '11' reserved
	 */
	private int sampling_frequency;

	private int padding_bit;

	/*
	 * mode: 2-bit '00' Stereo; '01' Joint Stereo (Stereo); '10' Dual channel (Two
	 * mono channels); '11' Single channel (Mono).
	 */
	private int mode;

	/*
	 * mode_extension: 2-bit intensity_stereo MS_stereo '00' off off '01' on off
	 * '10' off on '11' on on
	 */
	private int mode_extension;

	private int framesize;
	private int maindatasize; // main_data length
	private int sideinfosize; // side_information length
	private int lsf;
	private int headerMask;
	private boolean isMS, isIntensity;
	private boolean sync; // true:甯уご鐨勭壒寰佹湭鏀瑰彉

	/**
	 * 鐢ㄦ寚瀹氱殑鍙傛暟鍒濆鍖朒eader銆傚鏋滀簨鍏堜笉鐭ラ亾杩欎簺鍙傛暟鐨勫�硷紝鍙互鎸囧畾涓洪浂銆�
	 * 
	 * @param trackLength 鎸囧畾闊宠建闀垮害锛屽崟浣嶁�滃瓧鑺傗�濄��
	 * @param duration    鎸囧畾闊宠建鎾斁鏃堕暱锛屽崟浣嶁�滅鈥濄��
	 */
	public void initialize(long trackLength, int duration) {
		this.trackLength = trackLength;
		this.duration = duration;
		headerMask = 0xffe00000;
		progress_index = 1;
		// 鍒濆鍖栵紝浣垮彲閲嶅叆
		sync = false;
		trackFrames = 0;
		sideinfosize = tocNumber = tocPer = tocFactor = framecounter = 0;
		vbrtoc = null;
		strBitRate = null;
		progress = null;
	}

	private void parseHeader(int h) {
		verID = (h >> 19) & 3;
		layer = 4 - (h >> 17) & 3;
		protection_bit = (h >> 16) & 0x1;
		bitrate_index = (h >> 12) & 0xF;
		sampling_frequency = (h >> 10) & 3;
		padding_bit = (h >> 9) & 0x1;
		mode = (h >> 6) & 3;
		mode_extension = (h >> 4) & 3;

		isMS = mode == 1 && (mode_extension & 2) != 0;
		isIntensity = mode == 1 && (mode_extension & 0x1) != 0;
		lsf = (verID == MPEG1) ? 0 : 1;

		switch (layer) {
		case 1:
			framesize = bitrate[lsf][0][bitrate_index] * 12000;
			framesize /= samplingRate[verID][sampling_frequency];
			framesize = ((framesize + padding_bit) << 2);
			break;
		case 2:
			framesize = bitrate[lsf][1][bitrate_index] * 144000;
			framesize /= samplingRate[verID][sampling_frequency];
			framesize += padding_bit;
			break;
		case 3:
			framesize = bitrate[lsf][2][bitrate_index] * 144000;
			framesize /= samplingRate[verID][sampling_frequency] << (lsf);
			framesize += padding_bit;
			// 璁＄畻甯ц竟淇℃伅闀垮害
			if (verID == MPEG1)
				sideinfosize = (mode == 3) ? 17 : 32;
			else
				sideinfosize = (mode == 3) ? 9 : 17;
			break;
		}

		// 璁＄畻涓绘暟鎹暱搴�
		maindatasize = framesize - 4 - sideinfosize;
		if (protection_bit == 0)
			maindatasize -= 2; // CRC
	}

	private int byte2int(byte[] b, int off) {
		int int32 = b[off++] & 0xff;
		int32 <<= 8;
		int32 |= b[off++] & 0xff;
		int32 <<= 8;
		int32 |= b[off++] & 0xff;
		int32 <<= 8;
		int32 |= b[off] & 0xff;
		return int32;
	}

	private int byte2short(byte[] b, int off) {
		int int16 = b[off++] & 0xff;
		int16 <<= 8;
		int16 |= b[off] & 0xff;
		return int16;
	}

	private boolean available(int h, int curmask) {
		return (h & curmask) == curmask && ((h >> 19) & 3) != 1 // version ID: '01' - reserved
				&& ((h >> 17) & 3) != 0 // Layer index: '00' - reserved
				&& ((h >> 12) & 15) != 15 // Bitrate Index: '1111' - reserved
				&& ((h >> 12) & 15) != 0 // Bitrate Index: '0000' - free
				&& ((h >> 10) & 3) != 3; // Sampling Rate Index: '11' - reserved
	}

	private int idx; // 鏆傚瓨syncFrame鏂规硶涓紦鍐插尯b鐨勫亸绉婚噺

	public int offset() {
		return idx;
	}

	/**
	 * 甯у悓姝ュ強甯уご淇℃伅瑙ｇ爜銆傝皟鐢ㄥ墠搴旂‘淇濇簮鏁版嵁缂撳啿鍖� b 闀垮害 b.length 涓嶅皬浜庢渶澶у抚闀� 1732銆�
	 * <p>
	 * 鏈柟娉曟墽琛岀殑鎿嶄綔锛�
	 * <ol>
	 * <li>鏌ユ壘婧愭暟鎹紦鍐插尯 b 鍐呭抚鍚屾瀛楋紙syncword锛夈��</li>
	 * <li>濡傛灉鏌ユ壘鍒板抚鍚屾瀛楁锛�</li>
	 * <ol type="I">
	 * <li>瑙ｆ瀽甯уご4瀛楄妭銆�</li>
	 * <li>濡傛灉褰撳墠鏄涓�甯э紝瑙ｇ爜VBR淇℃伅銆�</li>
	 * </ol>
	 * <li>杩斿洖銆�</li>
	 * <ul>
	 * <li>鑻ヨ繑鍥�<b>true</b>琛ㄧず鏌ユ壘鍒板抚鍚屾瀛楁锛� 鎺ヤ笅鏉ヨ皟鐢� {@link #getVersion()}銆�
	 * {@link #getFrameSize()} 绛夋柟娉曡兘澶熻繑鍥炴纭殑鍊笺��</li>
	 * <li>鑻ユ湭鏌ユ壘鍒板抚鍚屾瀛楁锛岃繑鍥�<b>false</b>銆�</li>
	 * </ul>
	 * </ol>
	 * 
	 * @param b      婧愭暟鎹紦鍐插尯銆�
	 * @param off    缂撳啿鍖� b 涓暟鎹殑鍒濆鍋忕Щ閲忋��
	 * @param endPos 缂撳啿鍖� b 涓厑璁歌闂殑鏈�澶у亸绉婚噺銆傛渶澶у亸绉婚噺鍙兘姣旂紦鍐插尯 b 鐨勪笂鐣屽皬銆�
	 * @return 杩斿洖<b>true</b>琛ㄧず鏌ユ壘鍒板抚鍚屾瀛楁銆�
	 */
	public boolean syncFrame(byte[] b, int off, int endPos) {
		int h, mask = 0;
		int skipBytes = 0; // ----debug
		idx = off;

		if (endPos - idx <= 4)
			return false;

		h = byte2int(b, idx);
		idx += 4;

		while (true) {
			// 1.鏌ユ壘甯у悓姝ュ瓧
			while (!available(h, headerMask)) {
				h = (h << 8) | (b[idx++] & 0xff);
				if (idx == endPos) {
					idx -= 4;
					return false;
				}
			}
			if (idx > 4 + off) {
				sync = false;
				skipBytes += idx - off - 4;
			}

			// 2. 瑙ｆ瀽甯уご
			parseHeader(h);
			if (idx + framesize > endPos + 4) {
				idx -= 4;
				return false;
			}

			// 鑻erID绛夊抚鐨勭壒寰佹湭鏀瑰彉(sync==true),涓嶇敤涓庝笅涓�甯х殑鍚屾澶存瘮杈�
			if (sync)
				break;

			// 3.涓庝笅涓�甯х殑鍚屾澶存瘮杈�,纭畾鏄惁鎵惧埌鏈夋晥鐨勫悓姝ュ瓧.
			if (idx + framesize > endPos) {
				idx -= 4;
				return false;
			}
			mask = 0xffe00000; // syncword
			mask |= h & 0x180000; // version ID
			mask |= h & 0x60000; // Layer index
			mask |= h & 0xc00; // sampling_frequency
			// mode, mode_extension 涓嶆槸姣忓抚閮界浉鍚�.
			if (available(byte2int(b, idx + framesize - 4), mask)) {
				if (headerMask == 0xffe00000) { // 鏄涓�甯�
					headerMask = mask;
					trackFrames = trackLength / framesize;
					parseVBR(b, idx);
					frameDuration = 1152f / (getSamplingRate() << lsf);
					if (trackFrames == 0)
						trackFrames = (long) (duration / frameDuration);
					if (trackLength == 0)
						trackLength = trackFrames * framesize;
					duration = frameDuration * trackFrames;
				}
				sync = true;
				break; // 鎵惧埌鏈夋晥鐨勫抚鍚屾瀛楁锛岀粨鏉熸煡鎵�
			}

			// 绉诲姩鍒颁笅涓�瀛楄妭锛岀户缁噸澶�1-3
			h = (h << 8) | (b[idx++] & 0xff);
		}

		if (protection_bit == 0)
			idx += 2; // CRC word
		framecounter++;

		if (skipBytes > 0)
			System.out.printf("frame# %d, skip bytes: %d\n", framecounter, skipBytes);

		return true;
	}

	/**
	 * 鑾峰彇澹伴亾妯″紡鏄惁涓轰腑/渚х珛浣撳０锛圡id/Side stereo锛夋ā寮忋��
	 * 
	 * @return true琛ㄧず鏄腑/渚х珛浣撳０妯″紡銆�
	 */
	public boolean isMS() {
		return isMS;
	}

	/**
	 * 鑾峰彇澹伴亾妯″紡鏄惁涓哄己搴︾珛浣撳０锛圛ntensity Stereo锛夋ā寮忋��
	 * 
	 * @return true琛ㄧず鏄己搴︾珛浣撳０妯″紡銆�
	 */
	public boolean isIntensityStereo() {
		return isIntensity;
	}

	/**
	 * 鑾峰彇褰撳墠甯х殑浣嶇巼銆�
	 * 
	 * @return 褰撳墠甯х殑浣嶇巼锛屽崟浣嶄负鈥滃崈浣嶆瘡绉掞紙Kbps锛夆�濄��
	 */
	public int getBitrate() {
		return bitrate[lsf][layer - 1][bitrate_index];
	}

	/**
	 * 鑾峰彇褰撳墠甯х殑浣嶇巼鐨勭储寮曞�笺��
	 * 
	 * @return 褰撳墠甯х殑浣嶇巼鐨勭储寮曞�硷紝浣嶇巼鐨勭储寮曞�艰寖鍥存槸1鑷�14鐨勬煇涓�鏁存暟銆�
	 */
	public int getBitrateIndex() {
		return bitrate_index;
	}

	/**
	 * 鑾峰彇澹伴亾鏁般��
	 * 
	 * @return 澹伴亾鏁帮細1鎴�2銆�
	 */
	public int getChannels() {
		return (mode == 3) ? 1 : 2;
	}

	/**
	 * 鑾峰彇澹伴亾妯″紡銆�
	 * 
	 * @return 澹伴亾妯″紡锛屽叾鍊艰〃绀虹殑鍚箟锛�
	 *         <table border="1" cellpadding="8">
	 *         <tr>
	 *         <th>杩斿洖鍊�</th>
	 *         <th>澹伴亾妯″紡</th>
	 *         </tr>
	 *         <tr>
	 *         <td>0</td>
	 *         <td>绔嬩綋澹帮紙stereo锛�</td>
	 *         </tr>
	 *         <tr>
	 *         <td>1</td>
	 *         <td>鑱斿悎绔嬩綋澹帮紙joint stereo锛�</td>
	 *         </tr>
	 *         <tr>
	 *         <td>2</td>
	 *         <td>鍙屽０閬擄紙dual channel锛�</td>
	 *         </tr>
	 *         <tr>
	 *         <td>3</td>
	 *         <td>鍗曞０閬擄紙mono channel锛�</td>
	 *         </tr>
	 *         </table>
	 * @see #getModeExtension()
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * 鑾峰彇澹伴亾鎵╁睍妯″紡銆�
	 * 
	 * @return 澹伴亾鎵╁睍妯″紡锛岃鍊艰〃绀哄綋鍓嶅０閬撲娇鐢ㄧ殑绔嬩綋澹扮紪鐮佹柟寮忥細
	 *         <table border="1" cellpadding="8">
	 *         <tr>
	 *         <th>杩斿洖鍊�</th>
	 *         <th>寮哄害绔嬩綋澹�</th>
	 *         <th>涓�/渚х珛浣撳０</th>
	 *         </tr>
	 *         <tr>
	 *         <td>0</td>
	 *         <td>off</td>
	 *         <td>off</td>
	 *         </tr>
	 *         <tr>
	 *         <td>1</td>
	 *         <td>on</td>
	 *         <td>off</td>
	 *         </tr>
	 *         <tr>
	 *         <td>2</td>
	 *         <td>off</td>
	 *         <td>on</td>
	 *         </tr>
	 *         <tr>
	 *         <td>3</td>
	 *         <td>on</td>
	 *         <td>on</td>
	 *         </tr>
	 *         </table>
	 * @see #getMode()
	 */
	public int getModeExtension() {
		return mode_extension;
	}

	/**
	 * 鑾峰彇MPEG鐗堟湰銆�
	 * 
	 * @return MPEG鐗堟湰锛歿@link #MPEG1}銆� {@link #MPEG2} 鎴� {@link #MPEG25} 銆�
	 */
	public int getVersion() {
		return verID;
	}

	/**
	 * 鑾峰彇MPEG缂栫爜灞傘��
	 * 
	 * @return MPEG缂栫爜灞傦細杩斿洖鍊�1琛ㄧずLayer鈪狅紝2琛ㄧずLayer鈪★紝3琛ㄧずLayer鈪€��
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * 鑾峰彇PCM鏍锋湰閲囨牱鐜囩殑绱㈠紩鍊笺��
	 * 
	 * @return PCM鏍锋湰閲囨牱鐜囩殑绱㈠紩鍊笺��
	 */
	public int getSamplingFrequency() {
		return sampling_frequency;
	}

	/**
	 * 鑾峰彇PCM鏍锋湰閲囨牱鐜囥��
	 * 
	 * @return 鑾峰彇PCM鏍锋湰閲囨牱鐜囷紝鍗曚綅鈥滆但鍏癸紙Hz锛夆��
	 */
	public int getSamplingRate() {
		return samplingRate[verID][sampling_frequency];
	}

	/**
	 * 鑾峰彇涓绘暟鎹暱搴︺��
	 * 
	 * @return 褰撳墠甯х殑涓绘暟鎹暱搴︼紝鍗曚綅鈥滃瓧鑺傗�濄��
	 */
	public int getMainDataSize() {
		return maindatasize;
	}

	/**
	 * 鑾峰彇杈逛俊鎭暱搴︺��
	 * 
	 * @return 褰撳墠甯ц竟淇℃伅闀垮害锛屽崟浣嶁�滃瓧鑺傗�濄��
	 */
	public int getSideInfoSize() {
		return sideinfosize;
	}

	/**
	 * 鑾峰彇甯ч暱搴︺�傚抚鐨勯暱搴� = 4瀛楄妭甯уご + CRC锛堝鏋滄湁鐨勮瘽锛�2瀛楄妭锛� + 杈逛俊鎭暱搴� + 涓绘暟鎹暱搴︺��
	 * <p>
	 * 鏃犺鍙彉浣嶇巼锛圴BR锛夌紪鐮佺殑鏂囦欢杩樻槸鍥哄畾浣嶇巼锛圕BR锛夌紪鐮佺殑鏂囦欢锛屾瘡甯х殑闀垮害涓嶄竴瀹氬悓銆�
	 * 
	 * @return 褰撳墠甯х殑闀垮害锛屽崟浣嶁�滃瓧鑺傗�濄��
	 */
	public int getFrameSize() {
		return framesize;
	}

	/**
	 * 鑾峰彇褰撳墠甯цВ鐮佸悗寰楀埌鐨凱CM鏍锋湰闀垮害銆傞�氬父鎯呭喌涓嬪悓涓�鏂囦欢姣忎竴甯цВ鐮佸悗寰楀埌鐨凱CM鏍锋湰闀垮害鏄浉鍚岀殑銆�
	 * 
	 * @return 褰撳墠甯цВ鐮佸悗寰楀埌鐨凱CM鏍锋湰闀垮害锛屽崟浣嶁�滃瓧鑺傗�濄��
	 */
	public int getPcmSize() {
		int pcmsize = (verID == MPEG1) ? 4608 : 2304;
		if (mode == 3) // if channels == 1
			pcmsize >>= 1;
		return pcmsize;
	}

	// ================================ 杈呭姪鍔熻兘 =================================
	// 鍒犻櫎浠ヤ笅浠ｇ爜鍙婂瀹冧滑鐨勭浉鍏冲紩鐢細(1)涓嶅奖鍝嶆枃浠剁殑姝ｅ父瑙ｇ爜锛�(2)涓嶈兘鑾峰彇鍙婃墦鍗板緟瑙ｇ爜鏂囦欢鐨勪俊鎭��

	private long trackLength; // 闊宠建甯ф�婚暱搴�(鏂囦欢闀垮害鍑忓幓鏍囩淇℃伅鍩熼暱搴�)
	private long trackFrames; // 闊宠建甯ф暟
	private float frameDuration;// 涓�甯ф椂闀�(绉�)
	private float duration; // 闊宠建鎾斁鏃堕暱(绉�)
	private int framecounter; // 褰撳墠甯у簭鍙�

	private StringBuilder vbrinfo;
	private byte[] vbrtoc;
	private int tocNumber, tocPer, tocFactor;
	private String strBitRate;

	private StringBuilder progress;
	private int progress_index;

	/**
	 * 鑾峰彇褰撳墠鏂囦欢鐨勯煶杞ㄩ暱搴︺��
	 * 
	 * @return 褰撳墠鏂囦欢鐨勯煶杞ㄩ暱搴︼紝鍗虫枃浠堕暱搴﹀噺鍘讳竴浜涙爣绛撅紙tag锛変俊鎭煙鍚庣殑绾煶涔愭暟鎹殑闀垮害锛屽崟浣嶁�滃瓧鑺傗�濄��
	 */
	public long getTackLength() {
		return trackLength;
	}

	/**
	 * 鑾峰彇褰撳墠甯х殑搴忓彿銆�
	 * 
	 * @return 褰撳墠甯х殑搴忓彿锛岃〃绀哄綋鍓嶆鍦ㄨВ鐮佺澶氬皯甯с�傜敱浜庢瘡涓�甯х殑鎾斁鏃堕暱鐩稿悓锛屾墍浠ュ彲浠ュ埄鐢ㄥ畠璁＄畻褰撳墠鎾斁鏃堕棿杩涘害銆�
	 */
	public int getFrames() {
		return framecounter;
	}

	/**
	 * 鑾峰彇褰撳墠鏂囦欢鐨勯煶杞ㄧ殑鎬诲抚鏁般��
	 * 
	 * @return 褰撳墠鏂囦欢鐨勯煶杞ㄧ殑甯ф暟锛屽嵆褰撳墠鏂囦欢鍏辨湁澶氬皯甯с�傜敱浜庢瘡涓�甯х殑鎾斁鏃堕暱鐩稿悓锛屾墍浠ュ彲浠ュ埄鐢ㄥ畠璁＄畻鏂囦欢鎾斁鏃堕暱銆�
	 */
	public long getTrackFrames() {
		return trackFrames;
	}

	/**
	 * 鑾峰彇褰撳墠鏂囦欢涓�甯х殑鎾斁鏃堕棿闀垮害銆�
	 * 
	 * @return 褰撳墠鏂囦欢涓�甯х殑鎾斁鏃堕棿闀垮害锛屽崟浣嶁�滅鈥濄��
	 */
	public float getFrameDuration() {
		return frameDuration;
	}

	/**
	 * 鑾峰彇褰撳墠鏂囦欢鐨勬挱鏀炬椂闂存�婚暱搴︺��
	 * 
	 * @return 褰撳墠鏂囦欢鐨勬甯告挱鏀撅紙鏃犲揩杩涘揩閫�銆佹暟鎹祦鏃犳崯鍧忥級鏃堕棿闀垮害锛屽崟浣嶁�滅鈥濄��
	 */
	public float getDuration() {
		return duration;
	}

	/**
	 * 鑾峰彇鎾斁褰撳墠鏂囦欢鏃堕棿杩涘害銆�
	 * 
	 * @return 褰撳墠鏂囦欢鐨勬挱鏀炬椂闂磋繘搴︼紝鍗曚綅鈥滅鈥濄��
	 */
	public int getElapse() {
		return (int) (framecounter * frameDuration);
	}

	/**
	 * 鑾峰彇鍙彉浣嶇巼锛圴BR锛夋爣绛句俊鎭��
	 * 
	 * @return 鍙彉浣嶇巼锛圴BR锛夋爣绛句俊鎭��
	 */
	public String getVBRInfo() {
		return vbrinfo == null ? null : vbrinfo.toString();
	}

	/*
	 * 瑙ｇ爜瀛樺偍鍦ㄧ涓�甯х殑VBR淇℃伅.鑻ョ涓�甯у瓨鍌ㄧ殑鏄疺BR淇℃伅,鐢变簬甯ц竟淇℃伅琚～鍏呬负0,涓嶈В 鐮乂BR tag
	 * 鑰屾妸杩欎竴甯т綔涓洪煶棰戝抚瑙ｇ爜涓嶅奖鍝嶈В鐮佸櫒鐨勫悗缁甯歌В鐮�.
	 */
	private void parseVBR(byte[] b, int off) {
		vbrinfo = null;
		final int maxOff = off + framesize - 4;
		if (maxOff >= b.length)
			return;
		for (int i = 2; i < sideinfosize; i++) // 鍓�2瀛楄妭鍙兘鏄疌RC_word
			if (b[off + i] != 0)
				return;
		off += sideinfosize;
		// System.out.println("tagsize=" + (frameSize-4-sideinfoSize));

		// -------------------------------VBR tag------------------------------
		if ((b[off] == 'X' && b[off + 1] == 'i' && b[off + 2] == 'n' && b[off + 3] == 'g')
				|| (b[off] == 'I' && b[off + 1] == 'n' && b[off + 2] == 'f' && b[off + 3] == 'o')) {
			// Xing/Info header
			if (maxOff - off < 120)
				return;
			off = xinginfoHeader(b, off);
		} else if (b[off] == 'V' && b[off + 1] == 'B' && b[off + 2] == 'R' && b[off + 3] == 'I') {
			// VBRI header
			if (maxOff - off < 26)
				return;

			off = vbriHeader(b, off);

			int toc_size = tocNumber * tocPer;
			if (maxOff - off < toc_size)
				return;
			vbrinfo.append("\n          TOC: ");
			vbrinfo.append(tocNumber);
			vbrinfo.append(" * ");
			vbrinfo.append(tocPer);
			vbrinfo.append(", factor = ");
			vbrinfo.append(tocFactor);
			vbrtoc = new byte[toc_size];
			System.arraycopy(b, off, vbrtoc, 0, toc_size);
			off += toc_size;
		} else
			return;

		// -------------------------------LAME tag------------------------------
		// 36-byte: 9+1+1+8+1+1+3+1+1+2+4+2+2

		if (maxOff - off < 36 || b[off] == 0) {
			strBitRate = "VBR";
			return;
		}
		// Encoder Version: 9-byte
		String encoder = new String(b, off, 9);
		off += 9;
		vbrinfo.append("\n      encoder: ");
		vbrinfo.append(encoder);

		// 'Info Tag' revision + VBR method: 1-byte
		int revi = (b[off] & 0xff) >> 4; // 0:rev0; 1:rev1; 15:reserved
		int lame_vbr = b[off++] & 0xf; // 0:unknown

		// 浣庨�氭护娉笂闄愬��(Lowpass filter value): 1-byte
		int lowpass = b[off++] & 0xff;
		vbrinfo.append("\n      lowpass: ");
		vbrinfo.append(lowpass * 100);
		vbrinfo.append("Hz");
		vbrinfo.append("\n     revision: ");
		vbrinfo.append(revi);

		// 鍥炴斁澧炵泭(Replay Gain):8-byte
		float peak = Float.intBitsToFloat(byte2int(b, off)); // Peak signal amplitude
		off += 4;
		int radio = byte2short(b, off); // Radio Replay Gain
		/*
		 * radio: bits 0h-2h: NAME of Gain adjustment: 000 = not set 001 = radio 010 =
		 * audiophile bits 3h-5h: ORIGINATOR of Gain adjustment: 000 = not set 001 = set
		 * by artist 010 = set by user 011 = set by my model 100 = set by simple RMS
		 * average bit 6h: Sign bit bits 7h-Fh: ABSOLUTE GAIN ADJUSTMENT. storing 10x
		 * the adjustment (to give the extra decimal place).
		 */
		off += 2;
		int phile = byte2short(b, off); // Audiophile Replay Gain
		/*
		 * phile鍚勪綅鍚箟鍚屼笂(radio)
		 */
		off += 2;

		// Encoding flags + ATH Type: 1 byte
		/*
		 * int enc_flag = (b[iOff] & 0xff) >> 4; int ath_type = b[iOff] & 0xf;
		 * //000?0000: LAME uses "--nspsytune" ? boolean nsp = ((enc_flag & 0x1) == 0) ?
		 * false : true; //00?00000: LAME uses "--nssafejoint" ? boolean nsj =
		 * ((enc_flag & 0x2) == 0) ? false : true; //0?000000: This track is --nogap
		 * continued in a next track ? //is true for all but the last track in a --nogap
		 * album boolean nogap_next = ((enc_flag & 0x4) == 0) ? false : true;
		 * //?0000000: This track is the --nogap continuation of an earlier one ? //is
		 * true for all but the first track in a --nogap album boolean nogap_cont =
		 * ((enc_flag & 0x8) == 0) ? false : true;
		 */
		off++;

		// ABR/CBR浣嶇巼鎴朧BR鐨勬渶灏忎綅鐜�(0xFF琛ㄧず浣嶇巼涓�255Kbps浠ヤ笂): 1-byte
		int lame_bitrate = b[off++] & 0xff;
		switch (lame_vbr) {
		case 1:
		case 8: // CBR
			strBitRate = String.format("CBR %1$dK", getBitrate());
			break;
		case 2:
		case 9: // ABR
			if (lame_bitrate < 0xff)
				strBitRate = String.format("ABR %1$dK", lame_bitrate);
			else
				strBitRate = String.format("ABR %1$dK浠ヤ笂", lame_bitrate);
			break;
		default:
			if (lame_bitrate == 0) // 0: unknown is VBR ?
				strBitRate = "VBR";
			else
				strBitRate = String.format("VBR(min%dK)", lame_bitrate);
		}

		// Encoder delays: 3-byte
		off += 3;

		// Misc: 1-byte
		off++;

		// MP3 Gain: 1-byte.
		// 浠讳綍MP3鑳芥棤鎹熸斁澶�2^(mp3_gain/4),浠�1.5dB涓烘杩涘�兼敼鍙�"Replay Gain"鐨�3涓煙:
		// "Peak signal amplitude", "Radio Replay Gain", "Audiophile Replay Gain"
		// mp3_gain = -127..+127, 瀵瑰簲鐨�:
		// 鍒嗚礉鍊�-190.5dB..+190.5dB; mp3_gain澧炲姞1, 澧炲姞1.5dB
		// 鏀惧ぇ鍊嶆暟0.000000000276883..3611622602.83833951
		int mp3_gain = b[off++]; // 鍏剁己鐪佸�间负0
		if (mp3_gain != 0)
			System.out.println("    MP3 Gain: " + mp3_gain + " [psa=" + peak + ",rrg=" + radio + ",arg=" + phile + "]");

		// Preset and surround info: 2-byte
		int preset_surround = byte2short(b, off);
		int surround_info = (preset_surround >> 11) & 0x7;
		switch (surround_info) {
		case 0: // no surround info
			break;
		case 1: // DPL encoding
			vbrinfo.append("\n     surround: DPL");
			break;
		case 2: // DPL2 encoding
			vbrinfo.append("\n     surround: DPL2");
			break;
		case 3: // Ambisonic encoding
			vbrinfo.append("\n     surround: Ambisonic");
			break;
		case 7: // reserved
			vbrinfo.append("\n     surround: invalid data");
			break;
		}
		preset_surround &= 0x7ff; // 11 bits: 2047 presets
		if (preset_surround != 0) { // 0: unknown / no preset used
			vbrinfo.append("\n     surround: preset ");
			vbrinfo.append(preset_surround);
		}
		off += 2;

		// Music Length: 4-byte
		// MP3鏂囦欢鍘熷鐨�(鍗抽櫎鍘籌D3 tag,APE tag绛�)'LAME Tag frame'鍜�'闊充箰鏁版嵁'鐨勬�诲瓧鑺傛暟
		int music_len = byte2int(b, off);
		off += 4;
		if (music_len != 0)
			trackLength = music_len;

		// Music CRC: 2-byte
		off += 2;

		// CRC-16 of Info Tag: 2-byte
	}

	private int vbriHeader(byte[] b, int off) {
		if (vbrinfo == null)
			vbrinfo = new StringBuilder();
		vbrinfo.append("   vbr header: vbri");

		// version ID: 2-byte
		// Delay: 2-byte
		int vbri_quality = byte2short(b, off + 8);
		vbrinfo.append("\n      quality: ");
		vbrinfo.append(vbri_quality);

		trackLength = byte2int(b, off + 10);
		vbrinfo.append("\n  track bytes: ");
		vbrinfo.append(trackLength);

		trackFrames = byte2int(b, off + 14);
		vbrinfo.append("\n track frames: ");
		vbrinfo.append(trackFrames);

		tocNumber = byte2short(b, off + 18);
		tocFactor = byte2short(b, off + 20);
		tocPer = byte2short(b, off + 22);
		int toc_frames = byte2short(b, off + 24); // 姣忎釜TOC琛ㄩ」鐨勫抚鏁�
		vbrinfo.append("\n   toc frames: ");
		vbrinfo.append(toc_frames);

		off += 26;
		return off;
	}

	private int xinginfoHeader(byte[] b, int off) {
		if (vbrinfo == null)
			vbrinfo = new StringBuilder();
		vbrinfo.append("   vbr header: ");
		vbrinfo.append(new String(b, off, 4));

		trackLength -= framesize;
		int xing_flags = byte2int(b, off + 4);
		if ((xing_flags & 1) == 1) { // track frames
			trackFrames = byte2int(b, off + 8);
			vbrinfo.append("\n track frames: ");
			vbrinfo.append(trackFrames);
			off += 4;
		}
		off += 8; // VBR header ID + flag
		if ((xing_flags & 0x2) != 0) { // track bytes
			trackLength = byte2int(b, off);
			off += 4;
			vbrinfo.append("\n  track bytes: ");
			vbrinfo.append(trackLength);
		}
		if ((xing_flags & 0x4) != 0) { // TOC: 100-byte
			vbrtoc = new byte[100];
			System.arraycopy(b, off, vbrtoc, 0, 100);
			off += 100;
			// System.out.println(" TOC: yes");
		}
		if ((xing_flags & 0x8) != 0) { // VBR quality
			int xing_quality = byte2int(b, off);
			off += 4;
			vbrinfo.append("\n      quality: ");
			vbrinfo.append(xing_quality);
		}
		tocNumber = 100;// TOC鍏�100涓〃椤�
		tocPer = 1; // 姣忎釜琛ㄩ」1瀛楄妭
		tocFactor = 1;
		return off;
	}

	/**
	 * 鍦ㄦ帶鍒跺彴鎵撳嵃甯уご淇℃伅锛堢殑涓�閮ㄥ垎锛夈��
	 */
	// 已修改
	// 返回音乐的头信息
	public String printHeaderInfo() {
		if (headerMask == 0xffe00000) // 鏈垚鍔熻В鏋愯繃甯уご
			return null;
		float duration = trackFrames * frameDuration;
		int m = (int) (duration / 60);
		String strDuration = String.format("%02d:%02d", m, (int) (duration - m * 60 + 0.5));
		if (strBitRate == null)
			strBitRate = String.format("%dK", bitrate[lsf][layer - 1][bitrate_index]);

		StringBuilder info = new StringBuilder();
		if (verID == 0)
			info.append("MPEG-2.5");
		else if (verID == 2)
			info.append("MPEG-2");
		else if (verID == 3)
			info.append("MPEG-1");
		info.append(", Layer ");
		info.append(layer);
		info.append(", ");
		info.append(getSamplingRate());
		info.append("Hz, ");
		info.append(strBitRate);
		if (mode == 0)
			info.append(", Stereo");
		else if (mode == 1)
			info.append(", Joint Stereo");
		else if (mode == 2)
			info.append(", Dual channel");
		else if (mode == 3)
			info.append(", Single channel(Mono)");
		if (mode_extension == 0)
			info.append(", ");
		else if (mode_extension == 1)
			info.append("(I/S), ");
		else if (mode_extension == 2)
			info.append("(M/S), ");
		else if (mode_extension == 3)
			info.append("(I/S & M/S), ");
		info.append(strDuration);
		return info.toString();
	}

	/**
	 * 鍦ㄦ帶鍒跺彴鎵撳嵃鍙彉浣嶇巼锛圴BR锛夋爣绛句俊鎭紙鐨勪竴閮ㄥ垎锛夈��
	 */
	public void printVBRTag() {
		if (vbrinfo != null)
			System.out.println(vbrinfo.toString());
	}

	/**
	 * 鍦ㄦ帶鍒跺彴鎵撳嵃鎾斁杩涘害銆�
	 */
	public void printProgress() {
		float t = framecounter * frameDuration;
		int m = (int) (t / 60);
		float s = t - 60 * m;
		int i = ((int) (100f * framecounter / trackFrames + 0.5) << 2) / 10;
		if (progress == null)
			progress = new StringBuilder(">----------------------------------------");
		if (i == progress_index) {
			progress.replace(i - 1, i + 1, "=>");
			progress_index++;
		}
		System.out.printf("\r#%-5d [%-41s] %02d:%05.2f ", framecounter, progress, m, s);
	}

}

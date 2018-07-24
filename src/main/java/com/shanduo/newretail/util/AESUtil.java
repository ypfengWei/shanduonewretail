package com.shanduo.newretail.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.shanduo.newretail.consts.WxPayConsts;

public class AESUtil {
	 
	/**
	 * 密钥算法
	 */
	private static final String ALGORITHM = "AES";
	/**
	 * 加解密算法/工作模式/填充方式
	 */
	private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS5Padding";
	/**
	 * 生成key
	 */
	private static SecretKeySpec key = new SecretKeySpec(MD5Util.MD5Encode(WxPayConsts.KEY, "UTF-8").toLowerCase().getBytes(), ALGORITHM);
 
	/**
	 * AES加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static String encryptData(String data) throws Exception {
		// 创建密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
		// 初始化
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return Base64Util.encode(cipher.doFinal(data.getBytes()));
	}
 
	/**
	 * AES解密
	 * 
	 * @param base64Data
	 * @return
	 * @throws Exception
	 */
	public static String decryptData(String base64Data) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
		SecretKeySpec keys = new SecretKeySpec(MD5Util.MD5Encode(WxPayConsts.KEY, "UTF-8").toLowerCase().getBytes(), ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, keys);
		return new String(cipher.doFinal(Base64Util.decode(base64Data)));
	}
 
	public static void main(String[] args) throws Exception {
		String A = "hOyuwmNG6bQeCh3iSfiNQ1Rd3ZG3dr9a30+85atqPP+SgtujoSoredOC5S1toAruh9vr"
				+ "/NtLO7EGipoFX01mmjFIur2VLOCgOUHp0SLb4bxD23CgSz4URHk5G3vvfolBZyzebhrtw"
				+ "C82kMCkZuEaQLuL+q3NQuKTUKRtvU7mD3vD8OpDq4RsW5uT7LAde4WCproJJl+/0mmaOM"
				+ "A7O3ntFj63K/RdIWAUPXLcunZCa+IVBpDQ9I4EE88dyu+JJ72uD5k7dsg0TKMNdbTGVel"
				+ "GXbZ6ozpzIt6be99C6oI4a+AXxhY3sK8QxM0ij6Jh85c+HeJ4Hv5jUVzFyr9J0W/460nv"
				+ "yHJMGz6PePRBod/7WS/zorkAQVwgX79wN9XHgoMLhkvVVn9EFeC3O3OevFsD8ImQ6d0YK"
				+ "gzqvqYQ6ZNfAAJXK7MQynKG64Z/4zpjHfxsjWKWXDf5oUOvXKwQKvLqE3lo3FACSKCWUj"
				+ "i/rpN2pb7lAdkGMpYhaVIZ+7v1x0y5JqPV+yS5hPFBuZtXKGwVaK7W3aixtajgiCbEX0g"
				+ "plrmtJnkigdD9qUxBrzLijdDGvhHBZQMpicvDSo33Jf6BE3NEBzoR16eg4T/BBh/9aKXN"
				+ "AL8IPcvEjcoywAiMVWxIynJoqfAHZoIMSLFaAHdb6eUdUssKbVTRLzMGBlIwYMiSPrws4"
				+ "98s+TNv50iyQalbUCLFzfqetbnob1ktUkz9JI0Q4RMpiNk1Lxrt3zZDTVwgq95FobYTqY"
				+ "7Dhx19346cAJLUVTRQXoNpXhU8N1ttt2zYhQCBu1tlefCt45o0cAiH8T2iTcgsmNr7NZe"
				+ "QQ59sJGh7Ec4qdUWXv7ZWtoDed4hoHKdJXbMxYZVUvBx7plm3dICXp4ql6nS43fWEp7SB"
				+ "ExzbCNCNXTHhdMyQcjydFzPf2nO4Qbcdqd76aBrnkmFpUzWIVOqjnewrluqDiOcNhcZuP"
				+ "o6brUpipqaJpWxHoV1xw6UpxGffrRwKY8XYtbNNeYwSqTIhBh71kQE12VxgKi7/Fwodxo"
				+ "3miD78sQwrt5vx0g3iEQr1M1ByJzIn+anqnyAjDoMZ3BwNy8Ky9ilo";
		String B = AESUtil.decryptData(A);
		System.out.println(B);
	}
 
}

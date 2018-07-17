package com.shanduo.newretail.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

	/**
	 * 密钥算法
	 */
	private static final String ALGORITHM = "AES";
	/**
	 * 加解密算法/工作模式/填充方式
	 */
	private static final String ALGORITHM_MODE_PADDING = "AES/ECB/PKCS7Padding";
	/**
	 * 生成key
	 */
	private static final String key = "your password";//此处为测试key，正式环境请替换成商户密钥
	private static SecretKeySpec secretKey = new SecretKeySpec(MD5Util.MD5Encode(key, "UTF-8").toLowerCase().getBytes(), ALGORITHM);
 
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
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
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
		// 创建密码器
		Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_PADDING);
		// 初始化
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(Base64Util.decode(base64Data)));
	}
}

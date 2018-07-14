/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.shanduo.newretail.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * SHA1 class
 *
 * 计算公众平台的消息签名接口.
 */
public class SHA1 {

	/**
	 * 用SHA1算法生成安全签名
	 * @param token 票据
	 * @param timestamp 时间戳
	 * @param nonce 随机字符串
	 * @param encrypt 密文
	 * @return 安全签名
	 * @throws AesException 
	 */
	public static String getSHA1(String token, String timestamp, String nonce) throws AesException
			  {
		try {
			String[] array = new String[] { token, timestamp, nonce };
			StringBuffer sb = new StringBuffer();
			// 字符串排序
			Arrays.sort(array);
			for (int i = 0; i < 3; i++) {
				sb.append(array[i]);
			}
			String str = sb.toString();
			// SHA1签名生成
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes());
			byte[] digest = md.digest();

			StringBuffer hexstr = new StringBuffer();
			String shaHex = "";
			for (int i = 0; i < digest.length; i++) {
				shaHex = Integer.toHexString(digest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexstr.append(0);
				}
				hexstr.append(shaHex);
			}
			return hexstr.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AesException(AesException.ComputeSignatureError);
		}
	}
	/**
    * 加密字符串
    */
   public static String SHA2(String decript) {
       if (decript != null && !decript.isEmpty()) {
           try {
               MessageDigest digest = MessageDigest.getInstance("SHA-1");
               digest.update(decript.getBytes());
               byte messageDigest[] = digest.digest();
               // Create Hex String
               StringBuilder hexString = new StringBuilder();
               // 字节数组转换为 十六进制 数
               for (byte aMessageDigest : messageDigest) {
                   String shaHex = Integer.toHexString(aMessageDigest & 0xFF);
                   if (shaHex.length() < 2) {
                       hexString.append(0);
                   }
                   hexString.append(shaHex);
               }
               return hexString.toString();
           } catch (NoSuchAlgorithmException e) {
               e.printStackTrace();
           }
       }
       return "";
   }
}

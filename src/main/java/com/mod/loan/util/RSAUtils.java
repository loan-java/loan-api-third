package com.mod.loan.util;

import org.apache.commons.lang.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加解密工具类
 */
public class RSAUtils {
	/**
	 * AES加密为base 64 code
	 *
	 * @param content
	 *            待加密的内容
	 * @param key
	 *            加密密钥
	 * @return 加密后的base 64 code
	 * @throws Exception
	 */
	public static String aesEncrypt(String content, String key) throws Exception {
		return encode(aesEncryptToBytes(content, key));
	}

	/**
	 * 解密
	 * 
	 * @param encryptedData
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String aesDecrypt(String encryptedData, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decode(key), "AES"));
		byte[] result = decode(encryptedData);
		byte[] res = cipher.doFinal(result);
		return new String(res, "utf-8");
	}

	/**
	 * base 64 encode
	 *
	 * @param bytes
	 *            待编码的byte[]
	 * @return 编码后的base 64 code
	 */
	private static String encode(byte[] bytes) {
		return new BASE64Encoder().encode(bytes);
	}

	/**
	 * AES加密
	 *
	 * @param content
	 *            待加密的内容
	 * @param key
	 *            加密密钥
	 * @return 加密后的byte[]
	 * @throws Exception
	 */
	private static byte[] aesEncryptToBytes(String content, String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(decode(key), "AES"));
		byte[] result = cipher.doFinal(content.getBytes("utf-8"));
		return result;
	}

	/**
	 * base 64 decode
	 *
	 * @param base64Code
	 *            待解码的base 64 code
	 * @return 解码后的byte[]
	 * @throws Exception
	 */
	private static byte[] decode(String base64Code) throws Exception {
		return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
	}

	/**
	 * 转为64编码
	 */
	public static String base64Encode(String msg) throws Exception {
		return StringUtils.isEmpty(msg) ? null : encode(msg.getBytes("utf-8"));
	}

	public static void main(String[] args) throws Exception {
		String d = base64Encode("JuHksidl92834LidfkLsssDIEN:I9089LISJDkdsl882kDilsDLLo"); // base64后的密钥
		System.out.println(d);
	}
}

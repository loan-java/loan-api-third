package com.mod.loan.util.juhe;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * RSA加解密
 * @author master.yang
 * @version $$Id: RsaUtils, v 0.1 2018/7/6 上午5:31 master.yang Exp $$
 */
public class RsaUtils {

    /**
     * KEY_ALGORITHM
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 加密Key的长度等于1024或者2048
     */
    public static int KEYSIZE = 1024;

    /**
     * 解密时必须按照此分组解密
     */
    public static int decodeLen = KEYSIZE / 8;

    /**
     * 加密时小于117即可
     */
    public static int encodeLen = 100;

    /** *//**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** *//**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;


    /**
     * 从文件中输入流中加载公钥
     *
     * @param path
     * @throws Exception
     */
    public static String loadPublicKeyByFile(String path) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr
     * @throws Exception
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)
            throws Exception {
        try {
            byte[] buffer = Base64.decodeBase64(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @param path
     * @return 是否成功
     * @throws Exception
     */
    public static String loadPrivateKeyByFile(String path) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)
            throws Exception {
        try {
            java.security.Security.addProvider(
                    new org.bouncycastle.jce.provider.BouncyCastleProvider()
            );
            System.out.println(privateKeyStr);

            byte[] buffer = (new BASE64Decoder()).decodeBuffer(privateKeyStr);
            System.out.println("转换Base64完成");

            System.out.println(new String(buffer));

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);

            System.out.println("");

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            System.out.println("获得私钥");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 公钥加密过程
     *
     * @param publicKey
     * @param plainTextData
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData)
            throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            //cipher = Cipher.getInstance("RSA");
            cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding", BouncyCastleProviderProvider.getInstance());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 私钥加密过程
     *
     * @param privateKey
     * @param plainTextData
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData)
            throws Exception {
        if (privateKey == null) {
            throw new Exception("加密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            //cipher = Cipher.getInstance("RSA");
            cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding", BouncyCastleProviderProvider.getInstance());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("加密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 分组解密
     * @param cipherData
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptPrivateKey(byte[] cipherData, String key) throws Exception {
        byte [] buffers = new byte[]{};

        for (int i = 0; i < cipherData.length; i += decodeLen) {
            byte[] subarray = ArrayUtils.subarray(cipherData, i, i + decodeLen);
            byte[] doFinal = decryptByPrivateKey2(subarray, key);
            buffers = ArrayUtils.addAll(buffers, doFinal);
        }
        return new String(buffers, "UTF-8");
    }

    private static byte[] decryptByPrivateKey2(byte[] data, String key) throws Exception {
        // 对密钥解密  取得私钥  
        byte[] keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密   
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 私钥解密过程
     *
     * @param privateKey
     * @param cipherData
     * @return 明文
     * @throws Exception
     */
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData)
            throws Exception {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            //cipher = Cipher.getInstance("RSA");
            cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding", BouncyCastleProviderProvider.getInstance());
            //cipher= Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", BouncyCastleProviderProvider.getInstance());

            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            e.printStackTrace();
            throw new Exception("密文数据已损坏");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("系统异常");
        }
    }

    public static byte[] decryptByGroup(RSAPrivateKey privateKey, byte[] encryptedData)
            throws Exception {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding", BouncyCastleProviderProvider.getInstance());

            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            int inputLen = encryptedData.length;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            e.printStackTrace();
            throw new Exception("密文数据已损坏");
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("系统异常");
        }
    }


    public static byte[] decrypt2(Key key, byte[] raw) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher.init(cipher.DECRYPT_MODE, key);
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;
            while (raw.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 公钥解密过程
     *
     * @param publicKey
     *            公钥
     * @param cipherData
     *            密文数据
     * @return 明文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData)
            throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            //cipher = Cipher.getInstance("RSA");
            cipher= Cipher.getInstance("RSA/ECB/PKCS1Padding", BouncyCastleProviderProvider.getInstance());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    public static void main(String[] args) throws Exception {
        String chipher = "bP2HUW3kkTNXZU4dgT93LsPacnJOrb4no_y5OLWzBTxZ9tZOyMdFUwohzfgq8HLK9ax_6Qx3WZmsz3ITqePHXThBuBU7L0XWmRbGAZvx5lsALYPFc7kSosC2k1X0O8o_wYuouBWwp7FeZyPjGZ931jZNmFG6VtehBkCRVNy1cr8";

        System.out.println(chipher);

        chipher = chipher.replace("-", "+");
        chipher = chipher.replace("_", "/");
        chipher = chipher.replace("\"", "=");

        System.out.println(chipher);

        String privateKey = "MIICXAIBAAKBgQChvfDV0cQTfnwG/7qZ7ZWCzr8QN5yb0jSQuTFoYbtXBvaZytK3RlDz9CLceGH1xFbIlrMF6zu35aCcm7xnyf8lnSsQOhLIjJE10pK5el6dnINm3YzNgZj3pKI91q/tnceeSy/jQt3n63zsQi4XpXy/Spj+K4H3HKxX7P4tn4n0IQIDAQAB" +
                "AoGBAIRzzJb9eknQifcdUw2dH5QIhTTdpdWBNeTSk+B8MHObUzUsgJTv83lkE2xi9S3ThJItvxt4wOXfGUFG0+pW5Cb3GPOIFuylkNFeAbHlhMnkYqVt2IWD+7yPL25sgUuuOmGtHGn6jG1ACKmnQfSC/vJEqBjJKaTO4QQ20G9ol1jxAkEAzj1a+6j5qxQugrtPQN0r9G/cFyM07xB3Y0FNR6PFc1BA+K/B8+A2vmSTKrOEEEUg+QzkQ1SomLM4z+swbHjFNQJBAMjEIT2ei9iQ+hhY5EcgHE+uaO4A6FQsj9RpzxaQXZKlpyKlzFdSe/SOlT3U0OklyrDXFUnSOCWHRDccQsY2bL0CQBOxhCiXwA94A+Dz5eN5uyLCM6/5" +
                "6qoRVnUh3TFEECyssyeMEOcqt8+CZxMixS+Qik99zaYoRVkfdANWn+8bsSkCQGaa" +
                "PXa1UTkDlpzcyQVEdtOOCdggpJtoDrV6wbgBXaD1gb4mR5EU+X5ZZBIucfnFM5o+wYVxpvoe8BDOI54S8G0CQDHFvdmlFtkGH09p0r6bAPQ8ZCXDhywDhoyJJOd/m4yDKysczZeWvhLA8Co0CXOhXojEC/yqX+BWeC1Sxc9sgoQ=";

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQChvfDV0cQTfnwG/7qZ7ZWCzr8QN5yb0jSQuTFoYbtXBvaZytK3RlDz9CLceGH1xFbIlrMF6zu35aCcm7xnyf8lnSsQOhLIjJE10pK5el6dnINm3YzNgZj3pKI91q/tnceeSy/jQt3n63zsQi4XpXy/Spj+K4H3HKxX7P4tn4n0IQIDAQAB";

        // RSAPublicKey rsaPublicKey = RsaUtils.loadPublicKeyByStr(publicKey);

        String privateKeyStr = loadPrivateKeyByFile("/Users/yangyebo/Downloads/rsa_private_dbd_key.pem");

        RSAPrivateKey rsaPrivateKey = loadPrivateKeyByStr(privateKeyStr);

        byte[] plaintext = RsaUtils.decrypt(loadPrivateKeyByStr(privateKey), Base64.decodeBase64(chipher.getBytes()));

        System.out.println(new String(plaintext));
    }
}

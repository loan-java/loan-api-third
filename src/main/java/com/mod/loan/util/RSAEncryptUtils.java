package com.mod.loan.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenanle
 */
public class RSAEncryptUtils {
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
    public static void main(String[] args) throws Exception {
        //生成公钥和私钥
        genKeyPair();
        //加密字符串
        String message = "{\"addBookInfo\":[{\"userName\":\"chenanle\",\"mobile\":\"18358333042\"},{\"userName\":\"cal\",\"mobile\":\"15888281084\"}]}";
//        System.out.println("随机生成的公钥为:" + keyMap.get(0));
//        System.out.println("随机生成的私钥为:" + keyMap.get(1));
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJA8ntnaQMVVazXOf9IiuLd2/kPS+CYxTPP+4sP8v2zd7rV83rBpW27MUYIT3dJbYwoGyWwktJjj2Hgs/O333L6qeRwaQFMvQXaeKPlr8D7j1+3uzjTUKAQ15JUqhRdDQBy21fJcSDonOnXBPwyHdo3yTbDDA/YPGcFVLJnqaruQIDAQAB";
        String priKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMkDye2dpAxVVrNc5/0iK4t3b+Q9L4JjFM8/7iw/y/bN3utXzesGlbbsxRghPd0ltjCgbJbCS0mOPYeCz87ffcvqp5HBpAUy9Bdp4o+WvwPuPX7e7ONNQoBDXklSqFF0NAHLbV8lxIOic6dcE/DId2jfJNsMMD9g8ZwVUsmepqu5AgMBAAECgYEAi+qQNk3g7xjDhYtoiwCq0KPtBTA1jotK48s7DB/H5916idOPKEVoFnCN6/LPbcwfGY/ApdtNrkBMwbMi1HspwneDDm1moZ4uchb1ggBATm6kTp5At7o+CQIHajw+lVHnqjLJljmPexF8SABzsnf1NS1NvD4pvPN9yGt2TZ0mw1ECQQDpJkdLwWX72VCGzVFrk53WFV1OSezqea+5v7AH7enDZdX/mpdrbUaZXhZ7+Xo4LsCuUv3kTIYhAwm0jaJTFc2dAkEA3LdAAiwZV+liedPgCnYeNKZqSuly1LgsfSicoJeGDdtouPZ7uXpqiY0GTRsXlVpsdE3Lgy9LHTN7OMUOw0iJzQJAMShq++QJFrLZMbL0qdP0AYF7rNgVZdLGZEmwWdul4BcGqd/0cSgXLJsT7ovJYzMoWRkSgyJHXYqAb1s1kIMWWQJBAI9Ig0wltrokb2JpUmtYLNm/IwfIr1w6x97ka3GxjDwYsM5KbR2Gw/zRyUJrEQ9LKVcDXdem7xtg3WCIRMIvqA0CQQDP/PTEemYD71tjsFFWwAmSprNadwRnayjaXZCyee4XoVz4kvqX0c+B3OxhZcYHJWymRgUPDGNs/RT6jHBpz1Rk";
        String messageEn = encrypt(message.replace("+",",").replace("/",",").replace("=",","),pubKey);
        System.out.println(message + "\t加密后的字符串为:" + messageEn);
        String messageDe = decrypt(messageEn.replace("-","+").replace(",","/"),priKey);
        System.out.println("还原后的字符串为:" + messageDe);
    }

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }
}

package com.mod.loan.util;

import com.mod.loan.util.juhe.RsaUtils;
import org.apache.commons.codec.binary.Base64;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;

/**
 * base64 工具类
 *
 * @author kk
 */
public class Base64Util {

    public static void main(String[] args) {
        System.out.println("解密后的明文： "+juHeRsaDecode("eKPoR0bJYipW8FqOzDGirn6HNNaUMwqj2n2325_DBw-1aEjvVqW5H5DDUuVzBUZDcM7FRZaxAX5NB4k_FstrLffkdA824BMXAsYNbEkw8mdZp-UdZ3Wd60dK_NYkn3BtWYuowu-ydFrxDHzb-woVTw8v7vtPZOIwXJLaNHoGsmY"));
    }

    /**
     * 聚合 编码
     */
    public static String juHeRsaEncode(String msg) {
        String encode = encode(msg.getBytes());
        return encode.replace("+", "-")
                .replace("/", ",")
                .replace("=", ",");
    }

    /**
     * 聚合 解码
     */
    public static String juHeRsaDecode(String msg) {
        //减号 转成 加号
        msg = msg.replace("-", "+");
        //下划线 转成 斜杠
        msg = msg.replace("_", "/");
        msg = msg.replace("\"", "=");

        //RSA 私钥
        String privateKey = "MIICXQIBAAKBgQCye0F8GhoEpvA44pBEJOr457VCVcy2eacC5D/Rw57+R9gVKjBd" +
                "iJUsYyhj1fsEbnzTuBNxexBs+a3DwDlFUOpSKb5g7Rf7Hf0HFGXFHU5aBTKBtVGm" +
                "lc5RxZfjxjXHz3rMRTieKGwd9mJkMR06oe94JZry3hs42QkxN9mINNLoPwIDAQAB" +
                "AoGAeERH1xk7iQD4hExezBxYXUq9QCrSm+8TlAY73txDibLdrz1Tg+NiKZvTfpqU" +
                "3+KXqI60Q1aateP0rCAeN1AT5BNu8d4O/BhCEHBkM9TasKUFIsVj7tFCYiCUXlWo" +
                "9tW9T40wMwpJoAYJYwN/ZHMT3RfFhWYVit5M8HA0of49jJECQQDZw79fMmWo7gTJ" +
                "/mzZDE52+Y7pKPAF3ErpAbdcqWXqoTkwbrEDnpjJ11+XZe6SW16NBqKKe+9cvXXm" +
                "afDXT/DZAkEA0dHHMC2Uz/i6JodAAYgmmxxCq7/45tXFJzm8SYG8fZ9WI/vsg841" +
                "T6QtMQay0i5VS5qG8q+E0wCR2IK8zYdy1wJBAK8A/P7IRaKpBfg/G8KifTxn+k2u" +
                "tJZBH4J5+p3hFSKmRouBqYg7IJa1GwFUzZGZFDdJqb6ZMxfYurNdWEvw1qECQQCG" +
                "EIc4hndmZ2PyCEtiby9Tvrvu5+vO+tpNVPu08BuTHQM6XNbj5Rd1Os7RW5lY6NgB" +
                "RcxiNrok5GFMOUUQAjLjAkBLAp5DmPChhDOp53hhdND/eXlkPGduHnamczeNsHEH" +
                "ScM1aEE7uIQmOLLL8EcX0L55xBEh6cm0YJwhfjhJdfIE";

        RSAPrivateKey rsaPrivateKey;
        byte[] plaintext;
        try {
            rsaPrivateKey = RsaUtils.loadPrivateKeyByStr(privateKey);
            plaintext = RsaUtils.decryptByGroup(rsaPrivateKey, Base64.decodeBase64(msg));
        } catch (Exception e) {
            return null;
        }

        return new String(plaintext);
    }

    /**
     * base64 解码
     */
    public static String decode(byte[] bytes) {
        return new String(Base64.decodeBase64(bytes), StandardCharsets.UTF_8);
    }

    /**
     * base64 编码
     */
    public static String encode(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes), StandardCharsets.UTF_8);
    }
}

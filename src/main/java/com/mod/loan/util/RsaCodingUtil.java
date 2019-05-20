package com.mod.loan.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;


/**
 * <b>Rsa加解密工具</b><br>
 * <br>
 * 公钥采用X509,Cer格式的<br>
 * 私钥采用PKCS12加密方式的PFX私钥文件<br>
 * 加密算法为1024位的RSA，填充算法为PKCS1Padding<br>
 */
@Slf4j
public final class RsaCodingUtil {


    // ======================================================================================
    // 私钥加密公钥解密
    // ======================================================================================

    /**
     * 根据私钥文件加密
     *
     * @param src
     * @param pfxPath
     * @param priKeyPass
     * @return
     */
    public static String encryptByPriPfxFile(String src, String pfxPath, String priKeyPass) {

        PrivateKey privateKey = RsaReadUtil.getPrivateKeyFromFile(pfxPath, priKeyPass);
        if (privateKey == null) {
            log.info("读取密钥失败,密钥文件:{},密码:{}",pfxPath,priKeyPass);
            return null;
        }
        return encryptByPrivateKey(src, privateKey);
    }

    /**
     * 根据私钥加密
     *
     * @param src
     * @param privateKey
     */
    public static String encryptByPrivateKey(String src, PrivateKey privateKey) {

        byte[] destBytes = rsaByPrivateKey(src.getBytes(), privateKey, Cipher.ENCRYPT_MODE);

        if (destBytes == null) {
            return null;
        }
        return FormatUtil.byte2Hex(destBytes);

    }

    /**
     * 指定Cer公钥路径解密
     *
     * @param src
     * @param pubCerPath
     * @return
     */
    public static String decryptByPubCerFile(String src, String pubCerPath) {
        PublicKey publicKey = RsaReadUtil.getPublicKeyFromFile(pubCerPath);
        if (publicKey == null) {
            return null;
        }
        return decryptByPublicKey(src, publicKey);
    }

    /**
     * 根据公钥文本解密
     *
     * @param src
     * @param pubKeyText
     * @return
     */
    public static String decryptByPubCerText(String src, String pubKeyText) {
        PublicKey publicKey = RsaReadUtil.getPublicKeyByText(pubKeyText);
        if (publicKey == null) {
            return null;
        }
        return decryptByPublicKey(src, publicKey);
    }

    /**
     * 根据公钥解密
     *
     * @param src
     * @param publicKey
     * @return
     */
    public static String decryptByPublicKey(String src, PublicKey publicKey) {

        try {
            byte[] destBytes = rsaByPublicKey(FormatUtil.hex2Bytes(src), publicKey, Cipher.DECRYPT_MODE);
            if (destBytes == null) {
                return null;
            }
            return new String(destBytes, RsaConst.ENCODE);
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    // ======================================================================================
    // 公私钥算法
    // ======================================================================================

    /**
     * 公钥算法
     *
     * @param srcData   源字节
     * @param publicKey 公钥
     * @param mode      加密 OR 解密
     * @return
     */
    private static byte[] rsaByPublicKey(byte[] srcData, PublicKey publicKey, int mode) {
        try {
            Cipher cipher = Cipher.getInstance(RsaConst.RSA_CHIPER);
            cipher.init(mode, publicKey);
            // 分段加密
            int blockSize = (mode == Cipher.ENCRYPT_MODE) ? RsaConst.ENCRYPT_KEYSIZE : RsaConst.DECRYPT_KEYSIZE;
            byte[] encryptedData = null;
            for (int i = 0; i < srcData.length; i += blockSize) {
                // 注意要使用2的倍数，否则会出现加密后的内容再解密时为乱码
                byte[] doFinal = cipher.doFinal(subarray(srcData, i, i + blockSize));
                encryptedData = addAll(encryptedData, doFinal);
            }
            return encryptedData;

        } catch (NoSuchAlgorithmException e) {
            log.error("公钥算法-不存在的解密算法:", e);
        } catch (NoSuchPaddingException e) {
            log.error("公钥算法-无效的补位算法:", e);
        } catch (IllegalBlockSizeException e) {
            log.error("公钥算法-无效的块大小:", e);
        } catch (BadPaddingException e) {
            log.error("公钥算法-补位算法异常:", e);
        } catch (InvalidKeyException e) {
            log.error("公钥算法-无效的私钥:", e);
        }
        return null;
    }

    /**
     * 私钥算法
     *
     * @param srcData    源字节
     * @param privateKey 私钥
     * @param mode       加密 OR 解密
     * @return
     */
    private static byte[] rsaByPrivateKey(byte[] srcData, PrivateKey privateKey, int mode) {
        try {
            Cipher cipher = Cipher.getInstance(RsaConst.RSA_CHIPER);
            cipher.init(mode, privateKey);
            // 分段加密
            int blockSize = (mode == Cipher.ENCRYPT_MODE) ? RsaConst.ENCRYPT_KEYSIZE : RsaConst.DECRYPT_KEYSIZE;
            byte[] decryptData = null;

            for (int i = 0; i < srcData.length; i += blockSize) {
                byte[] doFinal = cipher.doFinal(subarray(srcData, i, i + blockSize));

                decryptData = addAll(decryptData, doFinal);
            }
            return decryptData;
        } catch (NoSuchAlgorithmException e) {
			log.error("私钥算法-不存在的解密算法:", e);
        } catch (NoSuchPaddingException e) {
            log.error("私钥算法-无效的补位算法:", e);
        } catch (IllegalBlockSizeException e) {
            log.error("私钥算法-无效的块大小:", e);
        } catch (BadPaddingException e) {
            log.error("私钥算法-补位算法异常:", e);
        } catch (InvalidKeyException e) {
            log.error("私钥算法-无效的私钥:", e);
        }
        return null;
    }

    public static byte[] subarray(byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        int newSize = endIndexExclusive - startIndexInclusive;

        if (newSize <= 0) {
            return new byte[0];
        }

        byte[] subarray = new byte[newSize];

        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);

        return subarray;
    }

    private static byte[] addAll(byte[] array1, byte[] array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    private static byte[] clone(byte[] array) {
        if (array == null) {
            return null;
        }
        return (byte[]) array.clone();
    }
}

package com.mod.loan.util;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Enumeration;

/**
 * <b>公私钥读取工具</b><br>
 * <br>
 * @author Administrator
 */
@Slf4j
public class RsaReadUtil {

    /**
     * 根据Cer文件读取公钥
     *
     * @param pubCerPath 公钥路径
     * @return 公钥
     */
    public static PublicKey getPublicKeyFromFile(String pubCerPath) {
        FileInputStream pubKeyStream = null;
        try {
            pubKeyStream = new FileInputStream(pubCerPath);
            byte[] reads = new byte[pubKeyStream.available()];
            pubKeyStream.read(reads);
            return getPublicKeyByText(new String(reads));
        } catch (FileNotFoundException e) {
             log.error("公钥文件不存在:", e);
        } catch (IOException e) {
             log.error("公钥文件读取失败:", e);
        } finally {
            if (pubKeyStream != null) {
                try {
                    pubKeyStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 根据公钥Cer文本串读取公钥
     *
     * @param pubKeyText 公钥内容
     * @return 公钥
     */
    static PublicKey getPublicKeyByText(String pubKeyText) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(RsaConst.KEY_X509);
            BufferedReader br = new BufferedReader(new StringReader(pubKeyText));
            String line ;
            StringBuilder keyBuffer = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("-")) {
                    keyBuffer.append(line);
                }
            }
            Certificate certificate = certificateFactory.generateCertificate(new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(keyBuffer.toString())));
            return certificate.getPublicKey();
        } catch (Exception e) {
            // log.error("解析公钥内容失败:", e);
        }
        return null;
    }

    /**
     * 根据私钥路径读取私钥
     *
     * @param pfxPath 密钥文件路径
     * @param priKeyPass 密码
     * @return 密钥
     */
    public static PrivateKey getPrivateKeyFromFile(String pfxPath, String priKeyPass) {
        InputStream priKeyStream = null;
        try {
//            priKeyStream = RsaReadUtil.class.getClassLoader().getResourceAsStream(pfxPath);
			priKeyStream = new FileInputStream(pfxPath);
            byte[] reads = new byte[priKeyStream.available()];
            priKeyStream.read(reads);
            String str = new String(reads, "UTF-8");
            log.info("私钥:{}", str);
            return getPrivateKeyByStream(reads, priKeyPass);
        } catch (Exception e) {
            log.error("解析文件，读取私钥失败:", e);
        } finally {
            if (priKeyStream != null) {
                try {
                    priKeyStream.close();
                } catch (Exception e) {
                    log.error("解析文件，读取私钥失败:", e);
                }
            }
        }
        return null;
    }

    /**
     * 根据PFX私钥字节流读取私钥
     *
     * @param pfxBytes 密钥字节
     * @param priKeyPass 密码
     * @return 密钥key
     */
    public static PrivateKey getPrivateKeyByStream(byte[] pfxBytes, String priKeyPass) {
        try {
            KeyStore ks = KeyStore.getInstance(RsaConst.KEY_PKCS12);
            char[] charPriKeyPass = priKeyPass.toCharArray();
            ks.load(new ByteArrayInputStream(pfxBytes), charPriKeyPass);
            Enumeration<String> aliasEnum = ks.aliases();
            String keyAlias = null;
            if (aliasEnum.hasMoreElements()) {
                keyAlias = aliasEnum.nextElement();
            }
            return (PrivateKey) ks.getKey(keyAlias, charPriKeyPass);
        } catch (IOException e) {
            // 加密失败
             log.error("解析文件，读取私钥失败:", e);
        } catch (KeyStoreException e) {
             log.error("私钥存储异常:", e);
        } catch (NoSuchAlgorithmException e) {
             log.error("不存在的解密算法:", e);
        } catch (CertificateException e) {
             log.error("证书异常:", e);
        } catch (UnrecoverableKeyException e) {
             log.error("不可恢复的秘钥异常", e);
        }
        return null;
    }
}

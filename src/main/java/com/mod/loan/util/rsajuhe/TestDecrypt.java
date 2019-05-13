package com.mod.loan.util.rsajuhe;

import org.apache.commons.codec.binary.Base64;

import java.security.interfaces.RSAPrivateKey;

public class TestDecrypt {

    public static void main(String[] args) throws Exception {

        //密文
        String ciphertext = "Fp_C3LIIiUqltwA8Eo6WJoCKAxpgL6LYVKyIKilP1J-sq0lHykzugjz1I4qPw82sW7IdoOuLyOOUc5eLyyGDdoKRZ63dq3nVgo3KtmKTi0hZAc9tq_5XJTPiMjhOW4o_OianYdc0ZBtWj_V8qDX2-xPZ60RopAnDZjd__pCVjukl7P3yxgq0Pzw7sQD7NJ8SoWxtHfuu0srJXj5TcD-8seJbGMIJ90r3nS-4GhkfolRcAgQnAefXLvhgW9s13fxw1uXb12g78aNxvo58KLTn1961XXl0k0yglpjJl1kbP7UwUX7wqXGoHBr-WN8bnJOtbxrucZjOF3xQa4_WuG0jUHTH_WUXSs6DllT0VXn1FuVL3sXbybJ5Q8uIjsmTNBka1qq1ADtTBPZ1uqjp9iPh6dkLkoJdF69VYjeoxP20W4L4iqVDfGrPh0BNXEB2QnCOmngzWhg3dQl6q6-gvPeb-qPJE4peRFIiU7wGIb3zgXhVibvxROxhZR-VE2GG76ELqriCBLyyrcATvpB4SkCjxhMfxVgLFpfLxnlcPAZObKiDprBS_6LzMK-QMa4YWqvfLYDZhrfr0hhxs9cPg6kn_7lK1QkqWjlFBhOf0LVKi4HXzsOrinqbc4sXq0DyM0ZU5YnqOEd2VEufBsQyRXp4uqlp98oqYfaSbLW2JDIC-SdYoWwuSLFHKuzyGnkqiyP_NHVzkLLQHD4Ep7MBEvf7WYtL9PMvnOvu7UR1M06cm6u7g8RuVLqcptc4WOXVr-YhJf2Q1fJtsJoG1__KsexZ2E2R0UsPBTe_3RPKqXnzDNyBIAouwtfoGVkpmhRRGiRU0EJeVYH356IvrUjU9MxF2TgmrHBVrqdC0tpI-vnPR6EBBDucoWrB_xA3khTQ-Y5NLNn-3B8WMbgRDPeN0TVso1gP9SPNfI7D9daehtvv26sx4WgJLOa4vd_L_dFiPYK4Ab1wk_pATpv9fs9gPDcuQhUx1A4ew0fozZgGqWrG6SFZLJa7UxpVn--Zo6Mu_Xwi";

        System.out.println("ciphertext is: " + ciphertext);

        //减号 转成 加号
        ciphertext = ciphertext.replace("-", "+");
        //下划线 转成 斜杠
        ciphertext = ciphertext.replace("_", "/");
        ciphertext = ciphertext.replace("\"", "=");

        System.out.println("ciphertext after convert is: " + ciphertext);

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

        System.out.println("privateKey is: " + privateKey);

        RSAPrivateKey rsaPrivateKey = RsaUtils.loadPrivateKeyByStr(privateKey);

        byte[] plaintext = RsaUtils.decryptByGroup(rsaPrivateKey, Base64.decodeBase64(ciphertext));

        System.out.println("plaintext convert is: " + new String(plaintext));
    }
}

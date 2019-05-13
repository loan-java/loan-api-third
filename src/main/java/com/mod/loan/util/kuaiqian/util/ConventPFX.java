package com.mod.loan.util.kuaiqian.util;

import com.mod.loan.config.Constant;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class ConventPFX {
    public static final String PKCS12 = "PKCS12";
    public static final String JKS = "JKS";

    public static void coverToKeyStore() {
        try {
            KeyStore inputKeyStore = KeyStore.getInstance(PKCS12);
            FileInputStream fis = new FileInputStream(Constant.kuaiQianPriKeyPath);
            char[] nPassword = Constant.kuaiQianKeyPassword.toCharArray();

            inputKeyStore.load(fis, nPassword);
            fis.close();
            KeyStore outputKeyStore = KeyStore.getInstance(JKS);
            outputKeyStore.load(null, Constant.kuaiQianKeyPassword.toCharArray());
            Enumeration enums = inputKeyStore.aliases();

            while (enums.hasMoreElements()) {
                String keyAlias = (String) enums.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
                if (inputKeyStore.isKeyEntry(keyAlias)) {
                    Key key = inputKeyStore.getKey(keyAlias, nPassword);
                    Certificate[] certChain = inputKeyStore.getCertificateChain(keyAlias);

                    outputKeyStore.setKeyEntry(keyAlias, key, Constant.kuaiQianKeyPassword.toCharArray(), certChain);
                }
            }

            FileOutputStream out = new FileOutputStream(Constant.kuaiQianJksPath);
            outputKeyStore.store(out, nPassword);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        coverToKeyStore();
    }

}

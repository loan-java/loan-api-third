package com.mod.loan.util.juhe;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author master.yang
 * @version $$Id: BouncyCastleProviderProvider, v 0.1 2018/7/5 下午7:56 master.yang Exp $$
 */
public class BouncyCastleProviderProvider {

    private static BouncyCastleProvider bouncyCastleProvider = null;

    public static synchronized BouncyCastleProvider getInstance() {
        if (bouncyCastleProvider == null) {
            bouncyCastleProvider = new BouncyCastleProvider();
        }
        return bouncyCastleProvider;
    }
}

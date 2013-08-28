package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.Inject;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;
import com.timmattison.crypto.ecc.interfaces.ECCSignatureFactory;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/13/13
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinECCSignatureFactory implements ECCSignatureFactory {
    private final ECCSignatureFactory eccSignatureFactory;

    @Inject
    public BitcoinECCSignatureFactory(ECCSignatureFactory eccSignatureFactory) {
        this.eccSignatureFactory = eccSignatureFactory;
    }

    @Override
    public ECCSignature create(ECCParameters parameters, BigInteger r, BigInteger s, ECCPoint Qu) {
        ECCSignature signature = eccSignatureFactory.create(parameters, r, s, Qu);

        return signature;
    }
}

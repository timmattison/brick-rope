package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.crypto.ecc.fp.ECPointFp;
import com.timmattison.crypto.ecc.fp.ECSignatureFp;
import com.timmattison.crypto.ecc.fp.X9ECParameters;
import com.timmattison.cryptocurrency.factories.ECCSignatureFactory;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/13/13
 * Time: 6:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinECCSignatureFactory implements ECCSignatureFactory {
    @Override
    public ECSignatureFp create(X9ECParameters parameters, BigInteger r, BigInteger s, BigInteger dU) {
        // Calculate Qu = (xU, yU) = dU * G
        ECPointFp Qu = parameters.getG().multiply(dU);

        ECSignatureFp signature = new ECSignatureFp(parameters, r, s, Qu);

        return signature;
    }
}

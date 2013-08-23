package com.timmattison.cryptocurrency.factories;

import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/14/13
 * Time: 6:56 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCSignatureFactory {
    /**
     * @param parameters
     * @param r
     * @param s
     * @param dU the public key
     * @return
     */
    ECCSignature create(ECCParameters parameters, BigInteger r, BigInteger s, BigInteger dU);

    ECCSignature create(ECCParameters parameters, BigInteger r, BigInteger s, ECCPoint qu);
}

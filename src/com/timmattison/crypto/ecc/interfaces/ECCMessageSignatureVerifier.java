package com.timmattison.crypto.ecc.interfaces;

import com.timmattison.crypto.ecc.fp.ECPointFp;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/22/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCMessageSignatureVerifier {
    boolean signatureValid(ECCParameters eccParameters, byte[] messageBytes, ECCPoint qU, BigInteger r, BigInteger s);
}

package com.timmattison.crypto.ecc;

import com.timmattison.crypto.ecc.fp.ECPointFp;
import com.timmattison.crypto.ecc.fp.X9ECParameters;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/21/13
 * Time: 7:22 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCSignature {
    public BigInteger getR();

    public BigInteger getS();

    public ECCParameters getECCParameters();

    public BigInteger getN();

    public ECCPoint getG();

    public ECCPoint getQu();
}

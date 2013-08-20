package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.ECCParameters;
import com.timmattison.crypto.ecc.ECCPoint;
import com.timmattison.crypto.ecc.ECCSignature;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/21/13
 * Time: 7:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECSignatureFp implements ECCSignature {
    private BigInteger r;
    private BigInteger s;
    private ECCPoint Qu;
    private ECCParameters eccParameters;

    public ECSignatureFp() {
    }

    @AssistedInject
    public ECSignatureFp(@Assisted ECCParameters eccParameters, @Assisted("r") BigInteger r, @Assisted("s") BigInteger s, @Assisted ECPointFp Qu) {
        this.eccParameters = eccParameters;
        this.r = r;
        this.s = s;
        this.Qu = Qu;
    }

    public BigInteger getR() {
        return r;
    }

    public BigInteger getS() {
        return s;
    }

    public ECCParameters getECCParameters() {
        return eccParameters;
    }

    public BigInteger getN() {
        return eccParameters.getN();
    }

    public ECCPoint getG() {
        return eccParameters.getG();
    }

    public ECCPoint getQu() {
        return Qu;
    }
}
package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;

import javax.inject.Inject;
import java.math.BigInteger;

// This code is based off of the Javascript implementation found here - http://www-cs-students.stanford.edu/~tjw/jsbn/

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

    @Inject
    public ECSignatureFp(@Assisted("eccParameters") ECCParameters eccParameters, @Assisted("r") BigInteger r, @Assisted("s") BigInteger s, @Assisted("Qu") ECCPoint Qu) {
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

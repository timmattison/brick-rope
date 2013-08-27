package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
import com.timmattison.crypto.ecc.interfaces.ECCCurve;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECParametersFp implements ECCParameters {
    private ECCCurve curve;
    private ECCPoint g;
    private BigInteger n;
    private BigInteger h;

    public ECParametersFp() {
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ECCParameters)) {
            return false;
        }

        ECCParameters other = (ECCParameters) obj;

        return other.getCurve().equals(this.getCurve()) && other.getN().equals(this.getN()) && other.getG().equals(this.getG()) && other.getH().equals(this.getH());
    }

    @AssistedInject
    public ECParametersFp(@Assisted("curve") ECCCurve curve, @Assisted("g") ECCPoint g, @Assisted("n") BigInteger n, @Assisted("h") BigInteger h) {
        this.curve = curve;
        this.g = g;
        this.n = n;
        this.h = h;
    }

    public ECCCurve getCurve() {
        return this.curve;
    }

    public ECCPoint getG() {
        return this.g;
    }

    public BigInteger getN() {
        return this.n;
    }

    public BigInteger getH() {
        return this.h;
    }

    @Override
    public ECCFieldType getECCFieldType() {
        return ECCFieldType.Fp;
    }
}

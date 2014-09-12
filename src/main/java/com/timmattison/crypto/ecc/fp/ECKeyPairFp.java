package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
import com.timmattison.crypto.ecc.interfaces.ECCKeyPair;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;

import javax.inject.Inject;
import java.math.BigInteger;

// This code is based off of the Javascript implementation found here - http://www-cs-students.stanford.edu/~tjw/jsbn/

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECKeyPairFp implements ECCKeyPair {
    private BigInteger d;
    private ECCPoint q;
    private ECCParameters eccParameters;

    public ECKeyPairFp() {
    }

    @Inject
    public ECKeyPairFp(@Assisted("eccParameters") ECCParameters eccParameters, @Assisted("dU") BigInteger d) throws Exception {
        this.eccParameters = eccParameters;
        this.d = d;

        // Does d meet the criteria for a secret key?  It must be [1, n-1].
        if ((d.compareTo(BigInteger.ONE) < 0) || (d.compareTo(this.eccParameters.getN()) >= 0)) {
            throw new Exception("Private key d is not in [1, n-1]");
        }

        // Calculate q = (x, y) = d * G
        this.q = this.eccParameters.getG().multiply(d);

        validateQ();
    }

    private void validateQ() throws Exception {
        BigInteger x = q.getX().toBigInteger();
        BigInteger y = q.getY().toBigInteger();

        // SEC 1: 3.2.2.1 step 1 - Is Q zero (x, y) == (0, 0)?
        if ((x.compareTo(BigInteger.ZERO) == 0) && (y.compareTo(BigInteger.ZERO) == 0)) {
            // Yes, throw an exception
            throw new Exception("Failed at SEC 1: 3.2.2.1 step 1 - Q == 0");
        }

        BigInteger xQ3 = x.pow(3);
        BigInteger a = eccParameters.getCurve().getA().toBigInteger();
        BigInteger axQ = a.multiply(x);
        BigInteger b = eccParameters.getCurve().getB().toBigInteger();
        BigInteger p = eccParameters.getCurve().getP();

        BigInteger yQ2 = y.pow(2).mod(p);

        BigInteger xQ3PlusAxQ = xQ3.add(axQ);
        BigInteger xQ3PlusAxQPlusB = xQ3PlusAxQ.add(b);
        BigInteger checkValue = xQ3PlusAxQPlusB.mod(p);

        // SEC 1: 3.2.2.1 step 2 - Does xQ^3 + axQ + b (mod p) == yQ^2?
        if (checkValue.compareTo(yQ2) != 0) {
            // No, throw an exception
            throw new Exception("Failed at SEC 1: 3.2.2.1 step 2 - yQ2 != (xQ3 + axQ + b) mod p");
        }

        // SEC 1: 3.2.2.1 step 3 - Only for F_2^m, not implemented here

        // SEC 1: 3.2.2.1 step 4 - Is nQ == 0?
        ECCPoint checkPoint = q.multiply(eccParameters.getN());

        if ((checkPoint.getX().toBigInteger().compareTo(BigInteger.ZERO) != 0) || (checkPoint.getY().toBigInteger().compareTo(BigInteger.ZERO) != 0)) {
            // No, throw an exception
            throw new Exception("Failed at SEC 1: 3.2.2.1 step 4 - nQ != 0");
        }

        // At this point Q is valid
    }

    /**
     * The private key.  Must be in the interval [1, n-1] (notation indicates inclusive of 1 and n-1)
     *
     * @return
     */
    public BigInteger getD() {
        return d;
    }

    /**
     * The public key
     *
     * @return
     */
    public ECCPoint getQ() {
        return q;
    }

    /**
     * The N value for the curve parameters used with this key
     *
     * @return
     */
    public BigInteger getN() {
        return eccParameters.getN();
    }

    /**
     * The G value for the curve parameters used with this key
     *
     * @return
     */
    public ECCPoint getG() {
        return eccParameters.getG();
    }

    /**
     * A convenience method to get the x9EC parameters for the curve used with this key
     *
     * @return
     */
    public ECCParameters getECCParameters() {
        return eccParameters;
    }

    @Override
    public ECCFieldType getECCFieldType() {
        return ECCFieldType.Fp;
    }
}

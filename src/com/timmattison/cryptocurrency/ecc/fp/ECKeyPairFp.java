package com.timmattison.cryptocurrency.ecc.fp;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECKeyPairFp {
    private final BigInteger d;
    private final ECPointFp q;
    private final X9ECParameters x9ECParameters;

    public ECKeyPairFp(X9ECParameters x9ECParameters, BigInteger d) throws Exception {
        this.x9ECParameters = x9ECParameters;
        this.d = d;

        // Does d meet the criteria for a secret key?  It must be [1, n-1].
        if ((d.compareTo(BigInteger.ONE) < 0) || (d.compareTo(x9ECParameters.getN().subtract(BigInteger.ONE)) > 0)) {
            throw new Exception("Private key d is not in [1, n-1]");
        }

        // Calculate q = (x, y) = d * G
        this.q = this.x9ECParameters.getG().multiply(d);

        //validateQ();
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
        BigInteger a = x9ECParameters.getCurve().getA().toBigInteger();
        BigInteger axQ = a.multiply(x);
        BigInteger b = x9ECParameters.getCurve().getB().toBigInteger();
        BigInteger p = x9ECParameters.getCurve().getP();

        BigInteger yQ2 = y.pow(2);

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
        ECPointFp checkPoint = q.multiply(x9ECParameters.getN());

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
    public ECPointFp getQ() {
        return q;
    }

    /**
     * The N value for the curve parameters used with this key
     *
     * @return
     */
    public BigInteger getN() {
        return x9ECParameters.getN();
    }

    /**
     * The G value for the curve parameters used with this key
     *
     * @return
     */
    public ECPointFp getG() {
        return x9ECParameters.getG();
    }

    /**
     * A convenience method to get the x9EC parameters for the curve used with this key
     *
     * @return
     */
    public X9ECParameters getX9ECParameters() {
        return x9ECParameters;
    }
}

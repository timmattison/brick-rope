package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.bitcoin.test.BigIntegerHelper;
import com.timmattison.crypto.ecc.interfaces.ECCFieldElement;
import com.timmattison.crypto.ecc.interfaces.ECCFieldElementFactory;
import com.timmattison.crypto.ecc.interfaces.ECCFieldType;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 6:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECFieldElementFp implements ECCFieldElement {
    private ECCFieldElementFactory eccFieldElementFactory;
    private BigInteger x;
    private BigInteger q;
    private BigInteger trueX;

    public ECFieldElementFp() {
    }

    @AssistedInject
    public ECFieldElementFp(ECCFieldElementFactory eccFieldElementFactory, @Assisted("q") BigInteger q, @Assisted("x") BigInteger x) {
        this.eccFieldElementFactory = eccFieldElementFactory;

        this.x = x;
        // TODO if(x.compareTo(q) >= 0) error
        this.q = q;
    }

    @Override
    public boolean equals(Object obj) {
        // Is the other object an ECCFieldElement?
        if (!(obj instanceof ECCFieldElement)) {
            // No, they cannot be equal
            return false;
        }

        ECCFieldElement other = (ECCFieldElement) obj;

        if (other.getX().equals(getX()) && other.getQ().equals(getQ())) {
            // X and Q are equal.  The objects are equal.
            return true;
        } else {
            // Something didn't match.  They are not equal.
            return false;
        }
    }

    @Override
    public String toString() {
        return "[" + getX() + ", " + getQ() + "]";
    }

    public boolean equals(ECCFieldElement other) {
        if (other == this) return true;
        return (this.getQ().equals(other.getQ()) && this.getX().equals(other.getX()));
    }

    public BigInteger toBigInteger() {
        return this.getX();
    }

    public ECCFieldElement negate() {
        return eccFieldElementFactory.create(this.getQ(), this.getX().negate().mod(this.getQ()));
    }

    public ECCFieldElement add(ECCFieldElement b) {
        return eccFieldElementFactory.create(this.getQ(), this.getX().add(b.toBigInteger()).mod(this.getQ()));
    }

    public ECCFieldElement subtract(ECCFieldElement b) {
        return eccFieldElementFactory.create(this.getQ(), this.getX().subtract(b.toBigInteger()).mod(this.getQ()));
    }

    public ECCFieldElement multiply(ECCFieldElement b) {
        return eccFieldElementFactory.create(this.getQ(), this.getX().multiply(b.toBigInteger()).mod(this.getQ()));
    }

    public ECCFieldElement square() {
        return eccFieldElementFactory.create(this.getQ(), BigIntegerHelper.squareBigInteger(x).mod(this.getQ()));
    }

    public ECCFieldElement divide(ECCFieldElement b) {
        return eccFieldElementFactory.create(this.getQ(), this.getX().multiply(b.toBigInteger().modInverse(this.getQ())).mod(this.getQ()));
    }

    @Override
    public BigInteger getQ() {
        return q;
    }

    @Override
    public BigInteger getX() {
        if (x == null) {
            return null;
        }

        if (trueX == null) {
            trueX = x.mod(getQ());
        }

        return trueX;
    }

    @Override
    public ECCFieldType getECCFieldType() {
        return ECCFieldType.Fp;
    }
}

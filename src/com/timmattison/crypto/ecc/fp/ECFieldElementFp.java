package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.bitcoin.test.BigIntegerHelper;
import com.timmattison.crypto.ecc.ECCFieldElement;
import com.timmattison.crypto.ecc.ECCFieldElementFactory;
import com.timmattison.crypto.ecc.ECCFieldType;

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
        return (this.q.equals(other.getQ()) && this.x.equals(other.getX()));
    }

    public BigInteger toBigInteger() {
        return this.x;
    }

    public ECCFieldElement negate() {
        return eccFieldElementFactory.create(this.q, this.x.negate().mod(this.q));
    }

    public ECCFieldElement add(ECCFieldElement b) {
        return eccFieldElementFactory.create(this.q, this.x.add(b.toBigInteger()).mod(this.q));
    }

    public ECCFieldElement subtract(ECCFieldElement b) {
        return eccFieldElementFactory.create(this.q, this.x.subtract(b.toBigInteger()).mod(this.q));
    }

    public ECCFieldElement multiply(ECCFieldElement b) {
        return eccFieldElementFactory.create(this.q, this.x.multiply(b.toBigInteger()).mod(this.q));
    }

    public ECCFieldElement square() {
        return eccFieldElementFactory.create(this.q, BigIntegerHelper.squareBigInteger(x).mod(this.q));
    }

    public ECCFieldElement divide(ECCFieldElement b) {
        return eccFieldElementFactory.create(this.q, this.x.multiply(b.toBigInteger().modInverse(this.q)).mod(this.q));
    }

    @Override
    public BigInteger getQ() {
        return q;
    }

    @Override
    public BigInteger getX() {
        return x;
    }

    @Override
    public ECCFieldType getECCFieldType() {
        return ECCFieldType.Fp;
    }
}

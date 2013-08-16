package com.timmattison.crypto.ecc;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/16/13
 * Time: 7:30 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCParametersFactory {
    ECCParameters create(ECCCurve curve, ECCPoint g, BigInteger n, BigInteger h);
}

package com.timmattison.crypto.ecc.tests;

import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.*;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/21/13
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECCTestHelper {
    public static ECCPoint getPoint(Injector injector, ECCParameters parameters, BigInteger x, BigInteger y) {
        ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);
        ECCCurve curve = parameters.getCurve();
        ECCFieldElement xFieldElement = curve.fromBigInteger(x);
        ECCFieldElement yFieldElement = curve.fromBigInteger(y);
        return eccPointFactory.create(curve, xFieldElement, yFieldElement);
    }

}

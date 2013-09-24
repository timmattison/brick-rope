package com.timmattison.crypto.ecc.factories;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
import com.timmattison.crypto.ecc.fp.ECCurveFp;
import com.timmattison.crypto.ecc.interfaces.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/24/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECCurveFpFactory implements ECCCurveFactory {
    private final Map<String, BigInteger> invertedPValues = new HashMap<String, BigInteger>();
    private final ECCPointFactory eccPointFactory;
    private final ECCFieldElementFactory eccFieldElementFactory;

    @Inject
    public ECCurveFpFactory(ECCPointFactory eccPointFactory, ECCFieldElementFactory eccFieldElementFactory) {
        this.eccPointFactory = eccPointFactory;
        this.eccFieldElementFactory = eccFieldElementFactory;
    }

    @Override
    public ECCCurve create(@Assisted("p") BigInteger p, @Assisted("order") BigInteger order, @Assisted("a") BigInteger a, @Assisted("b") BigInteger b) {
        ECCCurve curve = new ECCurveFp(eccPointFactory, eccFieldElementFactory, p, order, a, b);

        // Do we have the inverted P value for this curve?
        if(!invertedPValues.containsKey(curve.toString())) {
            // No, calculate it and add it
            curve
        }
    }
}

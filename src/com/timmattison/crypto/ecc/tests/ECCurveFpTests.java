package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.ECCCurve;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/21/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECCurveFpTests {
    Injector injector = Guice.createInjector(new ECCTestModule());

    private ECCParameters getSmallCurve1Parameters() {
        return injector.getInstance(TestCurveParameters.class).getSmallCurve1Parameters();
    }

    @Test
    public void testInfinityIsInfinity() {
        ECCParameters eccParameters = getSmallCurve1Parameters();
        ECCCurve eccCurve = eccParameters.getCurve();
        ECCPoint infinity = eccCurve.getInfinity();

        Assert.assertTrue(infinity.isInfinity());
    }
}

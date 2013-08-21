package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.ECCCurveFactory;
import com.timmattison.crypto.ecc.ECCParameters;
import com.timmattison.crypto.ecc.ECCPoint;
import com.timmattison.crypto.ecc.ECCPointFactory;
import com.timmattison.crypto.ecc.fp.TestCurveParameters;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/21/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECPointFpTests {
    Injector injector = Guice.createInjector(new ECCTestModule());
    ECCCurveFactory eccCurveFactory = injector.getInstance(ECCCurveFactory.class);
    ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);

    private ECCParameters getSmallCurve1Parameters() {
        return injector.getInstance(TestCurveParameters.class).getSmallCurve1Parameters();
    }

    private ECCParameters getSmallCurve2Parameters() {
        return injector.getInstance(TestCurveParameters.class).getSmallCurve2Parameters();
    }

    @Test
    public void testShouldBeEqual1() {
        ECCPoint eccPoint1 = ECCTestHelper.getPoint(injector, getSmallCurve1Parameters(), BigInteger.valueOf(5), BigInteger.valueOf(5));
        ECCPoint eccPoint2 = ECCTestHelper.getPoint(injector, getSmallCurve1Parameters(), BigInteger.valueOf(5), BigInteger.valueOf(5));

        Assert.assertEquals(eccPoint1, eccPoint2);
    }

    @Test
    public void testShouldNotBeEqual1() {
        ECCPoint eccPoint1 = ECCTestHelper.getPoint(injector, getSmallCurve1Parameters(), BigInteger.valueOf(5), BigInteger.valueOf(5));
        ECCPoint eccPoint2 = ECCTestHelper.getPoint(injector, getSmallCurve2Parameters(), BigInteger.valueOf(5), BigInteger.valueOf(5));

        Assert.assertNotEquals(eccPoint1, eccPoint2);
    }

    @Test
    public void testShouldNotBeEqual2() {
        ECCPoint eccPoint1 = ECCTestHelper.getPoint(injector, getSmallCurve1Parameters(), BigInteger.valueOf(5), BigInteger.valueOf(5));
        ECCPoint eccPoint2 = ECCTestHelper.getPoint(injector, getSmallCurve1Parameters(), BigInteger.valueOf(5), BigInteger.valueOf(4));

        Assert.assertNotEquals(eccPoint1, eccPoint2);
    }
}

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

   @Test
    public void testInfinityIsInfinity() {
        ECCParameters eccParameters = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCCurve eccCurve = eccParameters.getCurve();
        ECCPoint infinity = eccCurve.getInfinity();

        Assert.assertTrue(infinity.isInfinity());
    }

    @Test
    public void testShouldEqual1() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCParameters eccParameters2 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();
        ECCCurve eccCurve2 = eccParameters2.getCurve();

        Assert.assertEquals(eccCurve1, eccCurve2);
    }

    @Test
    public void testShouldNotEqual() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCParameters eccParameters2 = ECCTestHelper.getSmallCurve2Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();
        ECCCurve eccCurve2 = eccParameters2.getCurve();

        Assert.assertNotEquals(eccCurve1, eccCurve2);
    }
}

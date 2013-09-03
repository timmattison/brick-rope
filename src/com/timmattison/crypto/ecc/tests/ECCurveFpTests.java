package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
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
    public void testShouldNotEqual1() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCParameters eccParameters2 = ECCTestHelper.getSmallCurve2Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();
        ECCCurve eccCurve2 = eccParameters2.getCurve();

        Assert.assertNotEquals(eccCurve1, eccCurve2);
    }

    @Test
    public void testShouldNotEqual2() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();

        Assert.assertFalse(eccCurve1.equals(eccParameters1));
    }

    @Test
    public void testShouldReturnInfinity() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();

        Assert.assertEquals(eccCurve1.getInfinity(), eccCurve1.decodePointHex("00"));
    }

    @Test
    public void testShouldReturnNull1() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();

        Assert.assertNull(eccCurve1.decodePointHex("02"));
    }

    @Test
    public void testShouldReturnNull2() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();

        Assert.assertNull(eccCurve1.decodePointHex("03"));
    }

    @Test
    public void testShouldReturnNull3() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();

        Assert.assertNull(eccCurve1.decodePointHex("05"));
    }

    @Test
    public void testShouldReturnNull4() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();

        Assert.assertNull(eccCurve1.decodePointHex("08"));
    }

    @Test
    public void testShouldReturnFp() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCCurve eccCurve1 = eccParameters1.getCurve();

        Assert.assertEquals(ECCFieldType.Fp, eccCurve1.getECCFieldType());
    }
}

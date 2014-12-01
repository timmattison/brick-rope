package com.timmattison.ecc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
import com.timmattison.crypto.ecc.interfaces.ECCCurve;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import com.timmattison.crypto.modules.ECCSECTestModule;
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
public class ECCurveFpTest {
    Injector injector = Guice.createInjector(new ECCSECTestModule());

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

        Assert.assertNull(eccCurve1.decodePointHex("05"));
    }

    @Test
    public void testShouldReturnNull2() {
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

    @Test
    public void testDecodePoint1() {
        // From https://bitcointalk.org/index.php?topic=237260.0
        ECCParameters eccParameters = ECCTestHelper.getSecp256r1(injector);
        ECCCurve eccCurve = eccParameters.getCurve();

        ECCPoint eccPoint = eccCurve.decodePointHex("03a3a8ee8a09fa012934eb0eee2150d4bcb6268f2b4430abf31f4a58c95b365b20");
        BigInteger x = eccPoint.getX().getX();
        BigInteger y = eccPoint.getY().getX();

        String xHex = x.toString(16);
        String yHex = y.toString(16);

        Assert.assertEquals("a3a8ee8a09fa012934eb0eee2150d4bcb6268f2b4430abf31f4a58c95b365b20", xHex);
        Assert.assertEquals("6e98c827edc5e80829c71e1fa9a83043379344316ba641d7c9c0850f687ed419", yHex);
    }
}

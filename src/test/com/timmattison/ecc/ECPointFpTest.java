package com.timmattison.ecc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
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
public class ECPointFpTest {
    Injector injector = Guice.createInjector(new ECCSECTestModule());

    @Test
    public void testShouldBeEqual1() {
        ECCPoint eccPoint1 = ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve1Parameters(injector), BigInteger.valueOf(5), BigInteger.valueOf(5));
        ECCPoint eccPoint2 = ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve1Parameters(injector), BigInteger.valueOf(5), BigInteger.valueOf(5));

        Assert.assertEquals(eccPoint1, eccPoint2);
    }

    @Test
    public void testShouldNotBeEqual1() {
        ECCPoint eccPoint1 = ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve1Parameters(injector), BigInteger.valueOf(5), BigInteger.valueOf(5));
        ECCPoint eccPoint2 = ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve2Parameters(injector), BigInteger.valueOf(5), BigInteger.valueOf(5));

        Assert.assertNotEquals(eccPoint1, eccPoint2);
    }

    @Test
    public void testShouldNotBeEqual2() {
        ECCPoint eccPoint1 = ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve1Parameters(injector), BigInteger.valueOf(5), BigInteger.valueOf(5));
        ECCPoint eccPoint2 = ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve1Parameters(injector), BigInteger.valueOf(5), BigInteger.valueOf(4));

        Assert.assertNotEquals(eccPoint1, eccPoint2);
    }

    @Test
    public void testShouldReturnFp() {
        ECCPoint eccPoint = ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve1Parameters(injector), BigInteger.valueOf(5), BigInteger.valueOf(5));

        Assert.assertEquals(ECCFieldType.Fp, eccPoint.getECCFieldType());
    }

    @Test
    public void testShouldNotEqual() {
        ECCParameters eccParameters = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCPoint eccPoint = ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve1Parameters(injector), BigInteger.valueOf(5), BigInteger.valueOf(5));

        Assert.assertFalse(eccPoint.equals(eccParameters));
    }
}

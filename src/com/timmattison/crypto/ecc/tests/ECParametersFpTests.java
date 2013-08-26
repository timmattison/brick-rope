package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.ECCCurve;
import com.timmattison.crypto.ecc.interfaces.ECCNamedCurveFactory;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
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
public class ECParametersFpTests {
    Injector injector = Guice.createInjector(new ECCTestModule());

    private ECCParameters getSmallCurve1Parameters() {
        return injector.getInstance(TestCurveParameters.class).getSmallCurve1Parameters();
    }

    private ECCParameters getSmallCurve2Parameters() {
        return injector.getInstance(TestCurveParameters.class).getSmallCurve2Parameters();
    }

    private ECCParameters getSecp160r1() {
        return injector.getInstance(ECCNamedCurveFactory.class).create().getSecp160r1();
    }

    private ECCParameters getSecp256k1() {
        return injector.getInstance(ECCNamedCurveFactory.class).create().getSecp256k1();
    }

    @Test
    public void testShouldBeEqual1() {
        ECCParameters eccParameters1 = getSmallCurve1Parameters();
        ECCParameters eccParameters2 = getSmallCurve1Parameters();

        Assert.assertEquals(eccParameters1, eccParameters2);
    }

    @Test
    public void testShouldBeEqual2() {
        ECCParameters eccParameters1 = getSecp160r1();
        ECCParameters eccParameters2 = getSecp160r1();

        Assert.assertEquals(eccParameters1, eccParameters2);
    }

    @Test
    public void testShouldNotBeEqual1() {
        ECCParameters eccParameters1 = getSmallCurve1Parameters();
        ECCParameters eccParameters2 = getSmallCurve2Parameters();

        Assert.assertNotEquals(eccParameters1, eccParameters2);
    }

    @Test
    public void testShouldNotBeEqual2() {
        ECCParameters eccParameters1 = getSecp160r1();
        ECCParameters eccParameters2 = getSecp256k1();

        Assert.assertNotEquals(eccParameters1, eccParameters2);
    }
}

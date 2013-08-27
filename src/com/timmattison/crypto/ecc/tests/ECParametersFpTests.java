package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/21/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECParametersFpTests {
    Injector injector = Guice.createInjector(new ECCTestModule());

    @Test
    public void testShouldBeEqual1() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCParameters eccParameters2 = ECCTestHelper.getSmallCurve1Parameters(injector);

        Assert.assertEquals(eccParameters1, eccParameters2);
    }

    @Test
    public void testShouldBeEqual2() {
        ECCParameters eccParameters1 = ECCTestHelper.getSecp160r1(injector);
        ECCParameters eccParameters2 = ECCTestHelper.getSecp160r1(injector);

        Assert.assertEquals(eccParameters1, eccParameters2);
    }

    @Test
    public void testShouldNotBeEqual1() {
        ECCParameters eccParameters1 = ECCTestHelper.getSmallCurve1Parameters(injector);
        ECCParameters eccParameters2 = ECCTestHelper.getSmallCurve2Parameters(injector);

        Assert.assertNotEquals(eccParameters1, eccParameters2);
    }

    @Test
    public void testShouldNotBeEqual2() {
        ECCParameters eccParameters1 = ECCTestHelper.getSecp160r1(injector);
        ECCParameters eccParameters2 = ECCTestHelper.getSecp256k1(injector);

        Assert.assertNotEquals(eccParameters1, eccParameters2);
    }
}

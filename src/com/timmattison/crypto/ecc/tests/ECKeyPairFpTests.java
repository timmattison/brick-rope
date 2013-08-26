package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/23/13
 * Time: 7:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECKeyPairFpTests {
    static Injector injector = Guice.createInjector(new ECCTestModule());
    ECCCurveFactory eccCurveFactory = injector.getInstance(ECCCurveFactory.class);
    ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);
    ECCParametersFactory eccParametersFactory = injector.getInstance(ECCParametersFactory.class);

    private static ECCParameters getSecp160r1() {
        return injector.getInstance(ECCNamedCurveFactory.class).create().getSecp160r1();
    }

    private static ECCKeyPair getKeyPair(ECCParameters eccParameters, BigInteger dU) {
        return injector.getInstance(ECCKeyPairFactory.class).create(eccParameters, dU);
    }

    private static final BigInteger gec2_2_1_2_dU = new BigInteger("971761939728640320549601132085879836204587084162");
    private static final BigInteger gec2_2_1_2_QuX = new BigInteger("466448783855397898016055842232266600516272889280");
    private static final BigInteger gec2_2_1_2_QuY = new BigInteger("1110706324081757720403272427311003102474457754220");

    /**
     * GEC2 2.1.2 - Key deployment for U
     */
    @Test
    public void testGec2_2_1_2() {
        // Validate the key pair
        ECCKeyPair gec2_2_1_2_eccKeyPair = getKeyPair(getSecp160r1(), gec2_2_1_2_dU);

        Assert.assertEquals(gec2_2_1_2_eccKeyPair.getD(), gec2_2_1_2_dU);
        Assert.assertEquals(gec2_2_1_2_eccKeyPair.getQ().getX().toBigInteger(), gec2_2_1_2_QuX);
        Assert.assertEquals(gec2_2_1_2_eccKeyPair.getQ().getY().toBigInteger(), gec2_2_1_2_QuY);
    }
}

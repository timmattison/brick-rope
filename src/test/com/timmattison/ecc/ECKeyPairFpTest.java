package com.timmattison.ecc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
import com.timmattison.crypto.ecc.interfaces.ECCKeyPair;
import com.timmattison.crypto.modules.ECCSECTestModule;
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
public class ECKeyPairFpTest {
    static Injector injector = Guice.createInjector(new ECCSECTestModule());

    private static final BigInteger gec2_2_1_2_dU = new BigInteger("971761939728640320549601132085879836204587084162");
    private static final BigInteger gec2_2_1_2_QuX = new BigInteger("466448783855397898016055842232266600516272889280");
    private static final BigInteger gec2_2_1_2_QuY = new BigInteger("1110706324081757720403272427311003102474457754220");

    /**
     * GEC2 2.1.2 - Key deployment for U
     */
    @Test
    public void testGec2_2_1_2() {
        // Validate the key pair
        ECCKeyPair gec2_2_1_2_eccKeyPair = ECCTestHelper.getKeyPair(injector, ECCTestHelper.getSecp160r1(injector), gec2_2_1_2_dU);

        Assert.assertEquals(gec2_2_1_2_eccKeyPair.getD(), gec2_2_1_2_dU);
        Assert.assertEquals(gec2_2_1_2_eccKeyPair.getQ().getX().toBigInteger(), gec2_2_1_2_QuX);
        Assert.assertEquals(gec2_2_1_2_eccKeyPair.getQ().getY().toBigInteger(), gec2_2_1_2_QuY);
    }

    @Test
    public void testKeyIsZeroShouldThrowException() {
        try {
            ECCKeyPair gec2_2_1_2_eccKeyPair = ECCTestHelper.getKeyPair(injector, ECCTestHelper.getSecp160r1(injector), BigInteger.ZERO);
            Assert.fail("Key was zero but the key pair was still created");
        } catch (Exception ex) {
            // Success
        }
    }

    @Test
    public void testKeyIsNShouldThrowException() {
        try {
            ECCKeyPair gec2_2_1_2_eccKeyPair = ECCTestHelper.getKeyPair(injector, ECCTestHelper.getSecp160r1(injector), ECCTestHelper.getSecp160r1(injector).getN());
            Assert.fail("Key was N but the key pair was still created");
        } catch (Exception ex) {
            // Success
        }
    }

    @Test
    public void testKeyIsOneShouldNotThrowException() {
        ECCKeyPair gec2_2_1_2_eccKeyPair = ECCTestHelper.getKeyPair(injector, ECCTestHelper.getSecp160r1(injector), BigInteger.ONE);
    }

    @Test
    public void testKeyIsNMinus1ShouldNotThrowException() {
        ECCKeyPair gec2_2_1_2_eccKeyPair = ECCTestHelper.getKeyPair(injector, ECCTestHelper.getSecp160r1(injector), ECCTestHelper.getSecp160r1(injector).getN().subtract(BigInteger.ONE));
    }

    @Test
    public void testShouldReturnFp() {
        ECCKeyPair gec2_2_1_2_eccKeyPair = ECCTestHelper.getKeyPair(injector, ECCTestHelper.getSecp160r1(injector), ECCTestHelper.getSecp160r1(injector).getN().subtract(BigInteger.ONE));

        Assert.assertEquals(ECCFieldType.Fp, gec2_2_1_2_eccKeyPair.getECCFieldType());
    }
}

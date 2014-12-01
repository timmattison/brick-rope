package com.timmattison.ecc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import com.timmattison.crypto.modules.ECCSECTestModule;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/10/13
 * Time: 7:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SECp256k1Test {
    Injector injector = Guice.createInjector(new ECCSECTestModule());

    /*
     * From http://crypto.stackexchange.com/questions/784/secp256k1-test-examples
     */
    // Set 1
    BigInteger m1 = new BigInteger("AA5E28D6A97A2479A65527F7290311A3624D4CC0FA1578598EE3C2613BF99522", 16);
    BigInteger X1 = new BigInteger("34F9460F0E4F08393D192B3C5133A6BA099AA0AD9FD54EBCCFACDFA239FF49C6", 16);
    BigInteger Y1 = new BigInteger("0B71EA9BD730FD8923F6D25A7A91E7DD7728A960686CB5A901BB419E0F2CA232", 16);
    // Set 2
    BigInteger m2 = new BigInteger("7E2B897B8CEBC6361663AD410835639826D590F393D90A9538881735256DFAE3", 16);
    BigInteger X2 = new BigInteger("D74BF844B0862475103D96A611CF2D898447E288D34B360BC885CB8CE7C00575", 16);
    BigInteger Y2 = new BigInteger("131C670D414C4546B88AC3FF664611B1C38CEB1C21D76369D7A7A0969D61D97D", 16);
    // Set 3
    BigInteger m3 = new BigInteger("6461E6DF0FE7DFD05329F41BF771B86578143D4DD1F7866FB4CA7E97C5FA945D", 16);
    BigInteger X3 = new BigInteger("E8AECC370AEDD953483719A116711963CE201AC3EB21D3F3257BB48668C6A72F", 16);
    BigInteger Y3 = new BigInteger("C25CAF2F0EBA1DDB2F0F3F47866299EF907867B7D27E95B3873BF98397B24EE1", 16);
    // Set 4
    BigInteger m4 = new BigInteger("376A3A2CDCD12581EFFF13EE4AD44C4044B8A0524C42422A7E1E181E4DEECCEC", 16);
    BigInteger X4 = new BigInteger("14890E61FCD4B0BD92E5B36C81372CA6FED471EF3AA60A3E415EE4FE987DABA1", 16);
    BigInteger Y4 = new BigInteger("297B858D9F752AB42D3BCA67EE0EB6DCD1C2B7B0DBE23397E66ADC272263F982", 16);
    // Set 5
    BigInteger m5 = new BigInteger("1B22644A7BE026548810C378D0B2994EEFA6D2B9881803CB02CEFF865287D1B9", 16);
    BigInteger X5 = new BigInteger("F73C65EAD01C5126F28F442D087689BFA08E12763E0CEC1D35B01751FD735ED3", 16);
    BigInteger Y5 = new BigInteger("F449A8376906482A84ED01479BD18882B919C140D638307F0C0934BA12590BDE", 16);

    @Test
    public void testInfinity() throws Exception {
        ECCParameters secp256k1 = ECCTestHelper.getSecp256k1(injector);
        ECCPoint result = secp256k1.getG().multiply(secp256k1.getN());

        Assert.assertTrue(result.isInfinity());
    }

    @Test
    public void testVectorSet1() {
        ECCPoint result = ECCTestHelper.getSecp256k1(injector).getG().multiply(m1);

        Assert.assertTrue(result.getX().toBigInteger().equals(X1));
        Assert.assertTrue(result.getY().toBigInteger().equals(Y1));
    }

    @Test
    public void testVectorSet2() {
        ECCPoint result = ECCTestHelper.getSecp256k1(injector).getG().multiply(m2);

        Assert.assertTrue(result.getX().toBigInteger().equals(X2));
        Assert.assertTrue(result.getY().toBigInteger().equals(Y2));
    }

    @Test
    public void testVectorSet3() {
        ECCPoint result = ECCTestHelper.getSecp256k1(injector).getG().multiply(m3);

        Assert.assertTrue(result.getX().toBigInteger().equals(X3));
        Assert.assertTrue(result.getY().toBigInteger().equals(Y3));
    }

    @Test
    public void testVectorSet4() {
        ECCPoint result = ECCTestHelper.getSecp256k1(injector).getG().multiply(m4);

        Assert.assertTrue(result.getX().toBigInteger().equals(X4));
        Assert.assertTrue(result.getY().toBigInteger().equals(Y4));
    }

    @Test
    public void testVectorSet5() {
        ECCPoint result = ECCTestHelper.getSecp256k1(injector).getG().multiply(m5);

        Assert.assertTrue(result.getX().toBigInteger().equals(X5));
        Assert.assertTrue(result.getY().toBigInteger().equals(Y5));
    }

    @Test
    public void testVectorSet5MustFail() {
        ECCPoint result = ECCTestHelper.getSecp256k1(injector).getG().multiply(m5);

        // Check it against the wrong points
        Assert.assertFalse(result.getX().toBigInteger().equals(X1));
        Assert.assertFalse(result.getY().toBigInteger().equals(Y1));
    }

    /**
     * Recommended by: http://crypto.stackexchange.com/questions/784/secp256k1-test-examples
     */
    @Test
    public void testRandomPoints() {
        Random random = new Random(1);

        ECCParameters parameters = ECCTestHelper.getSecp256k1(injector);
        ECCPoint g = parameters.getG();

        BigInteger n = parameters.getN();

        for (int loop = 0; loop < 64; loop++) {
            BigInteger a = new BigInteger(n.bitLength(), random);
            BigInteger b = new BigInteger(n.bitLength(), random);
            BigInteger c = a.add(b);

            ECCPoint p = g.multiply(a);
            ECCPoint q = g.multiply(b);
            ECCPoint r = g.multiply(c);

            ECCPoint pPlusQ = p.add(q);
            ECCPoint qPlusP = q.add(p);

            BigInteger pPlusQx = pPlusQ.getX().toBigInteger();
            BigInteger pPlusQy = pPlusQ.getY().toBigInteger();

            BigInteger rX = r.getX().toBigInteger();
            BigInteger rY = r.getY().toBigInteger();

            Assert.assertTrue(pPlusQ.equals(r));
            Assert.assertTrue(qPlusP.equals(r));
        }
    }
}


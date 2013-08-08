package com.timmattison.cryptocurrency.bitcoin.tests;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.cryptocurrency.bitcoin.DoubleSha256Hash;
import com.timmattison.cryptocurrency.bitcoin.factories.DoubleSha256Factory;
import com.timmattison.cryptocurrency.standard.HashComparator;
import com.timmattison.cryptocurrency.standard.StandardMerkleRootCalculator;
import com.timmattison.cryptocurrency.standard.tests.TestHelper;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/8/13
 * Time: 1:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class DoubleSha256HashTests {

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Test
    public void helloStringTest() {
        final String helloString = "hello";
        final byte[] expected = TestHelper.fromHexString("9595c9df90075148eb06860365df33584b75bff782a510c6cd4883a419833d50");
        final byte[] helloBytes = helloString.getBytes();

        final DoubleSha256Hash hasher = new DoubleSha256Hash(helloBytes);

        final byte[] result = hasher.getOutput();
        final String resultString = ByteArrayHelper.toHex(result);

        Assert.assertTrue(Arrays.equals(result, expected));
    }

    @Test
    public void helloHexStringTest() {
        final byte[] expected = TestHelper.fromHexString("9595c9df90075148eb06860365df33584b75bff782a510c6cd4883a419833d50");
        final byte[] helloBytes = TestHelper.fromHexString("68656c6c6f");

        final DoubleSha256Hash hasher = new DoubleSha256Hash(helloBytes);

        final byte[] result = hasher.getOutput();
        final String resultString = ByteArrayHelper.toHex(result);

        Assert.assertTrue(Arrays.equals(result, expected));
    }

    @Test
    public void block72785Merkle1() throws IOException {
        final byte[] leaf1 = TestHelper.fromHexString("2d7f4d1c25893dcaf538fdd1f34104687211ca7d8a1ba43c16b618d5fbc620c3");
        final byte[] leaf2 = TestHelper.fromHexString("3407a84dce0fe04fdab91608d1974941af3683ea6e4d904a30469485c50d336a");
        final byte[] expected = TestHelper.fromHexString("8ebc6ac1c5c656c19632f8b7efd130303a9710ed1c0ea12935255d6fefc5d3b4");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(leaf1);
        baos.write(leaf2);

        final byte[] leaves = baos.toByteArray();

        final DoubleSha256Hash hasher = new DoubleSha256Hash(leaves);

        final byte[] result = hasher.getOutput();
        final String resultString = ByteArrayHelper.toHex(result);

        Assert.assertTrue(Arrays.equals(result, expected));
    }
}

package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.TestHelper;
import com.timmattison.cryptocurrency.standard.hashing.sha.DoubleSha256Hash;
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
    final byte[] helloStringExpectedHash = TestHelper.fromLittleEndianHexString("9595c9df90075148eb06860365df33584b75bff782a510c6cd4883a419833d50");

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Test
    public void helloStringTest() {
        final String helloString = "hello";
        final byte[] helloBytes = helloString.getBytes();

        final DoubleSha256Hash hasher = new DoubleSha256Hash(helloBytes);

        final byte[] result = hasher.getOutput();
        final String resultString = ByteArrayHelper.toHex(result);

        Assert.assertTrue(Arrays.equals(result, helloStringExpectedHash));
    }

    @Test
    public void helloHexStringTest() {
        final byte[] helloBytes = TestHelper.fromLittleEndianHexString("68656c6c6f");

        final DoubleSha256Hash hasher = new DoubleSha256Hash(helloBytes);

        final byte[] result = hasher.getOutput();
        final String resultString = ByteArrayHelper.toHex(result);

        Assert.assertTrue(Arrays.equals(result, helloStringExpectedHash));
    }

    @Test
    public void opensslVerified1() throws IOException {
        final byte[] leaf1 = TestHelper.fromLittleEndianHexString("2d7f4d1c25893dcaf538fdd1f34104687211ca7d8a1ba43c16b618d5fbc620c3");
        final byte[] leaf2 = TestHelper.fromLittleEndianHexString("3407a84dce0fe04fdab91608d1974941af3683ea6e4d904a30469485c50d336a");
        final byte[] expected = TestHelper.fromLittleEndianHexString("f840ae66eeb6336a576b7e0d543d47063c4d49ae9d04c89b8bba5c257f134652");

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

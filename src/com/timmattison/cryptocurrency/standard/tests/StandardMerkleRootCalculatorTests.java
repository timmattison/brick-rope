package com.timmattison.cryptocurrency.standard.tests;

import com.timmattison.cryptocurrency.bitcoin.factories.DoubleSha256Factory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.standard.HashComparator;
import com.timmattison.cryptocurrency.standard.StandardMerkleRootCalculator;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/8/13
 * Time: 10:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class StandardMerkleRootCalculatorTests {
    private final StandardMerkleRootCalculator calculator = new StandardMerkleRootCalculator(new DoubleSha256Factory(), new HashComparator());
    private final byte[] merkleRootBlock0 = fromHexString("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b");
    private final byte[] merkleRootBlock1 = fromHexString("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098");
    private final byte[] merkleRootBlock170 = fromHexString("7dac2c5666815c17a3b36427de37bb9d2e2c5ccec3f8633eb91a4205cb4c10ff");

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Test
    public void bitcoinBlock0Test() {
        final byte[] hashOfBlock0Transaction0 = fromHexString("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b");
        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(hashOfBlock0Transaction0);

        byte[] result = calculator.calculateMerkleRoot(input);

        Assert.assertTrue(Arrays.equals(result, merkleRootBlock0));
    }

    @Test
    public void bitcoinBlock1Test() {
        final byte[] hashOfBlock1Transaction0 = fromHexString("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098");
        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(hashOfBlock1Transaction0);

        byte[] result = calculator.calculateMerkleRoot(input);

        Assert.assertTrue(Arrays.equals(result, merkleRootBlock1));
    }

    @Test
    public void bitcoinBlock170Test() {
        final byte[] hashOfBlock170Transaction0 = fromHexString("b1fea52486ce0c62bb442b530a3f0132b826c74e473d1f2c220bfa78111c5082");
        final byte[] hashOfBlock170Transaction1 = fromHexString("f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16");
        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(hashOfBlock170Transaction0);
        input.add(hashOfBlock170Transaction1);

        byte[] result = calculator.calculateMerkleRoot(input);

        Assert.assertTrue(Arrays.equals(result, merkleRootBlock170));
    }

    private byte[] fromHexString(String string) {
        if ((string.length() % 2) != 0)
            throw new IllegalArgumentException("Input string must contain an even number of characters");

        final byte result[] = new byte[string.length() / 2];
        final char enc[] = string.toCharArray();
        for (int i = 0; i < enc.length; i += 2) {
            StringBuilder curr = new StringBuilder(2);
            curr.append(enc[i]).append(enc[i + 1]);
            result[i / 2] = (byte) Integer.parseInt(curr.toString(), 16);
        }
        return result;
    }
}

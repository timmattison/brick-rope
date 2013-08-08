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
    private final boolean changeEndianness = false;

    private final StandardMerkleRootCalculator calculator = new StandardMerkleRootCalculator(new DoubleSha256Factory(), new HashComparator());
    private final byte[] merkleRootBlock0 = TestHelper.fromHexString("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b", changeEndianness);
    private final byte[] merkleRootBlock1 = TestHelper.fromHexString("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098", changeEndianness);
    private final byte[] merkleRootBlock170 = TestHelper.fromHexString("7dac2c5666815c17a3b36427de37bb9d2e2c5ccec3f8633eb91a4205cb4c10ff", changeEndianness);

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Test
    public void bitcoinBlock0Test() {
        final byte[] hashOfBlock0Transaction0 = TestHelper.fromHexString("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b", changeEndianness);
        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(hashOfBlock0Transaction0);

        byte[] result = calculator.calculateMerkleRoot(input);

        Assert.assertTrue(Arrays.equals(result, merkleRootBlock0));
    }

    @Test
    public void bitcoinBlock1Test() {
        final byte[] hashOfBlock1Transaction0 = TestHelper.fromHexString("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098", changeEndianness);
        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(hashOfBlock1Transaction0);

        byte[] result = calculator.calculateMerkleRoot(input);

        Assert.assertTrue(Arrays.equals(result, merkleRootBlock1));
    }

    @Test
    public void bitcoinBlock170Test() {
        final byte[] hashOfBlock170Transaction0 = TestHelper.fromHexString("b1fea52486ce0c62bb442b530a3f0132b826c74e473d1f2c220bfa78111c5082", changeEndianness);
        final byte[] hashOfBlock170Transaction1 = TestHelper.fromHexString("f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16", changeEndianness);
        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(hashOfBlock170Transaction0);
        input.add(hashOfBlock170Transaction1);

        byte[] result = calculator.calculateMerkleRoot(input);

        Assert.assertTrue(Arrays.equals(result, merkleRootBlock170));
    }

   @Test
    public void syntheticTest1() {
        final byte[] tx0 = TestHelper.fromHexString("3a459eab5f0cf8394a21e04d2ed3b2beeaa59795912e20b9c680e9db74dfb18c", changeEndianness);
        final byte[] tx1 = TestHelper.fromHexString("be38f46f0eccba72416aed715851fd07b881ffb7928b7622847314588e06a6b7", changeEndianness);
        final byte[] tx2 = TestHelper.fromHexString("d173f2a12b6ff63a77d9fe7bbb590bdb02b826d07739f90ebb016dc9297332be", changeEndianness);
        final byte[] tx3 = TestHelper.fromHexString("59d1e83e5268bbb491234ff23cbbf2a7c0aa87df553484afee9e82385fc7052f", changeEndianness);
        final byte[] tx4 = TestHelper.fromHexString("f1ce77a69d06efb79e3b08a0ff441fa3b1deaf71b358df55244d56dd797ac60c", changeEndianness);
        final byte[] tx5 = TestHelper.fromHexString("84053cba91fe659fd3afa1bf2fd0e3746b99215b50cd74e44bda507d8edf52e0", changeEndianness);

        final byte[] expectedRoot = TestHelper.fromHexString("9cdf7722eb64015731ba9794e32bdefd9cf69b42456d31f5e59aedb68c57ed52", changeEndianness);

        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(tx0);
        input.add(tx1);
        input.add(tx2);
        input.add(tx3);
        input.add(tx4);
        input.add(tx5);

        byte[] result = calculator.calculateMerkleRoot(input);

        Assert.assertTrue(Arrays.equals(result, expectedRoot));
    }

    @Test
    public void syntheticTest2() {
        final byte[] tx0 = TestHelper.fromHexString("59545fd8dfdd821ca7accecab0655d77437f5bba5aaa5ea8c042a26bc9ae514b", changeEndianness);
        final byte[] tx1 = TestHelper.fromHexString("15eca0aa3e2cc2b9b4fbe0629f1dda87f329500fcdcd6ef546d163211266b3b3", changeEndianness);

        final byte[] expectedRoot = TestHelper.fromHexString("9cdf7722eb64015731ba9794e32bdefd9cf69b42456d31f5e59aedb68c57ed52", changeEndianness);

        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(tx0);
        input.add(tx1);

        byte[] result = calculator.calculateMerkleRoot(input);
        String resultHex = ByteArrayHelper.toHex(result);

        Assert.assertTrue(Arrays.equals(result, expectedRoot));
    }
}

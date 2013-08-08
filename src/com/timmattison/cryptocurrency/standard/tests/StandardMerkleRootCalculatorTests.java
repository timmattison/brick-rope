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
    private final byte[] merkleRootBlock0 = TestHelper.fromLittleEndianHexString("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b");
    private final byte[] merkleRootBlock1 = TestHelper.fromLittleEndianHexString("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098");
    private final byte[] merkleRootBlock170 = TestHelper.fromLittleEndianHexString("7dac2c5666815c17a3b36427de37bb9d2e2c5ccec3f8633eb91a4205cb4c10ff");

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Test
    public void bitcoinBlock0Test() {
        final byte[] hashOfBlock0Transaction0 = TestHelper.fromLittleEndianHexString("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b");
        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(hashOfBlock0Transaction0);

        byte[] result = calculator.calculateMerkleRoot(input);

        Assert.assertTrue(Arrays.equals(result, merkleRootBlock0));
    }

    @Test
    public void bitcoinBlock1Test() {
        final byte[] hashOfBlock1Transaction0 = TestHelper.fromLittleEndianHexString("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098");
        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(hashOfBlock1Transaction0);

        byte[] result = calculator.calculateMerkleRoot(input);

        Assert.assertTrue(Arrays.equals(result, merkleRootBlock1));
    }

    @Test
    public void bitcoinBlock170Test() {
        final byte[] hashOfBlock170Transaction0 = TestHelper.fromLittleEndianHexString("b1fea52486ce0c62bb442b530a3f0132b826c74e473d1f2c220bfa78111c5082");
        final byte[] hashOfBlock170Transaction1 = TestHelper.fromLittleEndianHexString("f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16");
        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(hashOfBlock170Transaction0);
        input.add(hashOfBlock170Transaction1);

        byte[] result = calculator.calculateMerkleRoot(input);
        String resultString = ByteArrayHelper.toHex(result);

        Assert.assertTrue(Arrays.equals(result, merkleRootBlock170));
    }

    @Test
    public void block72785() {
        // https://en.bitcoin.it/wiki/Dump_format#CBlock
        // http://blockexplorer.com/block/00000000009ffdadbb2a8bcf8e8b1d68e1696802856c6a1d61561b1f630e79e7
        // http://blockexplorer.com/rawblock/00000000009ffdadbb2a8bcf8e8b1d68e1696802856c6a1d61561b1f630e79e7

        final byte[] tx0 = TestHelper.fromLittleEndianHexString("2d7f4d1c25893dcaf538fdd1f34104687211ca7d8a1ba43c16b618d5fbc620c3");
        final byte[] tx1 = TestHelper.fromLittleEndianHexString("3407a84dce0fe04fdab91608d1974941af3683ea6e4d904a30469485c50d336a");
        final byte[] tx2 = TestHelper.fromLittleEndianHexString("5edf5acf8f517d965219a5495321e0bedd761daf45bcdc59a33b07b520968b8c");
        final byte[] tx3 = TestHelper.fromLittleEndianHexString("65c35615b476c86f28a4d3a8985ea161cc2e35e6574eacbd68942782ce29804c");
        final byte[] tx4 = TestHelper.fromLittleEndianHexString("89aa32f6e1b047e740401ce4fd43a865631de5a959fde7451936c28c52249b56");
        final byte[] tx5 = TestHelper.fromLittleEndianHexString("e3e69c802b7e36d220151e4ccdeace1d58ca2af97c5fd970314bbecd9767a514");

        final String tx0String = ByteArrayHelper.toHex(tx0);

        final byte[] expectedRoot = TestHelper.fromLittleEndianHexString("e81287dc0c00422aaf0db3e4586c48b01acd82b3108da6956cbd6baf19cfaf9a");

        final List<byte[]> input = new ArrayList<byte[]>();
        input.add(tx0);
        input.add(tx1);
        input.add(tx2);
        input.add(tx3);
        input.add(tx4);
        input.add(tx5);

        byte[] result = calculator.calculateMerkleRoot(input);
        String resultHex = ByteArrayHelper.toHex(result);

        Assert.assertTrue(Arrays.equals(result, expectedRoot));
    }
}

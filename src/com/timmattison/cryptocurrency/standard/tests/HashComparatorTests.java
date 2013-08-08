package com.timmattison.cryptocurrency.standard.tests;

import com.timmattison.cryptocurrency.standard.HashComparator;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/8/13
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class HashComparatorTests {
    private final boolean changeEndianness = false;
    private final byte[] merkleRootBlock0 = TestHelper.fromHexString("4a5e1e4baab89f3a32518a88c31bc87f618f76673e2cc77ab2127b7afdeda33b", changeEndianness);
    private final byte[] merkleRootBlock1 = TestHelper.fromHexString("0e3e2357e806b6cdb1f70b54c3a3a17b6714ee1f0e68bebb44a74b1efd512098", changeEndianness);
    private final HashComparator hashComparator = new HashComparator();

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Test
    public void syntheticTest0() {
        List<byte[]> list = new ArrayList<byte[]>();
        list.add(merkleRootBlock0);
        list.add(merkleRootBlock1);

        Collections.sort(list, hashComparator);

        Assert.assertTrue(Arrays.equals(list.get(0), merkleRootBlock1));
        Assert.assertTrue(Arrays.equals(list.get(1), merkleRootBlock0));
    }

    @Test
    public void syntheticTest1() {
        List<byte[]> list = new ArrayList<byte[]>();
        list.add(merkleRootBlock1);
        list.add(merkleRootBlock0);

        Collections.sort(list, hashComparator);

        Assert.assertTrue(Arrays.equals(list.get(0), merkleRootBlock1));
        Assert.assertTrue(Arrays.equals(list.get(1), merkleRootBlock0));
    }
}

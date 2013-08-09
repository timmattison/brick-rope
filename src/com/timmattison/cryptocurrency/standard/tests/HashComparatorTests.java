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
    private final HashComparator hashComparator = new HashComparator();

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {

    }

    @Test
    public void block170() {
        final byte[] firstHash = TestHelper.fromBigEndianHexString("b1fea52486ce0c62bb442b530a3f0132b826c74e473d1f2c220bfa78111c5082");
        final byte[] secondHash = TestHelper.fromBigEndianHexString("f4184fc596403b9d638783cf57adfe4c75c605f6356fbc91338530e9831e9e16");

        List<byte[]> list = new ArrayList<byte[]>();
        list.add(firstHash);
        list.add(secondHash);

        Collections.sort(list, hashComparator);

        Assert.assertTrue(Arrays.equals(list.get(1), secondHash));
        Assert.assertTrue(Arrays.equals(list.get(0), firstHash));
    }

    final byte[] block72785Level3a = TestHelper.fromBigEndianHexString("2d7f4d1c25893dcaf538fdd1f34104687211ca7d8a1ba43c16b618d5fbc620c3");
    final byte[] block72785Level3b = TestHelper.fromBigEndianHexString("3407a84dce0fe04fdab91608d1974941af3683ea6e4d904a30469485c50d336a");
    final byte[] block72785Level3c = TestHelper.fromBigEndianHexString("5edf5acf8f517d965219a5495321e0bedd761daf45bcdc59a33b07b520968b8c");
    final byte[] block72785Level3d = TestHelper.fromBigEndianHexString("65c35615b476c86f28a4d3a8985ea161cc2e35e6574eacbd68942782ce29804c");
    final byte[] block72785Level3e = TestHelper.fromBigEndianHexString("89aa32f6e1b047e740401ce4fd43a865631de5a959fde7451936c28c52249b56");
    final byte[] block72785Level3f = TestHelper.fromBigEndianHexString("e3e69c802b7e36d220151e4ccdeace1d58ca2af97c5fd970314bbecd9767a514");

    private List<byte[]> getBlock72785List() {
        List<byte[]> block72785List = new ArrayList<byte[]>();
        block72785List.add(block72785Level3a);
        block72785List.add(block72785Level3b);
        block72785List.add(block72785Level3c);
        block72785List.add(block72785Level3d);
        block72785List.add(block72785Level3e);
        block72785List.add(block72785Level3f);
        return block72785List;
    }

    private void assertBlock72785Order(List<byte[]> list) {
        Assert.assertTrue(Arrays.equals(list.get(0), block72785Level3a));
        Assert.assertTrue(Arrays.equals(list.get(1), block72785Level3b));
        Assert.assertTrue(Arrays.equals(list.get(2), block72785Level3c));
        Assert.assertTrue(Arrays.equals(list.get(3), block72785Level3d));
        Assert.assertTrue(Arrays.equals(list.get(4), block72785Level3e));
        Assert.assertTrue(Arrays.equals(list.get(5), block72785Level3f));
    }

    @Test
    public void block72785AlreadySorted() {
        List<byte[]> list = getBlock72785List();

        Collections.sort(list, hashComparator);

        assertBlock72785Order(list);
    }

    @Test
    public void block72785CheckRandomized() {
        List<byte[]> list = getBlock72785List();

        for(int loop = 0; loop < 16; loop++) {
            Collections.shuffle(list);

            Collections.sort(list, hashComparator);

            assertBlock72785Order(list);
        }
    }
}

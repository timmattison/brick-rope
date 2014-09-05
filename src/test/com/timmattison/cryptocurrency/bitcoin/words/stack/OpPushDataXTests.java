package com.timmattison.cryptocurrency.bitcoin.words.stack;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/8/13
 * Time: 1:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpPushDataXTests {
    private OpPushData1 opPushData1;
    private OpPushData2 opPushData2;
    private OpPushData4 opPushData4;

    @Before
    public void setup() {
        opPushData1 = new OpPushData1();
        opPushData2 = new OpPushData2();
        opPushData4 = new OpPushData4();
    }

    @Test
    public void testOpPushData1() {
        for (int loop = 1; loop < 255; loop++) {
            StackHelpers.testPush(opPushData1, loop);
        }
    }

    @Test
    public void testOpPushData2() {
        Random random = new Random(0);

        for (int loop = 1; loop < 255; loop++) {
            // Test up to 64k
            int bytesToPush = random.nextInt() & 0x0FFFF;

            if (bytesToPush == 0) {
                bytesToPush = 1;
            }

            StackHelpers.testPush(opPushData2, bytesToPush);
        }
    }

    @Test
    public void testOpPushData4() {
        Random random = new Random(0);

        for (int loop = 1; loop < 255; loop++) {
            // Only test up to 128k to avoid blowing the whole system up
            int bytesToPush = random.nextInt() & 0x01FFFF;

            if (bytesToPush == 0) {
                bytesToPush = 1;
            }

            StackHelpers.testPush(opPushData4, bytesToPush);
        }
    }
}

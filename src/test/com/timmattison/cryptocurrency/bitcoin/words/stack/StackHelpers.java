package com.timmattison.cryptocurrency.bitcoin.words.stack;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import org.junit.Assert;

/**
 * Created by timmattison on 9/5/14.
 */
public class StackHelpers {
    public static void createDataArray(int headerSize, int length, byte[] output) {
        for (int loop = 0; loop < length; loop++) {
            output[loop + headerSize] = (byte) loop;
        }
    }

    public static boolean dataIsCorrect(int length, byte[] input) {
        for (int loop = 0; loop < length; loop++) {
            if (input[loop] != loop) {
                return false;
            }
        }

        return true;
    }

    private static byte[] createDataToPush(AbstractOpPushData abstractOpPushData, int length) {
        byte[] output = new byte[length + abstractOpPushData.getValueLength()];

        if (abstractOpPushData.getValueLength() == 1) {
            output[0] = (byte) length;
        } else if (abstractOpPushData.getValueLength() == 2) {
            output[0] = (byte) (length & 0xFF);
            output[1] = (byte) ((length & 0xFF00) >> 8);
        } else if (abstractOpPushData.getValueLength() == 4) {
            output[0] = (byte) (length & 0xFF);
            output[1] = (byte) ((length & 0xFF00) >> 8);
            output[2] = (byte) ((length & 0xFF0000) >> 16);
            output[3] = (byte) ((length & 0xFF000000) >> 24);
        } else {
            throw new UnsupportedOperationException("Unexpected value length");
        }

        StackHelpers.createDataArray(abstractOpPushData.getValueLength(), length, output);

        return output;
    }

    public static void testPush(AbstractOpPushData abstractOpPushData, int loop) {
        byte[] remaining = abstractOpPushData.build(createDataToPush(abstractOpPushData, loop));

        Assert.assertTrue(remaining.length == 0);

        StateMachine stateMachine = new FakeStateMachine();

        abstractOpPushData.execute(stateMachine);

        byte[] topOfStack = (byte[]) stateMachine.pop();

        Assert.assertTrue(topOfStack.length == loop);
        Assert.assertTrue(StackHelpers.dataIsCorrect(abstractOpPushData.getValueLength(), topOfStack));
    }
}

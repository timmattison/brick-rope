package com.timmattison.cryptocurrency.bitcoin.words.bitwiselogic;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpEqual extends BitwiseOp {
    private static final String word = "OP_EQUAL";
    private static final Byte opcode = (byte) 0x87;

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }

    @Override
    public void execute(StateMachine stateMachine) {
        // Pop the top two items off of the stack so we can compare them
        Object item1 = stateMachine.pop();
        Object item2 = stateMachine.pop();

        Boolean result = null;

        // Is item1 an array of java.lang.Byte?
        if (item1 instanceof Byte[]) {
            // Yes, convert it to a regular byte array
            item1 = ByteArrayHelper.convertByteArray((Byte[]) item1);
        }

        // Is item2 an array of java.lang.Byte?
        if (item2 instanceof Byte[]) {
            // Yes, convert it to a regular byte array
            item2 = ByteArrayHelper.convertByteArray((Byte[]) item2);
        }

        // Are both items byte arrays?
        if ((item1 instanceof byte[]) && (item2 instanceof byte[])) {
            // Yes, compare them with Array.equals
            result = Arrays.equals((byte[]) item1, (byte[]) item2);
        } else {
            // No, just use the equals operator to see if they are equal
            // XXX - This will probably not work in most cases
            result = item1.equals(item2);
        }

        // Did we get a result?
        if (result == null) {
            // No, throw an exception
            throw new UnsupportedOperationException("No comparison found for the top two items on the stack");
        }

        // Push the result onto the stack
        stateMachine.push(result);
    }
}

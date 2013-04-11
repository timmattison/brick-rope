package com.timmattison.bitcoin.test.script;

import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class StateMachine {
    public Stack<Object> stack = new Stack<Object>();

    public byte[] popByteArray() {
        Object item = stack.pop();

        if (item instanceof Byte[]) {
            return convertByteArray((Byte[]) item);
        } else if (item instanceof byte[]) {
            return (byte[]) item;
        } else {
            throw new UnsupportedOperationException("Item on the top of the stack cannot be converted to a byte array");
        }
    }

    public static byte[] convertByteArray(Byte[] input) {
        Byte[] array = (Byte[]) input;
        byte[] bytes = new byte[array.length];
        for (int loop = 0; loop < array.length; loop++) {
            bytes[loop] = array[loop];
        }

        return bytes;
    }
}

package com.timmattison.cryptocurrency.bitcoin.words.stack;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpPushData4 extends AbstractOpPushData {
    private static final String word = "OP_PUSHDATA4";
    private static final Byte opcode = (byte) 0x4e;

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }

    @Override
    protected int getValueLength() {
        return 4;
    }
}

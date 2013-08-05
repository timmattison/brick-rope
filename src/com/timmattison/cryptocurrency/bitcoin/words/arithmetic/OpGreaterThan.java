package com.timmattison.cryptocurrency.bitcoin.words.arithmetic;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpGreaterThan extends ArithmeticOp {
    private static final String word = "OP_GREATERTHAN";
    private static final Byte opcode = (byte) 0xa0;

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }
}

package com.timmattison.cryptocurrency.bitcoin.words.constants;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpFalse extends ConstantOp {
    private static final String word = "OP_FALSE";
    private static final Byte opcode = (byte) 0x00;

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }
}

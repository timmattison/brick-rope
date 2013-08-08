package com.timmattison.cryptocurrency.bitcoin.words.stack;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpRot extends StackOp {
    private static final String word = "OP_ROT";
    private static final Byte opcode = (byte) 0x7b;

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

    @Override
    public byte[] build(byte[] data) {
        //To change body of implemented methods use File | Settings | File Templates.
        throw new UnsupportedOperationException();
    }
}

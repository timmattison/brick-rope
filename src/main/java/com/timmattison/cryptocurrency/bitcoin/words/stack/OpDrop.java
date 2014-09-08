package com.timmattison.cryptocurrency.bitcoin.words.stack;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpDrop extends StackOp {
    private static final String word = "OP_DROP";
    private static final Byte opcode = (byte) 0x75;

    @Override
    public void execute(StateMachine stateMachine) {
        stateMachine.pop();
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

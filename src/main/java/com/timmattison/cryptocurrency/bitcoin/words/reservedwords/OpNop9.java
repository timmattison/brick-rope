package com.timmattison.cryptocurrency.bitcoin.words.reservedwords;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpNop9 extends ReservedWordOp {
    private static final String word = "OP_NOP9";
    private static final Byte opcode = (byte) 0xb8;

    @Override
    public void execute(StateMachine stateMachine) {
        // Do nothing
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

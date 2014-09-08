package com.timmattison.cryptocurrency.bitcoin.words.reservedwords;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpNop8 extends ReservedWordOp {
    private static final String word = "OP_NOP8";
    private static final Byte opcode = (byte) 0xb7;

    @Override
    public void execute(com.timmattison.cryptocurrency.bitcoin.StateMachine stateMachine) {
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

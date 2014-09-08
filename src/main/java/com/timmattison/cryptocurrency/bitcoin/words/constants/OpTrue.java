package com.timmattison.cryptocurrency.bitcoin.words.constants;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpTrue extends ConstantOp {
    private static final String word = "OP_TRUE";
    private static final Byte opcode = (byte) 0x51;

    @Override
    public void execute(StateMachine stateMachine) {
        stateMachine.push(1);
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

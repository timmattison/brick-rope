package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpCodeSeparator extends CryptoOp {
    private static final String word = "OP_CODESEPARATOR";
    private static final Byte opcode = (byte) 0xab;

    @Override
    public void execute(StateMachine stateMachine) {
        // TODO: Clean this up.  It is hacky.
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

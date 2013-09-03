package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpHash256 extends CryptoOp {
    private static final String word = "OP_HASH256";
    private static final Byte opcode = (byte) 0xaa;

    @Override
    public void execute(StateMachine stateMachine) {
        OpSha256 opSha256 = new OpSha256();

        // SHA-256 hash twice
        opSha256.execute(stateMachine);
        opSha256.execute(stateMachine);
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

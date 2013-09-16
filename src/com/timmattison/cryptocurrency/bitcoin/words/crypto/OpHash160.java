package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpHash160 extends CryptoOp {
    private static final String word = "OP_HASH160";
    private static final Byte opcode = (byte) 0xa9;

    @Override
    public void execute(StateMachine stateMachine) {
        OpSha256 opSha256 = new OpSha256();
        OpRipEmd160 opRipEmd160 = new OpRipEmd160();

        // SHA-256 hash, then RIPEMD-160 hash
        opSha256.execute(stateMachine);
        opRipEmd160.execute(stateMachine);
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

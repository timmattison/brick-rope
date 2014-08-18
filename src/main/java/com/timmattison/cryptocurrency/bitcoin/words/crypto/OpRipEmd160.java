package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.google.inject.Inject;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.standard.hashing.ripemd.RIPEMD160;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * <p/>
 * Hashes the value on the top of the stack with RIPEMD-160
 */
public class OpRipEmd160 extends CryptoOp {
    private static final String word = "OP_RIPEMD160";
    private static final Byte opcode = (byte) 0xa6;
    private final RIPEMD160 ripemd160;

    @Inject
    public OpRipEmd160(RIPEMD160 ripemd160) {
        this.ripemd160 = ripemd160;
    }

    @Override
    public void execute(StateMachine stateMachine) {
        Object value = stateMachine.pop();

        if (!(value instanceof byte[])) {
            throw new UnsupportedOperationException("Only byte arrays can be hashed");
        }

        byte[] byteArrayValue = (byte[]) value;

        ripemd160.initialize(byteArrayValue, byteArrayValue.length * 8);
        byte[] output = new byte[20];
        while(!ripemd160.isFinished()) {
            ripemd160.step();
        }

        throw new UnsupportedOperationException("Not implemented yet!");

        //stateMachine.push(output);
    }

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }
    /*
    @Override
    public void execute(StateMachine stateMachine) {
        byte[] bytesToHash = stateMachine.popByteArray();

        IMessageDigest md = HashFactory.getInstance(algorithm);
        md.update(bytesToHash, 0, bytesToHash.length);
        stateMachine.push(md.digest());
    }
    */
}

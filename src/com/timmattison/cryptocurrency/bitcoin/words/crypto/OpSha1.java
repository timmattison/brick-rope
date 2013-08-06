package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.timmattison.bitcoin.test.script.Constants;
import com.timmattison.bitcoin.test.script.SimpleHashingWord;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * <p/>
 * Hashes the value on the top of the stack with SHA-1
 */
public class OpSha1 extends CryptoOp {
    private static final String algorithm = Constants.SHA1_ALGORITHM;
    private static final String word = "OP_SHA1";
    private static final Byte opcode = (byte) 0xa7;

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

package com.timmattison.bitcoin.test.script.words.crypto;

import com.timmattison.bitcoin.test.script.Constants;
import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;
import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * <p/>
 * Hashes the value on the top of the stack with RIPEMD-160
 */
public class OpRipEmd160 extends Word {
    private static final String algorithm = Constants.RIPEMD160_ALGORITHM;
    private static final String word = "OP_RIPEMD160";
    private static final Byte opcode = (byte) 0xa6;

    public OpRipEmd160() {
        super(word, opcode);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        byte[] bytesToHash = stateMachine.popByteArray();

        IMessageDigest md = HashFactory.getInstance(algorithm);
        md.update(bytesToHash, 0, bytesToHash.length);
        stateMachine.stack.push(md.digest());
    }
}

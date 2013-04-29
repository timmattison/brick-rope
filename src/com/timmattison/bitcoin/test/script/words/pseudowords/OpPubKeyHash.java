package com.timmattison.bitcoin.test.script.words.pseudowords;

import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpPubKeyHash extends Word {
    private static final String word = "OP_PUBKEYHASH";
    private static final Byte opcode = (byte) 0xfd;

    public OpPubKeyHash() {
        super(word, opcode, false);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}

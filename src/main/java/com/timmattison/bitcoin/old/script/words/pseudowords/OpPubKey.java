package com.timmattison.bitcoin.old.script.words.pseudowords;

import com.timmattison.bitcoin.old.script.StateMachine;
import com.timmattison.bitcoin.old.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpPubKey extends Word {
    private static final String word = "OP_PUBKEY";
    private static final Byte opcode = (byte) 0xfe;

    public OpPubKey() {
        super(word, opcode, false);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}

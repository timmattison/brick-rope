package com.timmattison.bitcoin.old.script.words.reservedwords;

import com.timmattison.bitcoin.old.script.StateMachine;
import com.timmattison.bitcoin.old.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpNop8 extends Word {
    private static final String word = "OP_NOP8";
    private static final Byte opcode = (byte) 0xb7;

    public OpNop8() {
        super(word, opcode, false);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}

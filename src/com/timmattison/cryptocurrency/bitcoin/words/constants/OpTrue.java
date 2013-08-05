package com.timmattison.cryptocurrency.bitcoin.words.constants;

import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpTrue extends Word {
    private static final String word = "OP_TRUE";
    private static final Byte opcode = (byte) 0x51;

    public OpTrue() {
        super(word, opcode, false);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}

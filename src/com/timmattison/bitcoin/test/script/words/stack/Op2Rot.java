package com.timmattison.bitcoin.test.script.words.stack;

import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class Op2Rot extends Word {
    private static final String word = "OP_2ROT";
    private static final int opcode = 0x71;

    public Op2Rot() {
        super(word, opcode);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}

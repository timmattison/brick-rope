package com.timmattison.bitcoin.test.script.words.constants;

import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class Op6 extends Word {
    private static final String word = "OP_6";
    private static final Byte opcode = (byte) 0x56;

    public Op6() {
        super(word, opcode, false);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}

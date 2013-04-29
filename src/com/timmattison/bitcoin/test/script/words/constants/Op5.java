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
public class Op5 extends Word {
    private static final String word = "OP_5";
    private static final Byte opcode = (byte) 0x55;

    public Op5() {
        super(word, opcode, true);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}

package com.timmattison.bitcoin.test.script.words.splice;

import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpSubStr extends Word {
    private static final String word = "OP_SUBSTR";
    private static final Byte opcode = (byte) 0x7f;

    public OpSubStr() {
        super(word, opcode, true);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}

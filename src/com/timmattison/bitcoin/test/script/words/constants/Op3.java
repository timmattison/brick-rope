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
public class Op3 extends Word {
    private static final String word = "OP_3";
    private static final int opcode = 0x53;

    public Op3() {
        super(word, opcode);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

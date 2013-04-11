package com.timmattison.bitcoin.test.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Word {
    protected final int opcode;
    protected final String word;
    protected Object output;

    public Word(String word, int opcode) {
        this.opcode = opcode;
        this.word = word;
    }

    public int getOpcode() {
        return opcode;
    }

    public String getWord() {
        return word;
    }

    public Object getOutput() {
        return output;
    }

    public boolean isEnabled() {
        // All words are enabled by default
        return true;
    }

    public abstract void execute(StateMachine stateMachine);
}

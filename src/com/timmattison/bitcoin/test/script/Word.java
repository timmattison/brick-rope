package com.timmattison.bitcoin.test.script;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Word {
    protected final Byte opcode;
    protected final String word;
    protected Object output;
    private boolean innerDebug;

    public Word(String word, Byte opcode, boolean innerDebug) {
        this.opcode = opcode;
        this.word = word;
        this.innerDebug = innerDebug;
    }

    public Byte getOpcode() {
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

    protected boolean isInnerDebug() {
        return innerDebug;
    }
}

package com.timmattison.bitcoin.test.com.timmattison.bitcoin.test.script;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Word {
    private final int opcode;
    private final String word;
    private Object input;
    private Object output;

    public Word(String word, int opcode)
    {
        this.opcode = opcode;
        this.word = word;
    }

    public int getOpcode() { return opcode; }
    public String getWord() { return word; }

    public void setInput(Object input) {
        this.input = input;
    }

    public Object getOutput() {
        return output;
    }

    public abstract void execute(StateMachine stateMachine);
}

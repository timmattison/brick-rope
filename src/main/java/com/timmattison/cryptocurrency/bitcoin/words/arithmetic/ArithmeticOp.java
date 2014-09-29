package com.timmattison.cryptocurrency.bitcoin.words.arithmetic;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.bitcoin.WordDumper;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 3:53 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ArithmeticOp implements Word {
    private final WordDumper wordDumper;

    @Inject
    protected ArithmeticOp(WordDumper wordDumper) {
        this.wordDumper = wordDumper;
    }

    @Override
    public final byte[] build(byte[] data) {
        // Arithmetic operations manipulate the stack but do not consume any bytes
        return data;
    }

    @Override
    public final boolean isEnabled() {
        return true;
    }

    @Override
    public Object getOutput() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public byte[] dump() {
        byte[] dump = new byte[1];
        dump[0] = getOpcode();
        return dump;
    }

    @Override
    public String prettyDump(int indentationLevel) {
        return wordDumper.prettyDump(indentationLevel, this);
    }
}

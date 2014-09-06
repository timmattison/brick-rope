package com.timmattison.cryptocurrency.bitcoin;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 8:44 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BitcoinWord<T> implements Word {
    protected Byte opcode;
    protected String name;

    public Byte getOpcode() {
        if (opcode == null) {
            throw new IllegalStateException("Opcode cannot be NULL");
        }

        return opcode;
    }

    public String getName() {
        if (name == null) {
            throw new IllegalStateException("Name cannot be NULL");
        }

        return name;
    }

    public abstract T getOutput();

    public boolean isEnabled() {
        // By default all words are enabled
        return true;
    }

    public abstract void execute();
}

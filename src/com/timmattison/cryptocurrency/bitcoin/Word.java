package com.timmattison.cryptocurrency.bitcoin;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 8:44 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Word<T> {
    public Byte getOpcode();

    public String getName();

    public T getOutput();

    public boolean isEnabled();

    public void execute();
}

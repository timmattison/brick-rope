package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.standard.Script;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 9:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface StateMachine {
    public Object pop();

    public void push(Object object);

    public Object peek();

    void reset();

    public void execute(Script script);

    void setPreviousTransactionHash(byte[] previousTransactionHash);

    void setCurrentTransactionHash(byte[] currentTransactionHash);

    byte[] getPreviousTransactionHash();

    byte[] getCurrentTransactionHash();

    void setPreviousOutputIndex(int previousOutputIndex);

    int getPreviousOutputIndex();

    int getInputNumber();

    void setInputNumber(int inputNumber);
}

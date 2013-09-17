package com.timmattison.cryptocurrency.interfaces;

import com.timmattison.cryptocurrency.standard.Script;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:34 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Input extends Buildable {
    public boolean isCoinbase();

    public Script getScript();

    byte[] getPreviousTransactionId();

    long getPreviousOutputIndex();

    void setScript(Script script);
}

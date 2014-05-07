package com.timmattison.cryptocurrency.interfaces;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:32 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Transaction extends Buildable {
    List<Input> getInputs();

    List<Output> getOutputs();

    byte[] getHash();

    void clearInputScripts();

    public int getTransactionNumber();
}

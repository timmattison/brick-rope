package com.timmattison.cryptocurrency.standard.hashing;

import in.bitcha.main.client.hashing.tests.NamedValue;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/16/13
 * Time: 6:42 AM
 * To change this template use File | Settings | File Templates.
 */
public interface HashState {
    public List<NamedValue> getState();

    public boolean isFinished();
}

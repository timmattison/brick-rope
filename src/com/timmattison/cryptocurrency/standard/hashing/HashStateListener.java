package com.timmattison.cryptocurrency.standard.hashing;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/16/13
 * Time: 6:42 AM
 * To change this template use File | Settings | File Templates.
 */
public interface HashStateListener {
    public void stateUpdated(HashState hashState, String stateInfo);
}

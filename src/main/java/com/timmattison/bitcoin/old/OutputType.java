package com.timmattison.bitcoin.old;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/7/13
 * Time: 6:58 AM
 * To change this template use File | Settings | File Templates.
 */
public enum OutputType {
    SingleSigned,          // This is a normal output to a single address
    ProvablyUnspendable,   // This is an output that can never be spent.  This amount of Bitcoins is lost forever.
    AnyoneCanSpend,        // This output can be spent by anyone
    HashingPuzzle,         // This output can be spent by anyone who can solve the hashing puzzle
    Unknown                // This is an address that is not classified yet
}

package com.timmattison.bitcoin.old.script;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SimpleHashingWord extends Word {
    public SimpleHashingWord(String word, Byte opcode) {
        super(word, opcode, false);
    }

    /**
     * Gets the name of the hashing algorithm that can be used in a MessageDigest.getInstance call
     *
     * @return
     */
    public abstract String getHashingAlgorithm();

    @Override
    public void execute(StateMachine stateMachine) {
        // Get the hashing algorithm name
        String algorithm = getHashingAlgorithm();

        try {
            // Get an instance of the hashing algorithm
            MessageDigest md = MessageDigest.getInstance(algorithm);

            // Get the data to hash
            byte[] bytesToHash = (byte[]) stateMachine.pop();

            // Hash it
            md.update(bytesToHash);

            // Push the result back on the stack
            stateMachine.push(md.digest());
        } catch (NoSuchAlgorithmException e) {
            // Algorithm wasn't found, throw an exception
            throw new UnsupportedOperationException("Hashing algorithm " + algorithm + " doesn't exist");
        }
    }
}

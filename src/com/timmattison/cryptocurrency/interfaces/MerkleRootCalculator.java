package com.timmattison.cryptocurrency.interfaces;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/8/13
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MerkleRootCalculator {
    byte[] calculateMerkleRoot(List<byte[]> input);
}

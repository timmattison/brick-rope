package com.timmattison.cryptocurrency.factories;

import com.timmattison.cryptocurrency.interfaces.BlockChain;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/10/13
 * Time: 7:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockChainFactory {
    BlockChain getBlockChain(String filename);
}

package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.interfaces.BlockChain;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bitcoin {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChain.class);
    }
}

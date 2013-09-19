package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.standard.StandardBlock;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/4/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlock extends StandardBlock {
    @Inject
    public BitcoinBlock(BlockHeaderFactory blockHeaderFactory, TransactionFactory transactionFactory) {
        super(blockHeaderFactory, transactionFactory);
    }
}

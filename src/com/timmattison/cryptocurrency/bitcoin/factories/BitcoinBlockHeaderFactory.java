package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinBlockHeader;
import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;
import com.timmattison.cryptocurrency.interfaces.TargetFactory;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/5/13
 * Time: 7:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockHeaderFactory implements BlockHeaderFactory {
    private final HasherFactory hasherFactory;
    private final TargetFactory targetFactory;

    @Inject
    BitcoinBlockHeaderFactory(HasherFactory hasherFactory, TargetFactory targetFactory) {
        this.hasherFactory = hasherFactory;
        this.targetFactory = targetFactory;
    }

    @Override
    public BlockHeader createBlockHeader() {
        return new BitcoinBlockHeader(hasherFactory, targetFactory);
    }
}

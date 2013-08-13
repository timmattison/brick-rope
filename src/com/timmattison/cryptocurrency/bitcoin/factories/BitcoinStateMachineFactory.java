package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinBlockHeader;
import com.timmattison.cryptocurrency.bitcoin.BitcoinStateMachine;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.factories.WordFactory;
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
public class BitcoinStateMachineFactory implements StateMachineFactory {
    private final WordFactory wordFactory;

    @Inject
    BitcoinStateMachineFactory(WordFactory wordFactory) {
        this.wordFactory = wordFactory;
    }

    @Override
    public StateMachine createStateMachine() {
        return new BitcoinStateMachine(wordFactory);
    }
}

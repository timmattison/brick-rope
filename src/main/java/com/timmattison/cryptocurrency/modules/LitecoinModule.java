package com.timmattison.cryptocurrency.modules;

import com.google.inject.AbstractModule;
import com.timmattison.cryptocurrency.bitcoin.*;
import com.timmattison.cryptocurrency.bitcoin.factories.*;
import com.timmattison.cryptocurrency.factories.*;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.litecoin.LitecoinBlockReader;
import com.timmattison.cryptocurrency.standard.StandardBlockFactory;
import com.timmattison.cryptocurrency.standard.StandardMerkleRootCalculator;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class LitecoinModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BlockReader.class).to(LitecoinBlockReader.class);

        bind(Block.class).to(BitcoinBlock.class);
        bind(BlockHeader.class).to(BitcoinBlockHeader.class);
        bind(Block.class).to(BitcoinBlock.class);
        bind(StateMachine.class).to(BitcoinStateMachine.class);
        bind(BlockValidator.class).to(BitcoinBlockValidator.class);

        bind(TransactionFactory.class).to(BitcoinTransactionFactory.class);
        bind(BlockFactory.class).to(StandardBlockFactory.class);
        bind(BlockHeaderFactory.class).to(BitcoinBlockHeaderFactory.class);
        bind(InputFactory.class).to(BitcoinInputFactory.class);
        bind(OutputFactory.class).to(BitcoinOutputFactory.class);
        bind(ScriptingFactory.class).to(BitcoinScriptingFactory.class);
        bind(TargetFactory.class).to(BitcoinTargetFactory.class);

        bind(HasherFactory.class).to(DoubleSha256Factory.class);

        bind(MerkleRootCalculator.class).to(StandardMerkleRootCalculator.class);
    }
}

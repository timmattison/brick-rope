package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.AbstractModule;
import com.timmattison.cryptocurrency.bitcoin.factories.*;
import com.timmattison.cryptocurrency.interfaces.TargetFactory;
import com.timmattison.cryptocurrency.factories.*;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.StandardBlockFactory;
import com.timmattison.cryptocurrency.standard.StandardBlockChain;
import com.timmattison.cryptocurrency.standard.StandardMerkleRootCalculator;
import com.timmattison.cryptocurrency.standard.hashing.chunks.ChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.chunks.StandardChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.padding.MessagePadder;
import com.timmattison.cryptocurrency.standard.hashing.padding.StandardMessagePadder;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BlockReader.class).to(BitcoinBlockReader.class);

        bind(BlockChain.class).to(StandardBlockChain.class);
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
        bind(ScriptFactory.class).to(BitcoinScriptFactory.class);
        bind(WordFactory.class).to(BitcoinWordFactory.class);
        bind(TargetFactory.class).to(BitcoinTargetFactory.class);
        bind(StateMachineFactory.class).to(BitcoinStateMachineFactory.class);

        bind(HasherFactory.class).to(DoubleSha256Factory.class);

        bind(MerkleRootCalculator.class).to(StandardMerkleRootCalculator.class);
        //bind(MerkleRootCalculator.class).to(ParallelMerkleRootCalculator.class);

        // For hashing
        bind(ChunkExtractor.class).to(StandardChunkExtractor.class);
        bind(MessagePadder.class).to(StandardMessagePadder.class);
    }
}

package com.timmattison.cryptocurrency.modules;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.timmattison.crypto.ecc.fp.*;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.crypto.ecc.random.impl.RealBigIntegerRandom;
import com.timmattison.crypto.ecc.random.interfaces.BigIntegerRandom;
import com.timmattison.crypto.ecc.random.interfaces.RandomFactory;
import com.timmattison.cryptocurrency.bitcoin.*;
import com.timmattison.cryptocurrency.bitcoin.factories.*;
import com.timmattison.cryptocurrency.factories.*;
import com.timmattison.cryptocurrency.fakes.FakeBitcoinBlockReader;
import com.timmattison.cryptocurrency.fakes.FakeBlockChainFactory;
import com.timmattison.cryptocurrency.fakes.FakeBlockValidator;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.StandardBlockFactory;
import com.timmattison.cryptocurrency.standard.StandardMerkleRootCalculator;
import com.timmattison.cryptocurrency.standard.hashing.chunks.ChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.chunks.StandardChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.padding.MessagePadder;
import com.timmattison.cryptocurrency.standard.hashing.padding.StandardMessagePadder;
import com.timmattison.cryptocurrency.standard.hashing.sha.DoubleSha256Hash;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinFastExtractorModule extends AbstractModule {
    private final int blockToExtract;

    public BitcoinFastExtractorModule(int blockToExtract) {
        this.blockToExtract = blockToExtract;
    }

    @Override
    protected void configure() {
        // Set the number of blocks to skip
        bind(Integer.class).annotatedWith(Names.named("blocksToSkip")).toInstance(blockToExtract);

        // Default blockchain file
        bind(String.class).annotatedWith(Names.named("defaultBlockChain")).toInstance("/Users/timmattison/Desktop/bitcoin-blockchain.dat");

        // Use a fake Bitcoin block reader that skips blocks very quickly
        bind(BlockReader.class).to(FakeBitcoinBlockReader.class);

        // Use a fake block validator that always returns true
        bind(BlockValidator.class).to(FakeBlockValidator.class);

        // Use a fake block chain factory
        bind(BlockChainFactory.class).to(FakeBlockChainFactory.class);

        bind(Block.class).to(BitcoinBlock.class);
        bind(BlockHeader.class).to(BitcoinBlockHeader.class);
        bind(Block.class).to(BitcoinBlock.class);
        bind(StateMachine.class).to(BitcoinStateMachine.class);

        bind(TransactionFactory.class).to(BitcoinTransactionFactory.class);
        bind(TransactionLocator.class).to(BitcoinInMemoryTransactionLocator.class);
        bind(BlockFactory.class).to(StandardBlockFactory.class);
        bind(BlockHeaderFactory.class).to(BitcoinBlockHeaderFactory.class);
        bind(InputFactory.class).to(BitcoinInputFactory.class);
        bind(OutputFactory.class).to(BitcoinOutputFactory.class);
        bind(ScriptingFactory.class).to(BitcoinScriptingFactory.class);
        bind(TargetFactory.class).to(BitcoinTargetFactory.class);
        install(new FactoryModuleBuilder().implement(StateMachine.class, BitcoinStateMachine.class).build(StateMachineFactory.class));
        bind(SignatureProcessor.class).to(BitcoinSignatureProcessor.class);

        // ECC bindings
        bind(ECCParamsFactory.class).to(BitcoinECCParamsFactory.class);

        bind(ECCCurve.class).to(ECCurveFp.class);
        bind(ECCNamedCurveFp.class).to(SECNamedCurveFp.class);
        bind(ECCParameters.class).to(ECParametersFp.class);
        bind(ECCFieldElement.class).to(ECFieldElementFp.class);
        bind(ECCPoint.class).to(ECPointFp.class);

        bind(ECCKeyPair.class).to(ECKeyPairFp.class);
        bind(ECCSignature.class).to(ECSignatureFp.class);

        install(new FactoryModuleBuilder().implement(ECCCurve.class, ECCurveFp.class).build(ECCCurveFactory.class));
        install(new FactoryModuleBuilder().implement(ECCNamedCurveFp.class, SECNamedCurveFp.class).build(ECCNamedCurveFactory.class));
        install(new FactoryModuleBuilder().implement(ECCParameters.class, ECParametersFp.class).build(ECCParametersFactory.class));
        install(new FactoryModuleBuilder().implement(ECCFieldElement.class, ECFieldElementFp.class).build(ECCFieldElementFactory.class));
        install(new FactoryModuleBuilder().implement(ECCPoint.class, ECPointFp.class).build(ECCPointFactory.class));
        install(new FactoryModuleBuilder().implement(ECCKeyPair.class, ECKeyPairFp.class).build(ECCKeyPairFactory.class));
        install(new FactoryModuleBuilder().implement(ECCSignature.class, ECSignatureFp.class).build(ECCSignatureFactory.class));

        // Message signing
        bind(ECCMessageSigner.class).to(ECMessageSignerFp.class);
        install(new FactoryModuleBuilder().implement(ECCMessageSigner.class, ECMessageSignerFp.class).build(ECCMessageSignerFactory.class));
        install(new FactoryModuleBuilder().implement(Hash.class, DoubleSha256Hash.class).build(ECCMessageSignerHashFactory.class));
        bind(BigIntegerRandom.class).to(RealBigIntegerRandom.class);
        install(new FactoryModuleBuilder().implement(Random.class, Random.class).build(RandomFactory.class));
        install(new FactoryModuleBuilder().implement(ECCMessageSignatureVerifier.class, ECMessageSignatureVerifierFp.class).build(ECCMessageSignatureVerifierFactory.class));

        // Merkle root calculation
        bind(MerkleRootCalculator.class).to(StandardMerkleRootCalculator.class);

        // For hashing
        bind(HasherFactory.class).to(DoubleSha256Factory.class);
        bind(ChunkExtractor.class).to(StandardChunkExtractor.class);
        bind(MessagePadder.class).to(StandardMessagePadder.class);
    }
}

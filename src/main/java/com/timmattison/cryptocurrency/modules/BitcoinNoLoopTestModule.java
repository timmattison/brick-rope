package com.timmattison.cryptocurrency.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
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
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.blockstorage.database.H2BlockStorage;
import com.timmattison.cryptocurrency.standard.NoLoopVariableLengthInteger;
import com.timmattison.cryptocurrency.standard.StandardBlockFactory;
import com.timmattison.cryptocurrency.standard.StandardMerkleRootCalculator;
import com.timmattison.cryptocurrency.standard.hashing.chunks.ChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.chunks.StandardChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.padding.MessagePadder;
import com.timmattison.cryptocurrency.standard.hashing.padding.StandardMessagePadder;
import com.timmattison.cryptocurrency.standard.hashing.sha.DoubleSha256Hash;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;
import com.timmattison.cryptocurrency.standard.interfaces.VariableLengthInteger;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinNoLoopTestModule extends AbstractModule {
    @Override
    protected void configure() {
        // Default blockchain file
        bind(String.class).annotatedWith(Names.named("defaultBlockChain")).toInstance("/Users/timmattison/Desktop/bitcoin-blockchain.dat");
        bind(BlockReader.class).to(BitcoinBlockReader.class);

        bind(Block.class).to(BitcoinBlock.class);
        bind(BlockHeader.class).to(BitcoinBlockHeader.class);
        bind(Block.class).to(BitcoinBlock.class);
        bind(StateMachine.class).to(BitcoinStateMachine.class);
        bind(BlockValidator.class).to(BitcoinBlockValidator.class);

        bind(TransactionFactory.class).to(BitcoinTransactionFactory.class);
        bind(TransactionLocator.class).to(BitcoinInMemoryTransactionLocator.class);
        bind(BlockFactory.class).to(StandardBlockFactory.class);
        bind(BlockHeaderFactory.class).to(BitcoinBlockHeaderFactory.class);
        bind(InputFactory.class).to(BitcoinInputFactory.class);
        bind(OutputFactory.class).to(BitcoinOutputFactory.class);
        bind(ScriptingFactory.class).to(BitcoinScriptingFactory.class);
        bind(TargetFactory.class).to(BitcoinTargetFactory.class);
        bind(StateMachineFactory.class).to(BitcoinStateMachineFactory.class);
        bind(SignatureProcessorFactory.class).to(BitcoinSignatureProcessorFactory.class);
        bind(BlockChainFactory.class).to(BitcoinBlockChainFactory.class);

        install(new FactoryModuleBuilder().implement(VariableLengthInteger.class, NoLoopVariableLengthInteger.class).build(VariableLengthIntegerFactory.class));

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

        // For storage
        bind(BlockStorage.class).to(H2BlockStorage.class).in(Singleton.class);
    }
}

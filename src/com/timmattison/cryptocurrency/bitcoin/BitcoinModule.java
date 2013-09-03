package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.timmattison.crypto.ecc.factories.SHA1MessageSignerDigestFactory;
import com.timmattison.crypto.ecc.fp.*;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.crypto.ecc.random.impl.RealBigIntegerRandom;
import com.timmattison.crypto.ecc.random.interfaces.BigIntegerRandom;
import com.timmattison.crypto.ecc.random.interfaces.RandomFactory;
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

import java.util.Random;

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
        bind(SignatureProcessorFactory.class).to(BitcoinSignatureProcessorFactory.class);

        // ECC bindings
        bind(ECCParamsFactory.class).to(BitcoinECCParamsFactory.class);
        bind(ECCParamsFactory.class).to(BitcoinECCParamsFactory.class);

        bind(ECCCurve.class).to(ECCurveFp.class);
        bind(ECCNamedCurve.class).to(SECNamedCurve.class);
        bind(ECCParameters.class).to(ECParametersFp.class);
        bind(ECCFieldElement.class).to(ECFieldElementFp.class);
        bind(ECCPoint.class).to(ECPointFp.class);

        bind(ECCKeyPair.class).to(ECKeyPairFp.class);
        bind(ECCSignature.class).to(ECSignatureFp.class);

        install(new FactoryModuleBuilder().implement(ECCCurve.class, ECCurveFp.class).build(ECCCurveFactory.class));
        install(new FactoryModuleBuilder().implement(ECCNamedCurve.class, SECNamedCurve.class).build(ECCNamedCurveFactory.class));
        install(new FactoryModuleBuilder().implement(ECCParameters.class, ECParametersFp.class).build(ECCParametersFactory.class));
        install(new FactoryModuleBuilder().implement(ECCFieldElement.class, ECFieldElementFp.class).build(ECCFieldElementFactory.class));
        install(new FactoryModuleBuilder().implement(ECCPoint.class, ECPointFp.class).build(ECCPointFactory.class));
        install(new FactoryModuleBuilder().implement(ECCKeyPair.class, ECKeyPairFp.class).build(com.timmattison.crypto.ecc.interfaces.ECCKeyPairFactory.class));
        install(new FactoryModuleBuilder().implement(ECCSignature.class, ECSignatureFp.class).build(com.timmattison.crypto.ecc.interfaces.ECCSignatureFactory.class));

        // Message signing
        bind(ECCMessageSigner.class).to(ECMessageSignerFp.class);
        install(new FactoryModuleBuilder().implement(ECCMessageSigner.class, ECMessageSignerFp.class).build(ECCMessageSignerFactory.class));
        bind(ECCMessageSignerDigestFactory.class).to(SHA1MessageSignerDigestFactory.class);
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

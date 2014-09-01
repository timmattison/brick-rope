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
import com.timmattison.cryptocurrency.standard.*;
import com.timmattison.cryptocurrency.standard.hashing.chunks.ChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.chunks.StandardChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.padding.MessagePadder;
import com.timmattison.cryptocurrency.standard.hashing.padding.StandardMessagePadder;
import com.timmattison.cryptocurrency.standard.hashing.sha.DoubleSha256Hash;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;
import com.timmattison.cryptocurrency.standard.interfaces.VariableLengthInteger;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinModule extends AbstractModule {
    public static final String DATABASE_FILE_NAME = "databaseFile";
    public static final String DATABASE_NAME_NAME = "databaseName";
    public static final String BLOCKCHAIN_FILE_NAME = "blockchainFile";
    private static final int threads = 10;

    private String databaseFile;
    private String databaseName;
    private boolean useH2Storage = false;
    private String blockchainFile;
    private boolean usePostgresqlStorage = false;

    public void useH2Storage(String databaseFile) {
        this.databaseFile = databaseFile;
        this.useH2Storage = true;
    }

    public void usePostgresqlStorage(String databaseName) {
        this.databaseName = databaseName;
        this.usePostgresqlStorage = true;
    }

    public void useBlockChainFile(String blockchainFile) {
        this.blockchainFile = blockchainFile;
    }

    @Override
    protected void configure() {
        if (blockchainFile == null) {
            // Default blockchain file
            bind(String.class).annotatedWith(Names.named(BLOCKCHAIN_FILE_NAME)).toInstance("/Users/timmattison/Desktop/bitcoin-blockchain.dat");
        } else {
            bind(String.class).annotatedWith(Names.named(BLOCKCHAIN_FILE_NAME)).toInstance(blockchainFile);
        }

        bind(BlockReader.class).to(BitcoinBlockReader.class);

        bind(Block.class).to(BitcoinBlock.class);
        bind(BlockHeader.class).to(BitcoinBlockHeader.class);
        bind(Block.class).to(BitcoinBlock.class);
        bind(StateMachine.class).to(BitcoinStateMachine.class);
        bind(BlockValidator.class).to(BitcoinBlockValidator.class);

        bind(TransactionFactory.class).to(BitcoinTransactionFactory.class);
        bind(TransactionLocator.class).to(BitcoinBlockStorageTransactionLocator.class);
        bind(BlockFactory.class).to(StandardBlockFactory.class);
        bind(BlockHeaderFactory.class).to(BitcoinBlockHeaderFactory.class);
        bind(InputFactory.class).to(BitcoinInputFactory.class);
        bind(OutputFactory.class).to(BitcoinOutputFactory.class);
        bind(ScriptingFactory.class).to(BitcoinScriptingFactory.class);
        bind(TargetFactory.class).to(BitcoinTargetFactory.class);
        bind(StateMachineFactory.class).to(BitcoinStateMachineFactory.class);
        bind(SignatureProcessorFactory.class).to(BitcoinSignatureProcessorFactory.class);
        bind(BlockChainFactory.class).to(BitcoinBlockChainFactory.class);
        bind(TransactionValidator.class).to(BitcoinParallelTransactionValidator.class);
        bind(TransactionListValidator.class).to(BitcoinParallelTransactionListValidator.class);

        install(new FactoryModuleBuilder().implement(VariableLengthInteger.class, StandardVariableLengthInteger.class).build(VariableLengthIntegerFactory.class));

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
        install(new FactoryModuleBuilder().implement(ECCKeyPair.class, ECKeyPairFp.class).build(com.timmattison.crypto.ecc.interfaces.ECCKeyPairFactory.class));
        install(new FactoryModuleBuilder().implement(ECCSignature.class, ECSignatureFp.class).build(com.timmattison.crypto.ecc.interfaces.ECCSignatureFactory.class));

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
        if (useH2Storage) {
            bind(String.class).annotatedWith(Names.named(DATABASE_FILE_NAME)).toInstance(databaseFile);
            bind(BlockStorage.class).to(H2BlockStorage.class).in(Singleton.class);
        }

        // For storage
        if (usePostgresqlStorage) {
            bind(String.class).annotatedWith(Names.named(DATABASE_NAME_NAME)).toInstance(databaseName);
            bind(BlockStorage.class).to(PostgresqlBlockStorage.class).in(Singleton.class);
        }

        // For multithreading
        //bind(ExecutorService.class).toInstance(Executors.newFixedThreadPool(threads));
        bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
    }
}

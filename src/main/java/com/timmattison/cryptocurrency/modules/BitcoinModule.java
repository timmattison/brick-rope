package com.timmattison.cryptocurrency.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.timmattison.crypto.ecc.fp.*;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.crypto.ecc.random.impl.RealBigIntegerRandom;
import com.timmattison.crypto.ecc.random.interfaces.BigIntegerRandom;
import com.timmattison.crypto.ecc.random.interfaces.RandomFactory;
import com.timmattison.cryptocurrency.bitcoin.*;
import com.timmattison.cryptocurrency.bitcoin.factories.*;
import com.timmattison.cryptocurrency.bitcoin.words.arithmetic.*;
import com.timmattison.cryptocurrency.bitcoin.words.bitwiselogic.*;
import com.timmattison.cryptocurrency.bitcoin.words.constants.*;
import com.timmattison.cryptocurrency.bitcoin.words.crypto.*;
import com.timmattison.cryptocurrency.bitcoin.words.flowcontrol.*;
import com.timmattison.cryptocurrency.bitcoin.words.pseudowords.OpInvalidOpcode;
import com.timmattison.cryptocurrency.bitcoin.words.pseudowords.OpPubKey;
import com.timmattison.cryptocurrency.bitcoin.words.pseudowords.OpPubKeyHash;
import com.timmattison.cryptocurrency.bitcoin.words.reservedwords.*;
import com.timmattison.cryptocurrency.bitcoin.words.splice.*;
import com.timmattison.cryptocurrency.bitcoin.words.stack.*;
import com.timmattison.cryptocurrency.factories.*;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.StandardBlockFactory;
import com.timmattison.cryptocurrency.standard.StandardMerkleRootCalculator;
import com.timmattison.cryptocurrency.standard.StandardVariableLengthInteger;
import com.timmattison.cryptocurrency.standard.blockstorage.database.H2BlockStorage;
import com.timmattison.cryptocurrency.standard.blockstorage.database.MySqlBlockStorage;
import com.timmattison.cryptocurrency.standard.blockstorage.database.PostgresqlBlockStorage;
import com.timmattison.cryptocurrency.standard.blockstorage.database.StorageType;
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

    private String databaseFile;
    private String databaseName;
    private StorageType storageType = null;
    private String blockchainFile;

    public void useH2Storage(String databaseFile) {
        throwExceptionOnMultipleStorageTypes();
        this.databaseFile = databaseFile;
        this.storageType = StorageType.H2;
    }

    private void throwExceptionOnMultipleStorageTypes() {
        if (storageType != null) {
            throw new UnsupportedOperationException("Can't use multiple storage types");
        }
    }

    public void usePostgresqlStorage(String databaseName) {
        throwExceptionOnMultipleStorageTypes();
        this.databaseName = databaseName;
        this.storageType = StorageType.PostgreSQL;
    }

    public void useMySqlStorage(String databaseName) {
        throwExceptionOnMultipleStorageTypes();
        this.databaseName = databaseName;
        this.storageType = StorageType.MySQL;
    }

    public void useBlockChainFile(String blockchainFile) {
        this.blockchainFile = blockchainFile;
    }

    @Override
    protected void configure() {
        if (blockchainFile == null) {
            // Default blockchain file
            //bind(String.class).annotatedWith(Names.named(BLOCKCHAIN_FILE_NAME)).toInstance("/Users/timmattison/Desktop/bitcoin-blockchain.dat");
            bind(String.class).annotatedWith(Names.named(BLOCKCHAIN_FILE_NAME)).toInstance("bitcoin-blockchain.dat");
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
        install(new FactoryModuleBuilder().implement(StateMachine.class, BitcoinStateMachine.class).build(StateMachineFactory.class));
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
        if (storageType == null) {
            throw new UnsupportedOperationException("No storage type specified");
        }

        if (storageType.equals(StorageType.H2)) {
            bind(String.class).annotatedWith(Names.named(DATABASE_FILE_NAME)).toInstance(databaseFile);
            bind(BlockStorage.class).to(H2BlockStorage.class).in(Singleton.class);
        } else if (storageType.equals(StorageType.PostgreSQL)) {
            bind(String.class).annotatedWith(Names.named(DATABASE_NAME_NAME)).toInstance(databaseName);
            bind(BlockStorage.class).to(PostgresqlBlockStorage.class).in(Singleton.class);
        } else if (storageType.equals(StorageType.MySQL)) {
            bind(String.class).annotatedWith(Names.named(DATABASE_NAME_NAME)).toInstance(databaseName);
            bind(BlockStorage.class).to(MySqlBlockStorage.class).in(Singleton.class);
        } else {
            throw new UnsupportedOperationException("Unknown storage type [" + storageType + "]");
        }

        // For multithreading
        //bind(ExecutorService.class).toInstance(Executors.newFixedThreadPool(threads));
        bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());

        bindOpCodes();
    }

    private void bindOpCodes() {
        Multibinder<Word> wordMultibinder = Multibinder.newSetBinder(binder(), Word.class);

        wordMultibinder.addBinding().to(Op0NotEqual.class);
        wordMultibinder.addBinding().to(Op1Add.class);
        wordMultibinder.addBinding().to(Op1Sub.class);
        wordMultibinder.addBinding().to(Op2Div.class);
        wordMultibinder.addBinding().to(Op2Mul.class);
        wordMultibinder.addBinding().to(OpAbs.class);
        wordMultibinder.addBinding().to(OpAdd.class);
        wordMultibinder.addBinding().to(OpBoolAnd.class);
        wordMultibinder.addBinding().to(OpBoolOr.class);
        wordMultibinder.addBinding().to(OpDiv.class);
        wordMultibinder.addBinding().to(OpGreaterThan.class);
        wordMultibinder.addBinding().to(OpGreaterThanOrEqual.class);
        wordMultibinder.addBinding().to(OpLessThan.class);
        wordMultibinder.addBinding().to(OpLessThanOrEqual.class);
        wordMultibinder.addBinding().to(OpLShift.class);
        wordMultibinder.addBinding().to(OpMax.class);
        wordMultibinder.addBinding().to(OpMin.class);
        wordMultibinder.addBinding().to(OpMod.class);
        wordMultibinder.addBinding().to(OpMul.class);
        wordMultibinder.addBinding().to(OpNegate.class);
        wordMultibinder.addBinding().to(OpNot.class);
        wordMultibinder.addBinding().to(OpNumEqual.class);
        wordMultibinder.addBinding().to(OpNumEqualVerify.class);
        wordMultibinder.addBinding().to(OpNumNotEqual.class);
        wordMultibinder.addBinding().to(OpRShift.class);
        wordMultibinder.addBinding().to(OpSub.class);
        wordMultibinder.addBinding().to(OpWithin.class);
        wordMultibinder.addBinding().to(OpAnd.class);
        wordMultibinder.addBinding().to(OpEqual.class);
        wordMultibinder.addBinding().to(OpEqualVerify.class);
        wordMultibinder.addBinding().to(OpInvert.class);
        wordMultibinder.addBinding().to(OpOr.class);
        wordMultibinder.addBinding().to(OpXor.class);
        wordMultibinder.addBinding().to(Op0.class);
        wordMultibinder.addBinding().to(Op1.class);
        wordMultibinder.addBinding().to(Op10.class);
        wordMultibinder.addBinding().to(Op11.class);
        wordMultibinder.addBinding().to(Op12.class);
        wordMultibinder.addBinding().to(Op13.class);
        wordMultibinder.addBinding().to(Op14.class);
        wordMultibinder.addBinding().to(Op15.class);
        wordMultibinder.addBinding().to(Op16.class);
        wordMultibinder.addBinding().to(Op1Negate.class);
        wordMultibinder.addBinding().to(Op2.class);
        wordMultibinder.addBinding().to(Op3.class);
        wordMultibinder.addBinding().to(Op4.class);
        wordMultibinder.addBinding().to(Op5.class);
        wordMultibinder.addBinding().to(Op6.class);
        wordMultibinder.addBinding().to(Op7.class);
        wordMultibinder.addBinding().to(Op8.class);
        wordMultibinder.addBinding().to(Op9.class);
        wordMultibinder.addBinding().to(OpFalse.class);
        wordMultibinder.addBinding().to(OpPushData1.class);
        wordMultibinder.addBinding().to(OpPushData2.class);
        wordMultibinder.addBinding().to(OpPushData4.class);
        wordMultibinder.addBinding().to(OpTrue.class);
        wordMultibinder.addBinding().to(OpCodeSeparator.class);
        wordMultibinder.addBinding().to(OpHash160.class);
        wordMultibinder.addBinding().to(OpHash256.class);
        wordMultibinder.addBinding().to(OpRipEmd160.class);
        wordMultibinder.addBinding().to(OpSha1.class);
        wordMultibinder.addBinding().to(OpSha256.class);
        wordMultibinder.addBinding().to(OpElse.class);
        wordMultibinder.addBinding().to(OpEndIf.class);
        wordMultibinder.addBinding().to(OpIf.class);
        wordMultibinder.addBinding().to(OpNop.class);
        wordMultibinder.addBinding().to(OpNotIf.class);
        wordMultibinder.addBinding().to(OpReturn.class);
        wordMultibinder.addBinding().to(OpVerify.class);
        wordMultibinder.addBinding().to(OpInvalidOpcode.class);
        wordMultibinder.addBinding().to(OpPubKey.class);
        wordMultibinder.addBinding().to(OpPubKeyHash.class);
        wordMultibinder.addBinding().to(OpNop1.class);
        wordMultibinder.addBinding().to(OpNop10.class);
        wordMultibinder.addBinding().to(OpNop2.class);
        wordMultibinder.addBinding().to(OpNop3.class);
        wordMultibinder.addBinding().to(OpNop4.class);
        wordMultibinder.addBinding().to(OpNop5.class);
        wordMultibinder.addBinding().to(OpNop6.class);
        wordMultibinder.addBinding().to(OpNop7.class);
        wordMultibinder.addBinding().to(OpNop8.class);
        wordMultibinder.addBinding().to(OpNop9.class);
        wordMultibinder.addBinding().to(OpReserved.class);
        wordMultibinder.addBinding().to(OpReserved1.class);
        wordMultibinder.addBinding().to(OpReserved2.class);
        wordMultibinder.addBinding().to(OpVer.class);
        wordMultibinder.addBinding().to(OpVerIf.class);
        wordMultibinder.addBinding().to(OpVerNotIf.class);
        wordMultibinder.addBinding().to(OpCat.class);
        wordMultibinder.addBinding().to(OpLeft.class);
        wordMultibinder.addBinding().to(OpRight.class);
        wordMultibinder.addBinding().to(OpSize.class);
        wordMultibinder.addBinding().to(OpSubStr.class);
        wordMultibinder.addBinding().to(Op2Drop.class);
        wordMultibinder.addBinding().to(Op2Dup.class);
        wordMultibinder.addBinding().to(Op2Over.class);
        wordMultibinder.addBinding().to(Op2Rot.class);
        wordMultibinder.addBinding().to(Op2Swap.class);
        wordMultibinder.addBinding().to(Op3Dup.class);
        wordMultibinder.addBinding().to(OpDepth.class);
        wordMultibinder.addBinding().to(OpDrop.class);
        wordMultibinder.addBinding().to(OpDup.class);
        wordMultibinder.addBinding().to(OpFromAltStack.class);
        wordMultibinder.addBinding().to(OpIfDup.class);
        wordMultibinder.addBinding().to(OpNip.class);
        wordMultibinder.addBinding().to(OpOver.class);
        wordMultibinder.addBinding().to(OpPick.class);
        wordMultibinder.addBinding().to(OpRoll.class);
        wordMultibinder.addBinding().to(OpRot.class);
        wordMultibinder.addBinding().to(OpSwap.class);
        wordMultibinder.addBinding().to(OpToAltStack.class);
        wordMultibinder.addBinding().to(OpTuck.class);

        wordMultibinder.addBinding().to(OpCheckSig.class);
        wordMultibinder.addBinding().to(OpCheckMultiSig.class);
    }
}

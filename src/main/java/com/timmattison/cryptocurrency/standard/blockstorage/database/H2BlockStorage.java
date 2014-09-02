package com.timmattison.cryptocurrency.standard.blockstorage.database;

import com.google.inject.name.Named;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.modules.BitcoinModule;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;

/**
 * Created by timmattison on 4/15/14.
 */
public class H2BlockStorage extends AbstractDatabaseBlockStorage {
    private static final String h2Driver = "org.h2.Driver";
    private static final String h2JdbcPrefix = "jdbc:h2:";
    private static final String blocksTable = "BLOCKS";
    private static final String transactionsTable = "TRANSACTIONS";
    private static final String transactionHashField = "transactionHash";
    private static final String transactionNumberField = "transactionNumber";
    private static final String blockNumberField = "blockNumber";
    private static final String blockField = "block";
    private static final String blockOffsetField = "blockOffset";
    private static final String createBlocksTableSql = "CREATE TABLE IF NOT EXISTS " + blocksTable + " (" + blockNumberField + " BIGINT not null, " + blockField + " BLOB not null, " + blockOffsetField + " BIGINT not null);";
    private static final String createTransactionsTableSql = "CREATE TABLE IF NOT EXISTS " + transactionsTable + " (" + transactionHashField + " VARCHAR(255) not null, " + blockNumberField + " BIGINT not null, " + transactionNumberField + " BIGINT not null);";
    private static final String createBlockNumberBlocksTableIndexSql = "CREATE INDEX IF NOT EXISTS blocknumber_" + blocksTable + "_index on " + blocksTable + "(blocknumber)";
    private static final String createBlockNumberTransactionsTableIndexSql = "CREATE INDEX IF NOT EXISTS blocknumber_" + transactionsTable + "_index on " + transactionsTable + "(blocknumber)";
    private static final String createTransactionHashTransactionsTableIndexSql = "CREATE INDEX IF NOT EXISTS transactionhash_" + transactionsTable + "_index on " + transactionsTable + "(transactionhash)";
    private static final String getBlockCountSql = "SELECT COUNT(" + blockField + ") FROM " + blocksTable;
    private static final String getLastBlockNumberSql = "SELECT MAX(" + blockNumberField + ") FROM " + blocksTable;
    private static final String getBlockSql = "SELECT " + blockField + " FROM " + blocksTable + " WHERE " + blockNumberField + " = ?";
    private static final String getBlockOffsetSql = "SELECT " + blockOffsetField + " FROM " + blocksTable + " WHERE " + blockNumberField + " = ?";
    private static final String getTransactionSql = "SELECT " + blockNumberField + ", " + transactionNumberField + " FROM " + transactionsTable + " WHERE " + transactionHashField + " = ?";
    private static final String storeBlockSql = "INSERT INTO " + blocksTable + " (" + blockNumberField + ", " + blockField + ", " + blockOffsetField + ") VALUES (?, ?, ?)";
    private static final String storeTransactionSql = "INSERT INTO " + transactionsTable + " (" + transactionHashField + ", " + blockNumberField + ", " + transactionNumberField + ") VALUES (?, ?, ?)";
    private final String databaseName;

    @Inject
    public H2BlockStorage(BlockFactory blockFactory, @Named(BitcoinModule.DATABASE_FILE_NAME) String databaseName) {
        super(blockFactory);
        this.databaseName = databaseName;
    }

    @Override
    protected String getJdbcUrl() {
        return h2JdbcPrefix + databaseName;
    }

    @Override
    protected String getDriverName() {
        return h2Driver;
    }

    @Override
    protected String getCreateBlockNumberBlocksTableIndexSql() {
        return createBlockNumberBlocksTableIndexSql;
    }

    @Override
    protected String getCreateBlockNumberTransactionsTableIndexSql() {
        return createBlockNumberTransactionsTableIndexSql;
    }

    @Override
    protected String getCreateTransactionHashTransactionsTableIndexSql() {
        return createTransactionHashTransactionsTableIndexSql;
    }

    @Override
    protected String getCreateBlocksTableSql() {
        return createBlocksTableSql;
    }

    @Override
    protected String getCreateTransactionsTableSql() {
        return createTransactionsTableSql;
    }

    @Override
    protected String getGetBlockCountSql() {
        return getBlockCountSql;
    }

    @Override
    protected String getGetLastBlockNumberSql() {
        return getLastBlockNumberSql;
    }

    @Override
    protected String getGetBlockSql() {
        return getBlockSql;
    }

    @Override
    protected String getGetBlockOffsetSql() {
        return getBlockOffsetSql;
    }

    @Override
    protected String getGetTransactionSql() {
        return getTransactionSql;
    }

    @Override
    protected String getStoreTransactionSql() {
        return storeTransactionSql;
    }

    @Override
    protected String getStoreBlockSql() {
        return storeBlockSql;
    }
}

package com.timmattison.cryptocurrency.standard;

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
public class H2BlockStorage implements BlockStorage {
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
    private static final String getBlockCountSql = "SELECT COUNT(" + blockField + ") FROM " + blocksTable;
    private static final String getLastBlockNumberSql = "SELECT MAX(" + blockNumberField + ") FROM " + blocksTable;
    private static final String getBlockSql = "SELECT " + blockField + " FROM " + blocksTable + " WHERE " + blockNumberField + " = ?";
    private static final String getBlockOffsetSql = "SELECT " + blockOffsetField + " FROM " + blocksTable + " WHERE " + blockNumberField + " = ?";
    private static final String getTransactionSql = "SELECT " + blockNumberField + ", " + transactionNumberField + " FROM " + transactionsTable + " WHERE " + transactionHashField + " = ?";
    private static final String storeBlockSql = "INSERT INTO " + blocksTable + " (" + blockNumberField + ", " + blockField + ", " + blockOffsetField + ") VALUES (?, ?, ?)";
    private static final String storeTransactionSql = "INSERT INTO " + transactionsTable + " (" + transactionHashField + ", " + blockNumberField + ", " + transactionNumberField + ") VALUES (?, ?, ?)";
    private final BlockFactory blockFactory;
    private final String databaseName;

    private Connection connection;
    private Long currentOffset = 0L;
    private Long lastBlockInserted = -1L;

    @Inject
    public H2BlockStorage(BlockFactory blockFactory, @Named(BitcoinModule.DATABASE_FILE_NAME) String databaseName) {
        this.blockFactory = blockFactory;
        this.databaseName = databaseName;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null) {
            Class.forName(h2Driver);
            connection = DriverManager.getConnection(h2JdbcPrefix + databaseName);
            connection.setAutoCommit(false);

            createTablesIfNecessary(connection);
            // TODO - Don't do this yet because it bloats the database size during the initial build! createIndexesIfNecessary(connection);
        }

        return connection;
    }

    private void createIndexesIfNecessary(Connection connection) throws SQLException {
        String createBlockNumberBlocksTableIndexSql = "CREATE INDEX IF NOT EXISTS blocknumber_" + blocksTable + "_index on " + blocksTable + "(blocknumber)";
        connection.createStatement().execute(createBlockNumberBlocksTableIndexSql);

        String createBlockNumberTransactionsTableIndexSql = "CREATE INDEX IF NOT EXISTS blocknumber_" + transactionsTable + "_index on " + transactionsTable + "(blocknumber)";
        connection.createStatement().execute(createBlockNumberTransactionsTableIndexSql);

        String createTransactionHashTransactionsTableIndexSql = "CREATE INDEX IF NOT EXISTS transactionhash_" + transactionsTable + "_index on " + transactionsTable + "(transactionhash)";
        connection.createStatement().execute(createTransactionHashTransactionsTableIndexSql);
    }

    private void createTablesIfNecessary(Connection connection) throws SQLException {
        connection.createStatement().execute(createBlocksTableSql);
        connection.createStatement().execute(createTransactionsTableSql);
    }

    private PreparedStatement prepareStatement(String sql) throws SQLException, ClassNotFoundException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public int getBlockCount() throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = prepareStatement(getBlockCountSql);

        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.first();
        int blockCount = resultSet.getInt(1);

        return blockCount;
    }

    @Override
    public int getLastBlockNumber() throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = prepareStatement(getLastBlockNumberSql);

        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.first();
        int lastBlockNumber = resultSet.getInt(1);

        return lastBlockNumber;
    }

    @Override
    public Block getBlock(long blockNumber) throws SQLException, ClassNotFoundException, IOException {
        PreparedStatement preparedStatement = prepareStatement(getBlockSql);
        preparedStatement.setLong(1, blockNumber);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.first()) {
            return null;
        }

        return (Block) blockFactory.createBlock(new ByteArrayInputStream(resultSet.getBytes(1)));
    }

    @Override
    public Long getBlockOffset(long blockNumber) throws SQLException, ClassNotFoundException, IOException {
        PreparedStatement preparedStatement = prepareStatement(getBlockOffsetSql);
        preparedStatement.setLong(1, blockNumber);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.first()) {
            return null;
        }

        return resultSet.getLong(1);
    }

    @Override
    public void storeBlock(long blockNumber, Block block) throws SQLException, ClassNotFoundException, IOException {
        // TODO - Do this in a transaction
        innerStoreBlock(blockNumber, block);
        innerStoreTransactions(blockNumber, block);
        connection.commit();
    }

    @Override
    public Transaction getTransaction(String transactionHash) throws SQLException, ClassNotFoundException, IOException {
        PreparedStatement preparedStatement = prepareStatement(getTransactionSql);
        preparedStatement.setString(1, transactionHash);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.first()) {
            return null;
        }

        int blockNumber = resultSet.getInt(1);
        int transactionNumber = resultSet.getInt(2);

        Block block = getBlock(blockNumber);

        Transaction transaction = block.getTransactions().get(transactionNumber);

        return transaction;
    }

    private void innerStoreTransactions(long blockNumber, Block block) throws SQLException, ClassNotFoundException {
        for (Transaction transaction : block.getTransactions()) {
            PreparedStatement preparedStatement = prepareStatement(storeTransactionSql);
            preparedStatement.setString(1, ByteArrayHelper.toHex(transaction.getHash()));
            preparedStatement.setLong(2, blockNumber);
            preparedStatement.setInt(3, transaction.getTransactionNumber());

            preparedStatement.executeUpdate();
        }
    }

    private void innerStoreBlock(long blockNumber, Block block) throws SQLException, ClassNotFoundException, IOException {
        // Are we inserting the next block?
        if(lastBlockInserted != (blockNumber - 1)) {
            // No, we need to re-calculate the offset
            long lastOffset = getBlockOffset(blockNumber - 1);
            currentOffset = lastOffset + getBlock(blockNumber - 1).dump().length;
        }

        PreparedStatement preparedStatement = prepareStatement(storeBlockSql);
        preparedStatement.setLong(1, blockNumber);
        preparedStatement.setBytes(2, block.dump());
        preparedStatement.setLong(3, currentOffset);

        preparedStatement.executeUpdate();

        lastBlockInserted = blockNumber;
        currentOffset += block.dump().length;
    }
}

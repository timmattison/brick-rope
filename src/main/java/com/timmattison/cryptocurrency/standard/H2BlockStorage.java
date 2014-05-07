package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;

/**
 * Created by timmattison on 4/15/14.
 */
public class H2BlockStorage implements BlockStorage {
    private final BlockFactory blockFactory;
    private final TransactionFactory transactionFactory;
    private static final String blocksTableName = "BLOCKS";
    private static final String transactionsTableName = "TRANSACTIONS";
    private Connection connection;

    @Inject
    public H2BlockStorage(BlockFactory blockFactory, TransactionFactory transactionFactory) {
        this.blockFactory = blockFactory;
        this.transactionFactory = transactionFactory;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null) {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/test");

            createTablesIfNecessary(connection);
            // TODO - Don't do this yet because it bloats the database size during the initial build! createIndexesIfNecessary(connection);
        }

        return connection;
    }

    private void createIndexesIfNecessary(Connection connection) throws SQLException {
        String createBlockNumberBlocksTableIndexSql = "CREATE INDEX IF NOT EXISTS blocknumber_" + blocksTableName + "_index on " + blocksTableName + "(blocknumber)";
        connection.createStatement().execute(createBlockNumberBlocksTableIndexSql);

        String createBlockNumberTransactionsTableIndexSql = "CREATE INDEX IF NOT EXISTS blocknumber_" + transactionsTableName + "_index on " + transactionsTableName + "(blocknumber)";
        connection.createStatement().execute(createBlockNumberTransactionsTableIndexSql);

        String createTransactionHashTransactionsTableIndexSql = "CREATE INDEX IF NOT EXISTS transactionhash_" + transactionsTableName + "_index on " + transactionsTableName + "(transactionhash)";
        connection.createStatement().execute(createTransactionHashTransactionsTableIndexSql);
    }

    private void createTablesIfNecessary(Connection connection) throws SQLException {
        String createBlocksTableSql = "CREATE TABLE IF NOT EXISTS " + blocksTableName + " (blockNumber int not null, block BLOB not null);";
        connection.createStatement().execute(createBlocksTableSql);

        String createTransactionsTableSql = "CREATE TABLE IF NOT EXISTS " + transactionsTableName + " (transactionHash BINARY not null, blockNumber int not null, transactionNumber int not null);";
        connection.createStatement().execute(createTransactionsTableSql);
    }

    private PreparedStatement prepareStatement(String sql) throws SQLException, ClassNotFoundException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public Block getBlock(int blockNumber) throws SQLException, ClassNotFoundException, IOException {
        String getBlockSql = "SELECT block FROM " + blocksTableName + " WHERE blockNumber = ?";

        PreparedStatement preparedStatement = prepareStatement(getBlockSql);
        preparedStatement.setInt(1, blockNumber);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.first()) {
            return null;
        }

        return (Block) blockFactory.createBlock(new ByteArrayInputStream(resultSet.getBytes(1)));
    }

    @Override
    public void storeBlock(int blockNumber, Block block) throws SQLException, ClassNotFoundException {
        // TODO - Do this in a transaction
        innerStoreBlock(blockNumber, block);
        innerStoreTransactions(blockNumber, block);
    }

    @Override
    public Transaction getTransaction(byte[] transactionHash) throws SQLException, ClassNotFoundException, IOException {
        String getTransactionSql = "SELECT blockNumber, transactionNumber FROM " + transactionsTableName + " WHERE transactionHash = ?";

        PreparedStatement preparedStatement = prepareStatement(getTransactionSql);
        preparedStatement.setBytes(1, transactionHash);

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

    private void innerStoreTransactions(int blockNumber, Block block) throws SQLException, ClassNotFoundException {
        String storeTransactionSql = "INSERT INTO TRANSACTIONS(transactionHash, blockNumber, transactionNumber) VALUES (?, ?, ?)";

        for (Transaction transaction : block.getTransactions()) {
            PreparedStatement preparedStatement = prepareStatement(storeTransactionSql);
            preparedStatement.setBytes(1, transaction.getHash());
            preparedStatement.setInt(2, blockNumber);
            preparedStatement.setInt(3, transaction.getTransactionNumber());

            preparedStatement.executeUpdate();
        }
    }

    private void innerStoreBlock(int blockNumber, Block block) throws SQLException, ClassNotFoundException {
        String storeBlockSql = "INSERT INTO BLOCKS(blockNumber, block) VALUES (?, ?)";

        PreparedStatement preparedStatement = prepareStatement(storeBlockSql);
        preparedStatement.setInt(1, blockNumber);
        preparedStatement.setBytes(2, block.dump());

        preparedStatement.executeUpdate();
        connection.commit();
    }
}

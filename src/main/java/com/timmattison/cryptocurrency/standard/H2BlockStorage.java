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

            createTableIfNecessary(connection);
        }

        return connection;
    }

    private void createTableIfNecessary(Connection connection) throws SQLException {
        String createBlocksTableSql = "CREATE TABLE IF NOT EXISTS " + blocksTableName + " (blockNumber int not null, block BLOB not null);";
        connection.createStatement().execute(createBlocksTableSql);

        String createTransactionsTableSql = "CREATE TABLE IF NOT EXISTS " + transactionsTableName + " (transactionHash BINARY not null, transaction BLOB not null, blockNumber int not null);";
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
        innerStoreBlock(blockNumber, block);
        innerStoreTransactions(blockNumber, block);
    }

    @Override
    public Transaction getTransaction(byte[] transactionHash) throws SQLException, ClassNotFoundException {
        String getTransactionSql = "SELECT transaction FROM " + transactionsTableName + " WHERE transactionHash = ?";

        PreparedStatement preparedStatement = prepareStatement(getTransactionSql);
        preparedStatement.setBytes(1, transactionHash);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (!resultSet.first()) {
            return null;
        }

        Transaction transaction = transactionFactory.createTransaction(-1);
        transaction.build(resultSet.getBytes(1));

        return transaction;
    }

    private void innerStoreTransactions(int blockNumber, Block block) throws SQLException, ClassNotFoundException {
        String storeTransactionSql = "INSERT INTO TRANSACTIONS(transactionHash, transaction, blockNumber) VALUES (?, ?, ?)";

        for (Transaction transaction : block.getTransactions()) {
            PreparedStatement preparedStatement = prepareStatement(storeTransactionSql);
            preparedStatement.setBytes(1, transaction.getHash());
            preparedStatement.setBytes(2, transaction.dump());
            preparedStatement.setInt(3, blockNumber);

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

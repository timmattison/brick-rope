package com.timmattison.cryptocurrency.standard.blockstorage.database;

import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by timmattison on 4/15/14.
 */
public abstract class AbstractDatabaseBlockStorage implements BlockStorage {
    private Connection connection;
    private final ExecutorService executorService;
    private final BlockFactory blockFactory;
    protected long lastBlockInserted;
    protected long currentOffset;

    @Inject
    public AbstractDatabaseBlockStorage(ExecutorService executorService, BlockFactory blockFactory) {
        this.executorService = executorService;
        this.blockFactory = blockFactory;
    }

    protected Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null) {
            Class.forName(getDriverName());
            String fullJdbcUrl = getJdbcUrl();
            connection = DriverManager.getConnection(fullJdbcUrl);
            connection.setAutoCommit(false);

            createTablesIfNecessary(connection);
            createIndexesIfNecessary(connection);
        }

        return connection;
    }

    protected abstract String getJdbcUrl();

    protected abstract String getDriverName();

    protected abstract String getCreateBlockNumberBlocksTableIndexSql();

    protected abstract String getCreateBlockNumberTransactionsTableIndexSql();

    protected abstract String getCreateTransactionHashTransactionsTableIndexSql();

    protected abstract String getCreateBlocksTableSql();

    protected abstract String getCreateTransactionsTableSql();

    protected void createIndexesIfNecessary(Connection connection) throws SQLException {
        connection.createStatement().execute(getCreateBlockNumberBlocksTableIndexSql());

        connection.createStatement().execute(getCreateBlockNumberTransactionsTableIndexSql());

        connection.createStatement().execute(getCreateTransactionHashTransactionsTableIndexSql());
    }

    private void createTablesIfNecessary(Connection connection) throws SQLException {
        connection.createStatement().execute(getCreateBlocksTableSql());
        connection.createStatement().execute(getCreateTransactionsTableSql());
    }

    protected PreparedStatement prepareStatement(String sql) throws SQLException, ClassNotFoundException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public int getBlockCount() throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = prepareStatement(getGetBlockCountSql());

        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.first();
        int blockCount = resultSet.getInt(1);
        resultSet.close();

        return blockCount;
    }

    protected abstract String getGetBlockCountSql();

    @Override
    public int getLastBlockNumber() throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = prepareStatement(getGetLastBlockNumberSql());

        ResultSet resultSet = preparedStatement.executeQuery();

        resultSet.first();
        int lastBlockNumber = resultSet.getInt(1);
        resultSet.close();

        return lastBlockNumber;
    }

    protected abstract String getGetLastBlockNumberSql();

    @Override
    public Block getBlock(long blockNumber) throws SQLException, ClassNotFoundException, IOException {
        PreparedStatement preparedStatement = prepareStatement(getGetBlockSql());
        preparedStatement.setLong(1, blockNumber);

        ResultSet resultSet = preparedStatement.executeQuery();

        try {
            if (!resultSet.first()) {
                return null;
            }

            return (Block) blockFactory.createBlock(new ByteArrayInputStream(resultSet.getBytes(1)));
        } finally {
            resultSet.close();
        }
    }

    protected abstract String getGetBlockSql();

    @Override
    public Long getBlockOffset(long blockNumber) throws SQLException, ClassNotFoundException, IOException {
        PreparedStatement preparedStatement = prepareStatement(getGetBlockOffsetSql());
        preparedStatement.setLong(1, blockNumber);

        ResultSet resultSet = preparedStatement.executeQuery();

        try {
            if (!resultSet.first()) {
                return null;
            }

            return resultSet.getLong(1);
        } finally {
            resultSet.close();
        }
    }

    protected abstract String getGetBlockOffsetSql();

    @Override
    public void storeBlock(long blockNumber, Block block) throws SQLException, ClassNotFoundException, IOException {
        // TODO - Do this in a transaction
        innerStoreBlock(blockNumber, block);
        innerStoreTransactions(blockNumber, block);
        connection.commit();
    }

    @Override
    public Transaction getTransaction(String transactionHash) throws SQLException, ClassNotFoundException, IOException {
        PreparedStatement preparedStatement = prepareStatement(getGetTransactionSql());
        preparedStatement.setString(1, transactionHash);

        ResultSet resultSet = preparedStatement.executeQuery();

        try {
            if (!resultSet.first()) {
                return null;
            }

            int blockNumber = resultSet.getInt(1);
            int transactionNumber = resultSet.getInt(2);

            Block block = getBlock(blockNumber);

            Transaction transaction = block.getTransactions().get(transactionNumber);

            return transaction;
        } finally {
            resultSet.close();
        }
    }

    protected abstract String getGetTransactionSql();

    private void innerStoreTransactions(final long blockNumber, Block block) throws SQLException, ClassNotFoundException {
        List<Future<?>> jobs = new ArrayList<Future<?>>();

        for (final Transaction transaction : block.getTransactions()) {
            jobs.add(executorService.submit(new Runnable() {
                @Override
                public void run() {
                    PreparedStatement preparedStatement = null;
                    try {
                        preparedStatement = prepareStatement(getStoreTransactionSql());
                        preparedStatement.setString(1, ByteArrayHelper.toHex(transaction.getHash()));
                        preparedStatement.setLong(2, blockNumber);
                        preparedStatement.setInt(3, transaction.getTransactionNumber());

                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.exit(1);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        System.exit(2);
                    }
                }
            }));
        }

        for (Future<?> job : jobs) {
            try {
                job.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(3);
            } catch (ExecutionException e) {
                e.printStackTrace();
                System.exit(4);
            }
        }
    }

    protected abstract String getStoreTransactionSql();

    private void innerStoreBlock(long blockNumber, Block block) throws SQLException, ClassNotFoundException, IOException {
        // What block are we inserting?
        if (blockNumber == 0) {
            // This is the first block, the offset is zero
            currentOffset = 0;
        } else if (lastBlockInserted != (blockNumber - 1)) {
            // This is not the next block we thought we'd be inserting.  We need to re-calculate the offset.
            long lastOffset = getBlockOffset(blockNumber - 1);
            currentOffset = lastOffset + getBlock(blockNumber - 1).dump().length;
        }

        PreparedStatement preparedStatement = prepareStatement(getStoreBlockSql());
        preparedStatement.setLong(1, blockNumber);
        preparedStatement.setBytes(2, block.dump());
        preparedStatement.setLong(3, currentOffset);

        preparedStatement.executeUpdate();

        lastBlockInserted = blockNumber;
        currentOffset += block.dump().length;
    }

    protected abstract String getStoreBlockSql();
}

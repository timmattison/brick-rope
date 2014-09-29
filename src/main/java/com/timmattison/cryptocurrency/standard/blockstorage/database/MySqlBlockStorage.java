package com.timmattison.cryptocurrency.standard.blockstorage.database;

import com.google.inject.name.Named;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.modules.BitcoinModule;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

/**
 * Created by timmattison on 4/15/14.
 */
public class MySqlBlockStorage extends AbstractDatabaseBlockStorage {
    private static final String mysqlDriver = "com.mysql.jdbc.Driver";
    private static final String mysqlJdbcPrefix = "jdbc:mysql://localhost/";
    private static final String blocksTable = "BLOCKS";
    private static final String transactionsTable = "TRANSACTIONS";
    private static final String transactionHashField = "transactionHash";
    private static final String transactionNumberField = "transactionNumber";
    private static final String blockNumberField = "blockNumber";
    private static final String blockField = "block";
    private static final String blockOffsetField = "blockOffset";
    private static final String createBlocksTableSql = "CREATE TABLE IF NOT EXISTS " + blocksTable + " (" + blockNumberField + " BIGINT not null, " + blockField + " LONGBLOB not null, " + blockOffsetField + " BIGINT not null);";
    private static final String createTransactionsTableSql = "CREATE TABLE IF NOT EXISTS " + transactionsTable + " (" + transactionHashField + " VARCHAR(255) not null, " + blockNumberField + " BIGINT not null, " + transactionNumberField + " BIGINT not null);";
    private static final String createBlockNumberBlocksTableIndexSql = "CREATE INDEX blocknumber_" + blocksTable + "_index on " + blocksTable + "(blocknumber)";
    private static final String createBlockNumberTransactionsTableIndexSql = "CREATE INDEX blocknumber_" + transactionsTable + "_index on " + transactionsTable + "(blocknumber)";
    private static final String createTransactionHashTransactionsTableIndexSql = "CREATE INDEX transactionhash_" + transactionsTable + "_index on " + transactionsTable + "(transactionhash)";
    private static final String getBlockCountSql = "SELECT COUNT(" + blockField + ") FROM " + blocksTable;
    private static final String getLastBlockNumberSql = "SELECT MAX(" + blockNumberField + ") FROM " + blocksTable;
    private static final String getBlockSql = "SELECT " + blockField + " FROM " + blocksTable + " WHERE " + blockNumberField + " = ?";
    private static final String getBlockOffsetSql = "SELECT " + blockOffsetField + " FROM " + blocksTable + " WHERE " + blockNumberField + " = ?";
    private static final String getTransactionSql = "SELECT " + blockNumberField + ", " + transactionNumberField + " FROM " + transactionsTable + " WHERE " + transactionHashField + " = ?";
    private static final String storeBlockSql = "INSERT INTO " + blocksTable + " (" + blockNumberField + ", " + blockField + ", " + blockOffsetField + ") VALUES (?, ?, ?)";
    private static final String storeTransactionSql = "INSERT INTO " + transactionsTable + " (" + transactionHashField + ", " + blockNumberField + ", " + transactionNumberField + ") VALUES (?, ?, ?)";
    private final String databaseName;

    @Inject
    public MySqlBlockStorage(ExecutorService executorService, BlockFactory blockFactory, @Named(BitcoinModule.DATABASE_NAME_NAME) String databaseName) {
        super(executorService, blockFactory);
        this.databaseName = databaseName;
    }

    @Override
    protected String getJdbcUrl() {
        return mysqlJdbcPrefix + databaseName + "?user=" + System.getProperty("user.name");
    }

    @Override
    protected String getDriverName() {
        return mysqlDriver;
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

    /**
     * Overriden version of createIndexesIfNecessary since MySQL needs some special transaction handling when there
     * are exceptions (eg. the indexes already exist)
     *
     * @param connection
     * @throws java.sql.SQLException
     */
    @Override
    protected void createIndexesIfNecessary(Connection connection) throws SQLException {
        try {
            connection.createStatement().execute(createBlockNumberBlocksTableIndexSql);
        } catch (MySQLSyntaxErrorException e) {
            throwIfNotAlreadyExistsException(e);
        }

        doSafeCommit(connection);

        try {
            connection.createStatement().execute(createBlockNumberTransactionsTableIndexSql);
        } catch (MySQLSyntaxErrorException e) {
            throwIfNotAlreadyExistsException(e);
        }

        doSafeCommit(connection);

        try {
            connection.createStatement().execute(createTransactionHashTransactionsTableIndexSql);
        } catch (MySQLSyntaxErrorException e) {
            throwIfNotAlreadyExistsException(e);
        }

        doSafeCommit(connection);
    }

    private void throwIfNotAlreadyExistsException(MySQLSyntaxErrorException e) throws MySQLSyntaxErrorException {
        if (!alreadyExistsException(e)) {
            e.printStackTrace();
            throw (e);
        }
    }

    private boolean alreadyExistsException(MySQLSyntaxErrorException mysqlException) {
        if (mysqlException.getMessage().contains("Duplicate key name")) {
            return true;
        }

        return false;
    }

    /**
     * Does a commit.  Just to be safe.  Does NOT hide exceptions.
     *
     * @param connection
     * @throws java.sql.SQLException
     */
    private void doSafeCommit(Connection connection) throws SQLException {
        /**
         * This COMMIT is necessary because if the previous statement failed MySQL will ignore any new commands
         * until a commit or rollback;
         */
        connection.createStatement().execute("COMMIT");
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

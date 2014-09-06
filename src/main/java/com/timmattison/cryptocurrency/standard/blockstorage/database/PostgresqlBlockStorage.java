package com.timmattison.cryptocurrency.standard.blockstorage.database;

import com.google.inject.name.Named;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.modules.BitcoinModule;
import org.postgresql.util.PSQLException;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by timmattison on 4/15/14.
 */
public class PostgresqlBlockStorage extends AbstractDatabaseBlockStorage {
    private static final String postgresqlDriver = "org.postgresql.Driver";
    private static final String postgresqlJdbcPrefix = "jdbc:postgresql://:5432/";
    private static final String blocksTable = "BLOCKS";
    private static final String transactionsTable = "TRANSACTIONS";
    private static final String transactionHashField = "transactionHash";
    private static final String transactionNumberField = "transactionNumber";
    private static final String blockNumberField = "blockNumber";
    private static final String blockField = "block";
    private static final String blockOffsetField = "blockOffset";
    private static final String createBlocksTableSql = "CREATE TABLE IF NOT EXISTS " + blocksTable + " (" + blockNumberField + " BIGINT not null, " + blockField + " BYTEA not null, " + blockOffsetField + " BIGINT not null);";
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
    public PostgresqlBlockStorage(BlockFactory blockFactory, @Named(BitcoinModule.DATABASE_NAME_NAME) String databaseName) {
        super(blockFactory);
        this.databaseName = databaseName;
    }

    @Override
    protected String getJdbcUrl() {
        return postgresqlJdbcPrefix + databaseName;
    }

    @Override
    protected String getDriverName() {
        return postgresqlDriver;
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
     * Overriden version of createIndexesIfNecessary since PostgreSQL needs some special transaction handling when there
     * are exceptions (eg. the indexes already exist)
     *
     * @param connection
     * @throws SQLException
     */
    @Override
    protected void createIndexesIfNecessary(Connection connection) throws SQLException {
        try {
            connection.createStatement().execute(createBlockNumberBlocksTableIndexSql);
        } catch (PSQLException e) {
            throwIfNotAlreadyExistsException(e);
        }

        doSafeCommit(connection);

        try {
            connection.createStatement().execute(createBlockNumberTransactionsTableIndexSql);
        } catch (PSQLException e) {
            throwIfNotAlreadyExistsException(e);
        }

        doSafeCommit(connection);

        try {
            connection.createStatement().execute(createTransactionHashTransactionsTableIndexSql);
        } catch (PSQLException e) {
            throwIfNotAlreadyExistsException(e);
        }

        doSafeCommit(connection);
    }

    private void throwIfNotAlreadyExistsException(PSQLException e) throws PSQLException {
        if (!alreadyExistsException(e)) {
            e.printStackTrace();
            throw (e);
        }
    }

    private boolean alreadyExistsException(PSQLException psqlException) {
        if (psqlException.getMessage().contains("already exists")) {
            return true;
        }

        return false;
    }

    /**
     * Does a commit.  Just to be safe.  Does NOT hide exceptions.
     *
     * @param connection
     * @throws SQLException
     */
    private void doSafeCommit(Connection connection) throws SQLException {
        /**
         * This COMMIT is necessary because if the previous statement failed PostgreSQL will ignore any new commands
         * until a commit or rollback;
         */
        connection.createStatement().execute("COMMIT");
    }

    /**
     * An overridden version of prepareStatement.  This is because PostgreSQL needs special flags set.
     *
     * @param sql
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    protected PreparedStatement prepareStatement(String sql) throws SQLException, ClassNotFoundException {
        return getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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

package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.interfaces.Block;

import java.sql.*;

/**
 * Created by timmattison on 4/15/14.
 */
public class H2BlockStorage implements BlockStorage {
    private Connection connection;

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        if(connection == null) {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/test");

            createTableIfNecessary(connection);
        }

        return connection;
    }

    private void createTableIfNecessary(Connection connection) throws SQLException {
        String createBlocksTableSql = "CREATE TABLE IF NOT EXISTS BLOCKS(blockNumber int not null, block OTHER);";
        connection.createStatement().execute(createBlocksTableSql);

        String createTransactionsTableSql = "CREATE TABLE IF NOT EXISTS BLOCKS(hash varchar(256) not null, transaction OTHER);";
        connection.createStatement().execute(createTransactionsTableSql);
    }

    private PreparedStatement prepareStatement(String sql) throws SQLException, ClassNotFoundException {
        return getConnection().prepareStatement(sql);
    }

    @Override
    public Block getBlock(int blockNumber) throws SQLException, ClassNotFoundException {
        String getBlockSql = "SELECT block FROM BLOCKS WHERE blockNumber = ?";

        PreparedStatement preparedStatement = prepareStatement(getBlockSql);
        preparedStatement.setInt(1, blockNumber);

        ResultSet resultSet = preparedStatement.executeQuery();

        if(!resultSet.first()) {
            return null;
        }

        return (Block) resultSet.getObject(1);
    }

    @Override
    public void storeBlock(int blockNumber, Block block) throws SQLException, ClassNotFoundException {
        String storeBlockSql = "INSERT INTO BLOCKS(blockNumber, block) VALUES (?, ?)";

        PreparedStatement preparedStatement = prepareStatement(storeBlockSql);
        preparedStatement.setInt(1, blockNumber);
        preparedStatement.setObject(2, block);

        preparedStatement.executeUpdate();
    }
}

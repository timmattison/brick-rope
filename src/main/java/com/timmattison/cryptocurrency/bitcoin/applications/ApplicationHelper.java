package com.timmattison.cryptocurrency.bitcoin.applications;

import org.apache.commons.cli.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/8/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApplicationHelper {
    public static final String DATABASE = "database";
    private static final String DATABASE_FILE_NAME = "block database file name";
    public static final String BLOCKCHAIN = "blockchain";
    private static final String BLOCKCHAIN_FILE_NAME = "blockchain file name";
    public static final String POSTGRESQL = "postgresql";
    private static final String POSTGRESQL_OPTION = "use PostgreSQL";
    public static final String MYSQL = "mysql";
    private static final String MYSQL_OPTION = "use MySQL";

    public static void logFine() {
        Handler[] handlers = Logger.getLogger("").getHandlers();

        for (Handler handler : handlers) {
            handler.setLevel(Level.FINEST);
        }
    }

    public static Map<String, String> processCommandLineOptions(String[] args, String applicationName) throws ParseException {
        // Create the Options object
        Options options = new Options();

        // Add the database adn blockchain options
        options.addOption(DATABASE, true, DATABASE_FILE_NAME);
        options.addOption(BLOCKCHAIN, true, BLOCKCHAIN_FILE_NAME);
        options.addOption(POSTGRESQL, false, POSTGRESQL_OPTION);
        options.addOption(MYSQL, false, MYSQL_OPTION);

        CommandLineParser commandLineParser = new BasicParser();
        CommandLine commandLine = commandLineParser.parse(options, args);

        Map<String, String> output = new HashMap<String, String>();

        String databaseFile = commandLine.getOptionValue(DATABASE);
        String blockchainFile = commandLine.getOptionValue(BLOCKCHAIN);

        boolean postgresql = commandLine.hasOption(POSTGRESQL);
        boolean mysql = commandLine.hasOption(MYSQL);

        if (databaseFile == null) {
            // Automatically generate the usage info with Apache CLI
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(applicationName, options);
            System.exit(1);
        }

        if (databaseFile != null) {
            output.put(DATABASE, databaseFile);
        }

        if (blockchainFile != null) {
            output.put(BLOCKCHAIN, blockchainFile);
        }

        if(postgresql) {
            output.put(POSTGRESQL, String.valueOf(postgresql));
        }

        if(mysql) {
            output.put(MYSQL, String.valueOf(mysql));
        }

        return output;
    }
}

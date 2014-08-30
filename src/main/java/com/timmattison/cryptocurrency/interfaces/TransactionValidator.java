package com.timmattison.cryptocurrency.interfaces;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionValidator {
    boolean isValid(Transaction transaction);
}

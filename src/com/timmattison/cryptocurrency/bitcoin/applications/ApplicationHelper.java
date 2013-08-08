package com.timmattison.cryptocurrency.bitcoin.applications;

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
    public static void logFine() {
        Handler[] handlers = Logger.getLogger("").getHandlers();

        for(Handler handler : handlers) {
            handler.setLevel(Level.FINE);
        }
    }
}

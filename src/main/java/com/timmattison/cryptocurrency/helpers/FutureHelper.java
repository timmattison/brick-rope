package com.timmattison.cryptocurrency.helpers;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by timmattison on 8/31/14.
 */
public class FutureHelper {
    public static boolean allTrue(List<Future<Boolean>> results) {
        boolean returnValue = true;

        for (Future<Boolean> future : results) {
            try {
                if (future.get().equals(Boolean.FALSE)) {
                    returnValue = false;
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                returnValue = false;
                break;
            } catch (ExecutionException e) {
                e.printStackTrace();
                returnValue = false;
                break;
            }
        }

        return returnValue;
    }
}

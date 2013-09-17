package com.timmattison.cryptocurrency.factories;

import com.timmattison.cryptocurrency.interfaces.Output;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 7:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface OutputFactory {
    Output createOutput(int transactionVersionNumber, int outputNumber);
}

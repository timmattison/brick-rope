package com.timmattison.cryptocurrency.factories;

import com.timmattison.cryptocurrency.interfaces.Input;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 7:04 AM
 * To change this template use File | Settings | File Templates.
 */
public interface InputFactory {
    Input createInput(int inputNumber);
}

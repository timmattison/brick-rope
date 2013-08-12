package com.timmattison.cryptocurrency.interfaces;

import com.timmattison.cryptocurrency.standard.Script;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:34 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Output extends Buildable {
    Script getScript();
}

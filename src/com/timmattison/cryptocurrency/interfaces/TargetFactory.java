package com.timmattison.cryptocurrency.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/9/13
 * Time: 6:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TargetFactory<T> {
    Target create(T value);
}

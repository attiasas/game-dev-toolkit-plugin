package com.attiasas.gamedevtoolkitplugin.utils;

/**
 * @Author: Assaf, On 2/28/2023
 * @Description:
 **/
public class Pair<V,U> {
    public V first;
    public U second;

    public Pair(V first, U second) {
        this.first = first;
        this.second = second;
    }

    public Pair() { this(null,null); }

    @Override
    public String toString() {
        return "{" +
                first +
                "," + second +
                '}';
    }
}

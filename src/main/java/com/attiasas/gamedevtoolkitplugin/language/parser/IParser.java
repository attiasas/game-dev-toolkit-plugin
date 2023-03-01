package com.attiasas.gamedevtoolkitplugin.language.parser;

/**
 * @Author: Assaf, On 3/1/2023
 * @Description:
 **/
public interface IParser<I,O> {

    public O parse(I input);
}

package com.attiasas.gamedevtoolkitplugin.language.parser;

/**
 * @Author: Assaf, On 3/2/2023
 * @Description:
 **/
public class ParseIdentifier {
    private String allowedRegex;

    public ParseIdentifier(String allowedRegex) {
        this.allowedRegex = allowedRegex;
    }

    public ParseIdentifier() {
        this("^[A-Za-z0-9_-\\.\\/\\\\]*$");
    }
}

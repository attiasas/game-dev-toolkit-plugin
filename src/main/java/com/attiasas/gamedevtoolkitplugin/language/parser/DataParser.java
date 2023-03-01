package com.attiasas.gamedevtoolkitplugin.language.parser;

import com.attiasas.gamedevtoolkitplugin.language.DataNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @Author: Assaf, On 3/1/2023
 * @Description:
 **/
public class DataParser implements IParser<String, DataNode> {

    private Tokenizer tokenizer;
    private IParser<List<String>,DataNode> strategy;

    public DataParser(@NotNull Tokenizer tokenizer, @NotNull IParser<List<String>,DataNode> strategy) {
        this.tokenizer = tokenizer;
        this.strategy = strategy;
    }

    public DataNode parse(Path path) throws IOException {
        tokenizer.tokenize(path);
        return strategy.parse(tokenizer.tokens);
    }

    @Override
    public DataNode parse(String input) {
        tokenizer.tokenize(input);
        return strategy.parse(tokenizer.tokens);
    }

    public class DataParseException extends Exception {

    }
}

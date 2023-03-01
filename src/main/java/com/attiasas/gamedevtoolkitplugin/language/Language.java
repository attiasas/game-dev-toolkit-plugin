package com.attiasas.gamedevtoolkitplugin.language;

import com.attiasas.gamedevtoolkitplugin.language.parser.DataParseRule;
import com.attiasas.gamedevtoolkitplugin.language.parser.IParser;
import com.attiasas.gamedevtoolkitplugin.language.parser.Tokenizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Assaf, On 3/1/2023
 * @Description:
 **/
public class Language implements IParser<String,DataNode> {

    private List<DataParseRule> rules = new ArrayList<>();

    private Set<String> separators = new HashSet<>();

    private DataNode sharedCurrent;

    public Language(List<DataParseRule> rules) {
        this.rules = rules;
    }

    public Language(DataParseRule... rules) {
        this(List.of(rules));
    }

    @Override
    public DataNode parse(String input) {
        Tokenizer tokenizer;
        tokenizer = this.createTokenizer();
        List<String> tokens = tokenizer.tokenize(input);
        int currentIndex = 0, lastIndex = -1;
        DataNode result = new DataNode();
        this.sharedCurrent = result;
        while (currentIndex < tokens.size() && currentIndex != lastIndex) {
            int advance = applyRules();
            lastIndex = currentIndex;
            currentIndex += advance;
        }

        return result;
    }

    private int applyRules() {
        return 1;
    }

    private Tokenizer createTokenizer() {
        return new Tokenizer(this.separators);
    }
}

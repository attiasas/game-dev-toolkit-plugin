package com.attiasas.gamedevtoolkitplugin.utils;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Assaf, On 3/3/2023
 * @Description:
 **/
public class RegexBuilder {

    public final static String ALPHABET = "[a-zA-Z]+";
    public final static String INTEGER = "-?\\d+";
    public final static String NUMBER = "-?\\d+(?:\\.\\d+)?";
    public final static String ALPHANUMERIC = "[a-zA-Z0-9]+";
    public final static String WHITESPACE = "\\s+";
    public final static String WHITESPACE_EXCEPT_NEWLINE = "[^\\S\\n]+";

    public final static String FILES = "^(((\\\\/([a-zA-Z0-9_\\\\-\\\\.]+))+|(\\\\/))|(((\\\\w{1}):\\\\/([a-zA-Z0-9_\\\\-\\\\.]+))+|((\\\\\\\\\\\\\\\\[a-zA-Z0-9_\\\\-\\\\.]+(\\\\\\\\[a-zA-Z0-9_\\\\-\\\\.]+)+)))(\\\\/([a-zA-Z0-9_\\-\\.\\s])+)*$";
    public final static String URL = "^(?:https?|ftp):\\/\\/(?:\\S+(?::\\S*)?@)?(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(?::\\d{2,5})?(?:\\/\\S*)?";

    private static final Set<Character> SPECIAL_CHARACTERS = Set.of('\\', '.', '+', '*', '?', '|', '(', ')', '[', ']', '{', '}', '^', '$');

    private StringBuilder builder;

    public RegexBuilder() {
        builder = new StringBuilder();
    }

    public static String matchBetweenWithEscaping(String symbol) {
        return Pattern.quote(symbol) + "([^" + Pattern.quote(symbol) + "\\\\]*(\\\\.[^" + Pattern.quote(symbol) + "\\\\]*)*)" + Pattern.quote(symbol);
//        return symbol + "([^" + symbol + "\\\\]*(\\\\.[^" + symbol + "\\\\]*)*)" + symbol;
    }

    public static void main(String[] args) {
        String input = "Visit us at https://www.example.com";
        String regex = new RegexBuilder()
                .literal("https://")
                .namedCaptureGroup("domain", "[^/]+")
                .literal("/")
                .build();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            System.out.println("Match found!");
            System.out.println("Domain name: " + matcher.group("domain"));
        }
    }

    public RegexBuilder literal(String text) {
        builder.append(Pattern.quote(text));
        return this;
    }

    public RegexBuilder optional(String text) {
        builder.append("(?:").append(Pattern.quote(text)).append(")?");
        return this;
    }

    public RegexBuilder or(String... options) {
        builder.append("(?:");
        for (int i = 0; i < options.length; i++) {
            builder.append(Pattern.quote(options[i]));
            if (i < options.length - 1) {
                builder.append("|");
            }
        }
        builder.append(")");
        return this;
    }

    public RegexBuilder captureGroup(String regex) {
        builder.append("(").append(regex).append(")");
        return this;
    }

    public RegexBuilder namedCaptureGroup(String groupName, String regex) {
        builder.append("(?<").append(groupName).append(">").append(regex).append(")");
        return this;
    }

    public RegexBuilder zeroOrMore(String regex) {
        builder.append("(?:").append(regex).append(")*");
        return this;
    }

    public RegexBuilder oneOrMore(String regex) {
        builder.append("(?:").append(regex).append(")+");
        return this;
    }

    public RegexBuilder zeroOrOne(String regex) {
        builder.append("(?:").append(regex).append(")?");
        return this;
    }

    public RegexBuilder betweenChars(char startChar, char endChar) {
        builder.append("(?<=").append(Pattern.quote(Character.toString(startChar))).append(")")
                .append("(.*?)")
                .append("(?=").append(Pattern.quote(Character.toString(endChar))).append(")");
        return this;
    }

    public void setExactMatch(String string) {
        builder.append("\\Q").append(string).append("\\E");
    }

    public void zeroOrMore() {
        builder.append("*");
    }

    public void oneOrMore() {
        builder.append("+");
    }

    public void zeroOrOne() {
        builder.append("?");
    }

    public void anyCharacter() {
        builder.append(".");
    }

    public void anyOf(String characters) {
        builder.append("[").append(characters).append("]");
    }

    public void anythingBut(String characters) {
        builder.append("[^").append(characters).append("]");
    }

    public void unicodeCharacter(String codepoint) {
        builder.append("\\u").append(codepoint);
    }

    public void namedCharacter(String name) {
        builder.append("\\N{").append(name).append("}");
    }

    public void positiveLookahead(String lookaheadRegex) {
        builder.append("(?=").append(lookaheadRegex).append(")");
    }

    // adds a negative lookahead assertion that matches a group of characters only if it is not followed by a specific pattern
    public void negativeLookahead(String lookaheadRegex) {
        builder.append("(?!").append(lookaheadRegex).append(")");
    }

    // adds a positive lookbehind assertion that matches a group of characters only if it is preceded by a specific pattern
    public void positiveLookbehind(String lookbehindRegex) {
        builder.append("(?<=").append(lookbehindRegex).append(")");
    }

    // adds a negative lookbehind assertion that matches a group of characters only if it is not preceded by a specific pattern
    public void negativeLookbehind(String lookbehindRegex) {
        builder.append("(?<!").append(lookbehindRegex).append(")");
    }

    public void startOfLine() {
        builder.append("^");
    }

    public void endOfLine() {
        builder.append("$");
    }

    public void whitespace() {
        builder.append("\\s");
    }

    public void nonWhitespace() {
        builder.append("\\S");
    }

    public void digit() {
        builder.append("\\d");
    }

    public void nonDigit() {
        builder.append("\\D");
    }

    public void word() {
        builder.append("\\w");
    }

    public void nonWord() {
        builder.append("\\W");
    }

    // matches the position between a word character and a non-word character, or between a word character and the beginning or end of a line
    public void wordBoundary() {
        builder.append("\\b");
    }

    public void nonWordBoundary() {
        builder.append("\\B");
    }

    public void backspace() {
        builder.append("\\b");
    }

    public void tab() {
        builder.append("\\t");
    }

    public void linefeed() {
        builder.append("\\n");
    }

    public void carriageReturn() {
        builder.append("\\r");
    }

    public void formfeed() {
        builder.append("\\f");
    }

    public void hexadecimalCharacter(String character) {
        builder.append("\\x").append(character);
    }

    public String build() {
        return builder.toString();
    }
}

package com.attiasas.gamedevtoolkitplugin.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Assaf, On 2/27/2023
 * @Description:
 **/
public class Tokenizer implements Iterable<String> {

    public static class Token {
        public final String token;
        public final int position;
        public final int line;

        public Token(String token, int line, int position) {
            this.token = token;
            this.line = line;
            this.position = position;
        }

        @Override
        public String toString() {
            return "Token{" +
                    "token='" + token + '\'' +
                    ", position=" + position +
                    ", line=" + line +
                    '}';
        }
    }



    public List<Token> tokens = new ArrayList<>();
    public final String[] delimiters;

    public Tokenizer(String... delimiters) {
        this.delimiters = delimiters != null && delimiters.length > 0 ? delimiters : new String[]{"\\s+"};
    }

    public Tokenizer(Collection<String>... collections) {
        int count = (int) Arrays.stream(collections).count();
        delimiters = new String[count];
        int i = 0;
        for (Collection<String> collection : collections) {
            for (String delimiter : collection) {
                delimiters[i] = delimiter;
                i++;
            }
        }
    }

    public List<Token> tokenize(String text) {
        this.tokens = Tokenizer.tokenize(text,this.delimiters);
        return new ArrayList<>(this.tokens);
    }

    public List<Token> tokenize(Path path) throws IOException {
        this.tokens = Tokenizer.tokenize(new String(Files.readAllBytes(path)),this.delimiters);
        return new ArrayList<>(this.tokens);
    }

    public static List<Token> tokenize(String input, String... delimiters) {
        List<Token> tokens = new ArrayList<>();
        String regex = String.join("|",delimiters);
        Matcher matcher = Pattern.compile(regex).matcher(input);

        int line = 1;
        int pos = 1;
        int start = 0;
        while (matcher.find()) {
            if (matcher.start() != start) {
                // Create a token for the text between the previous match and the current match
                Token textToken = new Token(input.substring(start, matcher.start()), line, pos);
                tokens.add(textToken);

                // Update position metadata
                pos += textToken.token.length();
            }

            // Create a token for the current match
            Token matchToken = new Token(matcher.group(), line, pos);
            tokens.add(matchToken);

            // Update position metadata
            pos += matchToken.token.length();

            // Update line metadata if the current match contains a newline character
            int newlineIndex = matcher.group().indexOf("\n");
            if (newlineIndex >= 0) {
                line += matcher.group().substring(0, newlineIndex).length() + 1;
                pos = 1;
            }

            start = matcher.end();
        }

        if (start != input.length()) {
            // Create a token for the remaining text after the last match
            Token textToken = new Token(input.substring(start), line, pos);
            tokens.add(textToken);
        }

        return tokens;
    }

    @Override
    public Iterator iterator() {
        return this.tokens.iterator();
    }

    @Override
    public void forEach(Consumer action) {
        this.tokens.forEach(action);
    }

    @Override
    public Spliterator spliterator() {
        return this.tokens.spliterator();
    }

    public static void main(String[] args) {
        Path test = Paths.get("build.gradle.kts");
        String[] regex = new String[]{"[\\s]+","\\{","\\}","\\(","\\)","\n"};
        try {
            Tokenizer tokenizer = new Tokenizer(regex);
            tokenizer.tokenize(test);
            System.out.println("Text:\n" + new String(Files.readAllBytes(test)));
            System.out.println("");
            System.out.println("Tokens: " + tokenizer.tokens);
            System.out.println("");
            for (String token : tokenizer) {
                System.out.println("" + token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

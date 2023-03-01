package com.attiasas.gamedevtoolkitplugin.language.parser;

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

    public List<String> tokens = new ArrayList<>();
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

    public List<String> tokenize(String text) {
        this.tokens = Tokenizer.tokenize(text,this.delimiters);
        return new ArrayList<>(this.tokens);
    }

    public List<String> tokenize(Path path) throws IOException {
        this.tokens = Tokenizer.tokenize(new String(Files.readAllBytes(path)),this.delimiters);
        return new ArrayList<>(this.tokens);
    }

    public static List<String> tokenize(String line, String... delimiters) {
        List<String> tokens = new ArrayList<>();
        String regex = String.join("|",delimiters);
        Matcher matcher = Pattern.compile(regex).matcher(line);
        int pos = 0;
        while (matcher.find()) {
            if (pos < matcher.start()) {
                tokens.add(line.substring(pos, matcher.start()));
            }
            tokens.add(matcher.group());
            pos = matcher.end();
        }
        if (pos < line.length()) {
            tokens.add(line.substring(pos));
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
//        String.join("| ", Arrays.stream(words)
//                .map(String::trim)
//                .toArray(String[]::new));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

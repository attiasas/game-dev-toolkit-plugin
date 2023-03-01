package com.attiasas.gamedevtoolkitplugin.language;

import com.attiasas.gamedevtoolkitplugin.language.parser.DataParseRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Assaf, On 3/1/2023
 * @Description:
 **/
public class GDFormat extends Language {

    public GDFormat() {
        super(generateRules());
    }

    public static List<DataParseRule> generateRules() {
        List<DataParseRule> gdRules = new ArrayList<>();

        return gdRules;
    }

    public static void main(String[] args) {
        GDFormat format = new GDFormat();
        System.out.println("Rules:\n" + generateRules());
        System.out.println("");
        try {
            String testFile = new String(Files.readAllBytes(Paths.get("build.gradle.kts")));
            DataNode data = format.parse(testFile);
            System.out.println("Data:\n" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.attiasas.gamedevtoolkitplugin.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @Author: Assaf, On 2/26/2023
 * @Description:
 **/
public class CommandRunner {

    public static String executeCommand(String command, String... args) throws IOException {
        return executeCommand(null, null, command, args);
    }

    public static String executeCommand(File workingDirectory, String command, String... args) throws IOException {
        return executeCommand(null, workingDirectory, command, args);
    }

    public static String executeCommand(Map<String, String> environmentVariables, String command, String... args) throws IOException {
        return executeCommand(environmentVariables, null, command, args);
    }

    public static String executeCommand(Map<String, String> environmentVariables, File workingDirectory, String command, String... args) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder().command(constructCommand(command,args));
        if (workingDirectory != null) {
            processBuilder = processBuilder.directory(workingDirectory);
        }
        if (environmentVariables != null) {
            Map<String, String> environment = processBuilder.environment();
            environment.putAll(environmentVariables);
        }
        Process process = processBuilder.start();
        return runProcess(process);
    }

    private static String constructCommand(String command, String... args) {
        StringBuilder cmd = new StringBuilder(command);
        for (String arg : args) {
            cmd.append(" ");
            cmd.append(arg);
        }
        return cmd.toString();
    }

    @NotNull
    private static String runProcess(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
                output.append(System.lineSeparator());
            }
        }
        return output.toString();
    }
}

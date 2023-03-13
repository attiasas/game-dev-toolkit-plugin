package com.attiasas.gamedevtoolkitplugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class Utils {

    public static List<String> listFiles(String directory, String... filterRegex) throws IOException {
        List<String> files = new ArrayList<>();
        if (filterRegex == null || filterRegex.length == 0) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directory))) {
                for (Path path : stream) {
                    if (Files.isRegularFile(path)) {
                        files.add(path.toString());
                    }
                }
            }
            return files;
        }
        Set<String> filterSet = new HashSet<>(Arrays.asList(filterRegex));
        Files.walk(Paths.get(directory))
                .filter(Files::isRegularFile)
                .filter(p -> filterSet.stream().anyMatch(p.toString()::matches))
                .forEach(p -> files.add(p.toString()));
        return files;
    }

    public static void refreshPluginWindow(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("JFrog");
        if (toolWindow != null) {
            toolWindow.activate(null);
        }
    }

    public static Path getProjectBasePath(Project project) {
        return project.getBasePath() != null ? Paths.get(project.getBasePath()) : Paths.get(".");
    }
}

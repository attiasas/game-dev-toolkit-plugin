package com.attiasas.gamedevtoolkitplugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class Utils {

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

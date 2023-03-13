package com.attiasas.gamedevtoolkitplugin.utils;

import com.attiasas.gamedevtoolkitplugin.log.Logger;
import com.intellij.util.text.SemVer;

import java.io.File;
import java.io.IOException;

/**
 * @Author: Assaf, On 2/26/2023
 * @Description:
 **/
public class GradleUtils {

    public static SemVer runVersion(File workingDir) throws IOException {
        return SemVer.parseFromText(CommandRunner.executeCommand(workingDir,"gradle", "--version"));
    }

    public static boolean isGradleInstalled() {
        try {
            return runVersion(null) != null;
        } catch (IOException e) {
            Logger.getInstance().error("Gradle is not installed", e);
            return false;
        }
    }
}

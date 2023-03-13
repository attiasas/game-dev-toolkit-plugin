package com.attiasas.gamedevtoolkitplugin.projects;

import com.intellij.openapi.project.Project;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class LibGDXProject {

    public LibGDXProject(Project project) {

    }

    public static LibGDXProject decode(Project project) {
        return new LibGDXProject(project);
    }
}

package com.attiasas.gamedevtoolkitplugin.projects;

import com.intellij.openapi.project.Project;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class GradleProject {

    public GradleProject(Project project) {

    }

    public static GradleProject decode(Project project) {
        return new GradleProject(project);
    }
}

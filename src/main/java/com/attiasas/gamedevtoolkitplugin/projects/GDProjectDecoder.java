package com.attiasas.gamedevtoolkitplugin.projects;

import com.google.common.collect.Sets;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Set;

import static com.attiasas.gamedevtoolkitplugin.utils.Utils.getProjectBasePath;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class GDProjectDecoder {

    private final Project project;
    private GDProject gdProject;

    private GDProjectDecoder(@NotNull Project project) {
        this.project = project;
    }

    public static GDProjectDecoder getInstance(@NotNull Project project) {
        return project.getService(GDProjectDecoder.class);
    }

    public void decode() {
        // If intellij is still indexing the project
        if (DumbService.isDumb(project)) {
            return;
        }

    }

    public boolean isValidProject() {
        return gdProject != null;
    }

    public GDProject get() {
        return gdProject;
    }

    public Set<Path> getProjectPaths(Project project) {
        final Set<Path> paths = Sets.newHashSet();
        paths.add(getProjectBasePath(project));
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            VirtualFile modulePath = ProjectUtil.guessModuleDir(module);
            if (modulePath != null) {
                paths.add(modulePath.toNioPath());
            }
        }
        return paths;
    }
}

package com.attiasas.gamedevtoolkitplugin.ui;

import com.attiasas.gamedevtoolkitplugin.log.Logger;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class GDPluginWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DumbService.getInstance(project).runWhenSmart(() -> {
            Logger logger = Logger.getInstance();
            logger.info("Creating GD windows from factory");

            ContentManager contentManager = toolWindow.getContentManager();
            addContentTab(contentManager, new ProjectWindowTab(project));
        });
    }

    private void addContentTab(ContentManager contentManager, AbstractToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content localContent = contentFactory.createContent(toolWindow, toolWindow.name, false);
        contentManager.addContent(localContent);
    }
}

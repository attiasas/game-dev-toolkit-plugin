package com.attiasas.gamedevtoolkitplugin.ui;

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
public class GDToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        BaseToolWindow baseToolWindow = new BaseToolWindow(project);
        addContentTab(contentManager,baseToolWindow);
    }

    private void addContentTab(ContentManager contentManager, AbstractToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content localContent = contentFactory.createContent(toolWindow, toolWindow.name, false);
        contentManager.addContent(localContent);
    }
}

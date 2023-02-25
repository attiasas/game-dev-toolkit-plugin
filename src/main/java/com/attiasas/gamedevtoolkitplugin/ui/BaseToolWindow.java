package com.attiasas.gamedevtoolkitplugin.ui;

import com.attiasas.gamedevtoolkitplugin.utils.ComponentUtils;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class BaseToolWindow extends AbstractToolWindow {

    public BaseToolWindow(@NotNull Project project) {
        super("TabName",project);
    }

    @Override
    JPanel createActionToolbar() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
//        actionGroup.addAction(ActionManager.getInstance().getAction("JFrog.RefreshLocal"), Constraints.FIRST);
        return createToolbarPanel(actionGroup);
    }

    @Override
    JComponent createComponents() {
        JPanel helloWorldPanel = new JBPanel<>();
        helloWorldPanel.setLayout(new BoxLayout(helloWorldPanel, BoxLayout.PAGE_AXIS));

        ComponentUtils.addCenteredHyperlinkLabel(helloWorldPanel, new HyperlinkLabel("Hello world!"));

        return ComponentUtils.createCenteredComponentPanel(helloWorldPanel);
    }

    @Override
    void registerListeners() {

    }

    @Override
    void onChange() {

    }
}

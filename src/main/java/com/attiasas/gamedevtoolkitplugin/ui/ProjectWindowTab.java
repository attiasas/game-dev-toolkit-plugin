package com.attiasas.gamedevtoolkitplugin.ui;

import com.attiasas.gamedevtoolkitplugin.log.Logger;
import com.attiasas.gamedevtoolkitplugin.utils.ComponentUtils;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class ProjectWindowTab extends AbstractToolWindow {

    private static final String TAB_NAME = "Project";

    public ProjectWindowTab(@NotNull Project project) {
        super(TAB_NAME,project);
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
        // Create a button to refresh the tool window
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> Logger.getInstance().info("Refreshing!"));
        helloWorldPanel.add(refreshButton);

        return ComponentUtils.createCenteredComponentPanel(helloWorldPanel);
    }

    @Override
    void registerListeners() {

    }

    @Override
    void onChange() {

    }
}

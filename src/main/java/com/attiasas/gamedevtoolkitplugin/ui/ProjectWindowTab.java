package com.attiasas.gamedevtoolkitplugin.ui;

import com.attiasas.gamedevtoolkitplugin.log.Logger;
import com.attiasas.gamedevtoolkitplugin.utils.ComponentUtils;
import com.attiasas.gamedevtoolkitplugin.utils.GradleUtils;
import com.attiasas.gamedevtoolkitplugin.utils.Utils;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;

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

    private JPanel helloWorldPanel;
    private JButton refreshButton;
    private JButton resetButton;

    private void reset() {
        helloWorldPanel.removeAll();
        helloWorldPanel.add(refreshButton);
        helloWorldPanel.add(resetButton);
    }

    @Override
    JComponent createComponents() {
        helloWorldPanel = new JBPanel<>();
//        helloWorldPanel.setLayout(new BoxLayout(helloWorldPanel, BoxLayout.PAGE_AXIS));

        // Create a button to refresh the tool window
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            Logger.getInstance().info("Refreshing!");
            reset();
            helloWorldPanel.add(new HyperlinkLabel("Gradle installed [" + (GradleUtils.isGradleInstalled() ? "V" : "X") + "]"));
            helloWorldPanel.add(new HyperlinkLabel("build.gradle exists [" + (Files.exists(Utils.getProjectBasePath(project).resolve("build.gradle")) ? "V" : "X") + "]"));
        });
        helloWorldPanel.add(refreshButton);
        // Create a button to reset the tool window
        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            Logger.getInstance().info("Resetting!");
            reset();
            helloWorldPanel.add(new HyperlinkLabel("Hello world!"));
        });
        helloWorldPanel.add(resetButton);

        return ComponentUtils.createCenteredComponentPanel(helloWorldPanel);
    }

    @Override
    void registerListeners() {

    }

    @Override
    void onChange() {

    }
}

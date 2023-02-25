package com.attiasas.gamedevtoolkitplugin.ui;

import com.attiasas.gamedevtoolkitplugin.utils.Constants;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public abstract class AbstractToolWindow extends SimpleToolWindowPanel implements Disposable {

    final MessageBusConnection projectBusConnection;
    final MessageBusConnection appBusConnection;

    final Project project;
    final String name;

    public AbstractToolWindow(@NotNull String name, @NotNull Project project) {
        super(Constants.TAB_DEFAULT_VERTICAL,Constants.TAB_DEFAULT_BORDERLESS);

        this.projectBusConnection = project.getMessageBus().connect(this);
        this.appBusConnection = ApplicationManager.getApplication().getMessageBus().connect(this);

        this.project = project;
        this.name = name;

        setToolbar(createActionToolbar());
        setContent(createComponents());
        registerListeners();
    }

    abstract JPanel createActionToolbar();
    abstract JComponent createComponents();
    abstract void registerListeners();
    abstract void onChange();

    JPanel createToolbarPanel(ActionGroup actionGroup) {
        if (actionGroup == null) {
            return null;
        }
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(Constants.TITLE + " toolbar", actionGroup, true);
        actionToolbar.setTargetComponent(this);
        JPanel toolbarPanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toolbarPanel.add(actionToolbar.getComponent());
        return toolbarPanel;
    }

    @Override
    public void dispose() {
        // Disconnect and release resources
        projectBusConnection.disconnect();
        appBusConnection.disconnect();
    }
}

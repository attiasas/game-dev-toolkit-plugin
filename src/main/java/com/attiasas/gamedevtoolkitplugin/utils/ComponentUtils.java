package com.attiasas.gamedevtoolkitplugin.utils;

import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class ComponentUtils {

    public static void addCenteredHyperlinkLabel(JPanel panel, HyperlinkLabel hyperlinkLabel) {
        hyperlinkLabel.setMaximumSize(hyperlinkLabel.getPreferredSize());
        hyperlinkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(hyperlinkLabel);
    }

    public static JPanel createCenteredComponentPanel(Component component) {
        JBPanel<?> panel = new JBPanel<>(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(component, c);
        panel.setBackground(UIUtil.getTableBackground());
        return panel;
    }
}

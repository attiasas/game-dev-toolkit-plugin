package com.attiasas.gamedevtoolkitplugin.utils;

import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class ComponentUtils {

    public static JTextArea createJTextArea(String text, boolean lineWrap) {
        JTextArea jTextArea = new JTextArea(text);
        jTextArea.setOpaque(true);
        jTextArea.setEditable(false);
        jTextArea.setLineWrap(lineWrap);
        jTextArea.setWrapStyleWord(true);
        jTextArea.setBackground(UIUtil.getTableBackground());
        jTextArea.setMargin(new JBInsets(2, 2, 2, 2));
        return jTextArea;
    }

    public static JLabel createDisabledTextLabel(String text) {
        JLabel label = new JBLabel(text);
        label.setEnabled(false);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    public static void replaceAndUpdateUI(JPanel panel, JComponent component, Object constraint) {
        panel.removeAll();
        panel.add(component, constraint);
        panel.validate();
        panel.repaint();
    }

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

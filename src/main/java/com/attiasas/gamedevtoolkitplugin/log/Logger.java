package com.attiasas.gamedevtoolkitplugin.log;

import com.attiasas.gamedevtoolkitplugin.utils.Constants;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: Assaf, On 2/25/2023
 * @Description:
 **/
public class Logger {

    private static final NotificationGroup EVENT_LOG_NOTIFIER = NotificationGroupManager.getInstance().getNotificationGroup(Constants.LOG_TITLE + " Log");
    private static final NotificationGroup BALLOON_NOTIFIER = NotificationGroupManager.getInstance().getNotificationGroup(Constants.LOG_TITLE + " Errors");
    private static Notification lastNotification;

    private static final com.intellij.openapi.diagnostic.Logger intellijLogger = com.intellij.openapi.diagnostic.Logger.getInstance(Logger.class);

    public static Logger getInstance() {
        return ApplicationManager.getApplication().getService(Logger.class);
    }

    private Logger() {
    }

    public void debug(String message) {
        intellijLogger.debug(message);
    }

    public void info(String message) {
        intellijLogger.info(message);
        NotificationType notificationType = NotificationType.INFORMATION;
        log(Constants.LOG_TITLE, message, notificationType);
    }

    public void warn(String message) {
        intellijLogger.warn(message);
        NotificationType notificationType = NotificationType.WARNING;
        log(Constants.LOG_TITLE, message, notificationType);
    }

    public void error(String message) {
        // log as "warn" log level to avoid popup annoying fatal errors
        intellijLogger.warn(message);
        NotificationType notificationType = NotificationType.ERROR;
        popupBalloon(message, notificationType);
        log(Constants.LOG_ERROR_TITLE, message, notificationType);
    }

    public void error(String message, Throwable t) {
        // log as "warn" log level to avoid popup annoying fatal errors
        intellijLogger.warn(message, t);
        NotificationType notificationType = NotificationType.ERROR;
        popupBalloon(message, notificationType);
        String title = StringUtils.defaultIfBlank(t.getMessage(), Constants.LOG_ERROR_TITLE);
        log(title, message + System.lineSeparator() + ExceptionUtils.getStackTrace(t), notificationType);
    }

    private static void log(String title, String message, NotificationType notificationType) {
        if (StringUtils.isBlank(message)) {
            message = title;
        }
        Notifications.Bus.notify(EVENT_LOG_NOTIFIER.createNotification(title, AddLogLevelPrefix(message, notificationType), notificationType));
    }

    private static void popupBalloon(String content, NotificationType notificationType) {
        if (lastNotification != null) {
            lastNotification.hideBalloon();
        }
        if (StringUtils.isBlank(content)) {
            content = Constants.LOG_ERROR_TITLE;
        }
        Notification notification = BALLOON_NOTIFIER.createNotification(Constants.LOG_ERROR_TITLE, content, notificationType);
        lastNotification = notification;
        Notifications.Bus.notify(notification);
    }

    private static String AddLogLevelPrefix(String message, NotificationType notificationType) {
        switch (notificationType) {
            case WARNING:
                return "[WARN] " + message;
            case ERROR:
                return "[ERROR] " + message;
        }
        return "[INFO] " + message;
    }

    /**
     * Add a log message with an open settings link.
     * Usage example:
     * Logger.openSettings("It looks like Gradle home was not properly set in your project.
     * Click <a href=\"#settings\">here</a> to set Gradle home.", project, GradleConfigurable.class);
     *
     * @param details      - The log message
     * @param project      - IDEA project
     * @param configurable - IDEA settings to open
     */
    public static void addOpenSettingsLink(String details, Project project, Class<? extends Configurable> configurable) {
        EVENT_LOG_NOTIFIER.createNotification(Constants.LOG_TITLE, AddLogLevelPrefix(details, NotificationType.INFORMATION), NotificationType.INFORMATION)
                .addAction(new AnAction() {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        ShowSettingsUtil.getInstance().showSettingsDialog(project, configurable);
                    }
                })
                .notify(project);
    }
}

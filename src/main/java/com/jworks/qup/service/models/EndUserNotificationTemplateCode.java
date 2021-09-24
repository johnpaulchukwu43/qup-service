package com.jworks.qup.service.models;

/**
 * @author Johnpaul Chukwu.
 * @since 04/05/2021
 */
public enum EndUserNotificationTemplateCode {

    USER_EMAIL_VERIFICATION_NOTIFICATION("default_user_email_verification_notification"),
    USER_UPDATE_PASSWORD_REQUEST_NOTIFICATION("default_user_update_password_request_notification");

    private String name;

    EndUserNotificationTemplateCode(String name) {
        this.name = name;
    }

    public static EndUserNotificationTemplateCode toTemplateCode(String notificationTemplateString) {
        for (EndUserNotificationTemplateCode endUserNotificationTemplateCode : EndUserNotificationTemplateCode.values()) {
            if (endUserNotificationTemplateCode.name().equalsIgnoreCase(notificationTemplateString)) {
                return endUserNotificationTemplateCode;
            }
        }

        throw new IllegalArgumentException(String.format("Unmatched notification template code: %s", notificationTemplateString));
    }

    public String getName() {
        return name;
    }
}

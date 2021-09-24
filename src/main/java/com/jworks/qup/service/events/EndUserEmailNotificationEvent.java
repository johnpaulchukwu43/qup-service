package com.jworks.qup.service.events;

import com.jworks.app.commons.models.NotificationType;
import com.jworks.qup.service.entities.BaseEndUser;
import com.jworks.qup.service.models.EndUserNotificationTemplateCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Johnpaul Chukwu.
 * @since 03/06/2021
 */

@Getter
public class EndUserEmailNotificationEvent<T extends BaseEndUser> extends ApplicationEvent {

    private final T user;
    private final EndUserNotificationTemplateCode endUserNotificationTemplateCode;
    private NotificationType notificationType = NotificationType.EMAIL;

    public EndUserEmailNotificationEvent(Object source, T user, EndUserNotificationTemplateCode endUserNotificationTemplateCode) {
        super(source);
        this.user = user;
        this.endUserNotificationTemplateCode = endUserNotificationTemplateCode;
    }
}

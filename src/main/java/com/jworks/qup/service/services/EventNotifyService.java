package com.jworks.qup.service.services;


import com.jworks.qup.service.entities.BaseEndUser;
import com.jworks.qup.service.events.EndUserEmailNotificationEvent;
import com.jworks.qup.service.models.EndUserNotificationTemplateCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * @author Johnpaul Chukwu.
 * @since 25/06/2021
 */

@Service
@RequiredArgsConstructor
public class EventNotifyService {

    private final ApplicationEventPublisher eventPublisher;

    <T extends BaseEndUser> void broadCastEndUserEmailNotification(T endUser, EndUserNotificationTemplateCode templateCode) {

        eventPublisher.publishEvent(new EndUserEmailNotificationEvent(this, endUser, templateCode));
    }
}

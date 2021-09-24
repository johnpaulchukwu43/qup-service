package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.models.NotificationStatus;
import com.jworks.app.commons.models.NotificationType;
import com.jworks.app.commons.utils.ReferenceGenerator;
import com.jworks.qup.service.entities.*;
import com.jworks.qup.service.events.EndUserEmailNotificationEvent;
import com.jworks.qup.service.models.EndUserNotificationDto;
import com.jworks.qup.service.models.EndUserNotificationTemplateCode;
import com.jworks.qup.service.models.NotificationTemplateDto;
import com.jworks.qup.service.processors.EndUserNotificationProcessor;
import com.jworks.qup.service.repositories.EndUserNotificationRepository;
import com.jworks.qup.service.repositories.EndUserOnboardNotificationRepository;
import com.jworks.qup.service.utils.TemplateProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import static com.jworks.app.commons.utils.AppUtil.isEmail;
import static com.jworks.app.commons.utils.ReferenceGenerator.INTENT_END_USER_NOTIFICATION_EMAIL;
import static com.jworks.qup.service.models.NotificationTemplateDto.MetaData.toMetaData;

/**
 * @author Johnpaul Chukwu.
 * @since 27/04/2021
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EndUserNotificationService {

    private final EndUserNotificationTemplateService endUserNotificationTemplateService;
    private final EndUserOnboardNotificationRepository endUserOnboardNotificationRepository;
    private final EndUserNotificationRepository endUserNotificationRepository;
    private final EndUserNotificationProcessor endUserNotificationProcessor;
    @Value("${sendgrid.user-notification-template-code}")
    private String providerTemplateCode;

    public void createNotification(EndUserNotificationDto endUserNotificationDto) throws SystemServiceException {

        NotificationType notificationType = endUserNotificationDto.getNotificationType();
        String reference = endUserNotificationDto.getReference();
        String senderAddress = endUserNotificationDto.getSenderAddress();
        String recipient = endUserNotificationDto.getRecipient();
        String senderName = endUserNotificationDto.getSender();
        BaseEndUser baseEndUser = endUserNotificationDto.getUser();

        if (baseEndUser == null) throw new IllegalArgumentException("baseEndUser is required !");

        if (StringUtils.isBlank(recipient)) {
            throw new SystemServiceException("Recipient is required");
        }

        if (StringUtils.isBlank(senderName)) {
            throw new SystemServiceException("Sender name is required");
        }

        if (NotificationType.EMAIL.equals(notificationType)) {

            if (StringUtils.isBlank(senderAddress))
                throw new SystemServiceException("Sender is required");

            if (!isEmail(recipient))
                throw new SystemServiceException("invalid email address");
        }

        BaseNotification baseNotification = BaseNotification.builder()
                .message(endUserNotificationDto.getMessage())
                .recipient(recipient)
                .reference(reference)
                .sender(senderName)
                .notificationType(notificationType)
                .providerTemplateCode(endUserNotificationDto.getProviderTemplateCode())
                .subject(endUserNotificationDto.getSubject())
                .notificationAttempts(0L)
                .notificationStatus(NotificationStatus.PENDING)
                .build();

        if (baseEndUser instanceof EndUser) {
            EndUserNotification endUserNotification = (EndUserNotification) baseNotification;
            endUserNotification.setEndUser((EndUser) baseEndUser);
            endUserNotification = endUserNotificationRepository.save(endUserNotification);

            endUserNotificationProcessor.processNotification(endUserNotification);

        } else if (baseEndUser instanceof EndUserOnboardRequest) {
            EndUserOnboardNotification endUserOnboardNotification = (EndUserOnboardNotification) baseNotification;
            endUserOnboardNotification.setEndUserOnboardRequest((EndUserOnboardRequest) baseEndUser);
            endUserOnboardNotification = endUserOnboardNotificationRepository.save(endUserOnboardNotification);
            endUserNotificationProcessor.processNotification(endUserOnboardNotification);
        } else {
            throw new SystemServiceException("Unable to create notification entry. Contact Admin.");
        }
    }


    @EventListener(EndUserEmailNotificationEvent.class)
    public void handleEndUserEmailNotificationEvent(EndUserEmailNotificationEvent endUserEmailNotificationEvent) {

        BaseEndUser user = endUserEmailNotificationEvent.getUser();

        EndUserNotificationTemplateCode endUserNotificationTemplateCode = endUserEmailNotificationEvent.getEndUserNotificationTemplateCode();


        log.info("received email broadcast with code: {}, belonging to user with ref: {}", endUserNotificationTemplateCode, user.getUserReference());

        try {
            EndUserNotificationTemplate endUserNotificationTemplate = endUserNotificationTemplateService.getByCodeAndNotificationType(endUserNotificationTemplateCode, NotificationType.EMAIL);

            NotificationTemplateDto.MetaData metaData = extractNotificationMetaData(user);

            String body = TemplateProcessor.prepareTemplateContent(endUserNotificationTemplate.getBody(), metaData);

            EndUserNotificationDto emailNotificationDto = EndUserNotificationDto.builder()
                    .user(user)
                    .message(body)
                    .subject(endUserNotificationTemplate.getSubject())
                    .reference(ReferenceGenerator.generateRef(INTENT_END_USER_NOTIFICATION_EMAIL))
                    .notificationType(NotificationType.EMAIL)
                    .recipient(user.getEmailAddress())
                    .sender(endUserNotificationTemplate.getSender())
                    .senderAddress(endUserNotificationTemplate.getSenderAddress())
                    .providerTemplateCode(endUserNotificationTemplate.getProviderTemplateCode())
                    .build();


            createNotification(emailNotificationDto);
        } catch (Exception ex) {
            log.error("Error occurred:", ex);
        }
    }

    private NotificationTemplateDto.MetaData extractNotificationMetaData(BaseEndUser baseEndUser) {
        return toMetaData(baseEndUser);
    }


}

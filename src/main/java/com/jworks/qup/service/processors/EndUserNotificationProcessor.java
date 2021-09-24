package com.jworks.qup.service.processors;

import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.models.NotificationStatus;
import com.jworks.qup.service.entities.BaseNotification;
import com.jworks.qup.service.entities.EndUserNotification;
import com.jworks.qup.service.entities.EndUserOnboardNotification;
import com.jworks.qup.service.models.ProviderResponseData;
import com.jworks.qup.service.repositories.EndUserNotificationRepository;
import com.jworks.qup.service.repositories.EndUserOnboardNotificationRepository;
import com.jworks.qup.service.services.EndUserEmailNotificationDispatcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Johnpaul Chukwu.
 * @since 27/04/2021
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EndUserNotificationProcessor {

    private final EndUserOnboardNotificationRepository endUserOnboardNotificationRepository;
    private final EndUserNotificationRepository endUserNotificationRepository;
    private final EndUserEmailNotificationDispatcherService endUserEmailNotificationDispatcherService;


    public <T extends BaseNotification> void processNotification(T userNotification) throws SystemServiceException {

        EndUserNotification endUserNotification;
        EndUserOnboardNotification endUserOnboardNotification;
        ProviderResponseData providerResponseData;

        if (userNotification instanceof EndUserNotification) {
            endUserNotification = (EndUserNotification) userNotification;

            endUserNotification.setNotificationAttempts(endUserNotification.getNotificationAttempts() + 1);
            endUserNotification.setBeingSent(true);
            endUserNotification.setNotificationStatus(NotificationStatus.PROCESSING);

            endUserNotification = endUserNotificationRepository.save(endUserNotification);

            providerResponseData = endUserEmailNotificationDispatcherService.sendMessage(endUserNotification);

            handleNotificationResponse(providerResponseData, endUserNotification);

            endUserNotificationRepository.save(endUserNotification);


        } else if (userNotification instanceof EndUserOnboardNotification) {
            endUserOnboardNotification = (EndUserOnboardNotification) userNotification;

            endUserOnboardNotification.setNotificationStatus(NotificationStatus.PROCESSING);
            endUserOnboardNotification.setNotificationAttempts(endUserOnboardNotification.getNotificationAttempts() + 1);
            endUserOnboardNotification.setBeingSent(true);

            endUserOnboardNotification = endUserOnboardNotificationRepository.save(endUserOnboardNotification);

            providerResponseData = endUserEmailNotificationDispatcherService.sendMessage(endUserOnboardNotification);

            handleNotificationResponse(providerResponseData, endUserOnboardNotification);

            endUserOnboardNotificationRepository.save(endUserOnboardNotification);

        } else {
            throw new SystemServiceException("Unable to process notification entry. Contact Admin.");
        }


    }


    private <T extends BaseNotification> void handleNotificationResponse(ProviderResponseData responseData, T userNotification) {


        NotificationStatus notificationStatus = responseData.getStatus();
        userNotification.setBeingSent(false);
        userNotification.setProviderNotificationStatus(notificationStatus);

        switch (notificationStatus) {

            case COMPLETED:
                userNotification.setNotificationStatus(NotificationStatus.COMPLETED);
                userNotification.setCompletedOn(Timestamp.valueOf(LocalDateTime.now()));
                break;
            default:
                userNotification.setNotificationStatus(NotificationStatus.FAILED);
                break;

        }

    }


}

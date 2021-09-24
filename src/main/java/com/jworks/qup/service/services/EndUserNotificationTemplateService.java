package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.models.NotificationType;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.qup.service.entities.EndUserNotificationTemplate;
import com.jworks.qup.service.models.EndUserNotificationTemplateCode;
import com.jworks.qup.service.models.NotificationTemplateDto;
import com.jworks.qup.service.repositories.NotificationTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jworks.app.commons.models.NotificationType.toNotificationType;
import static com.jworks.qup.service.models.NotificationTemplateDto.toNotificationTemplateDto;

/**
 * @author Johnpaul Chukwu.
 * @since 27/04/2021
 */

@Slf4j
@Service
public class EndUserNotificationTemplateService extends ServiceBluePrintImpl<EndUserNotificationTemplate, NotificationTemplateDto> {

    private final NotificationTemplateRepository notificationTemplateRepository;

    @Autowired
    public EndUserNotificationTemplateService(NotificationTemplateRepository notificationTemplateRepository) {
        super(notificationTemplateRepository);
        this.notificationTemplateRepository = notificationTemplateRepository;
    }

    public EndUserNotificationTemplate getByCodeAndNotificationType(EndUserNotificationTemplateCode code, NotificationType notificationType) throws UnProcessableOperationException {
        return notificationTemplateRepository.findByCodeAndNotificationType(code, notificationType).orElseThrow(() ->
                new UnProcessableOperationException(String.format("End user notification template code: %s not found.", code)));
    }

    public boolean notificationAlreadyExists(EndUserNotificationTemplateCode code, NotificationType notificationType) {

        return notificationTemplateRepository.findByCodeAndNotificationType(code, notificationType).isPresent();
    }

    public NotificationTemplateDto createNotificationTemplate(NotificationTemplateDto notificationTemplateDto) {

        EndUserNotificationTemplate disputeNotificationTemplate = new EndUserNotificationTemplate();

        EndUserNotificationTemplateCode disputeNotificationTemplateCode = EndUserNotificationTemplateCode.toTemplateCode(notificationTemplateDto.getCode());

        NotificationType notificationType = toNotificationType(notificationTemplateDto.getNotificationType());

        notificationAlreadyExists(disputeNotificationTemplateCode, notificationType);

        BeanUtils.copyProperties(notificationTemplateDto, disputeNotificationTemplate);

        disputeNotificationTemplate.setCode(disputeNotificationTemplateCode);
        disputeNotificationTemplate.setNotificationType(notificationType);

        return toNotificationTemplateDto(disputeNotificationTemplate);
    }


    public void createNotificationTemplates(List<EndUserNotificationTemplate> notificationTemplateList) {

        for (EndUserNotificationTemplate notificationTemplate : notificationTemplateList) {
            try {
                if (!notificationAlreadyExists(notificationTemplate.getCode(), notificationTemplate.getNotificationType())) {
                    save(notificationTemplate);
                }
            } catch (Exception ex) {
                log.error("Error occured:", ex);
            }
        }
    }

}

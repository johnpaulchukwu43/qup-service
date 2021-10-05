package com.jworks.qup.service.repositories;

import com.jworks.app.commons.models.NotificationType;
import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.EndUserNotificationTemplate;
import com.jworks.qup.service.models.EndUserNotificationTemplateCode;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Repository
public interface NotificationTemplateRepository extends BaseRepository<EndUserNotificationTemplate> {

    Optional<EndUserNotificationTemplate> findByCodeAndNotificationType(EndUserNotificationTemplateCode endUserNotificationTemplateCode, NotificationType notificationType);

}

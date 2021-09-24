package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.EndUserNotification;
import org.springframework.stereotype.Repository;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Repository
public interface EndUserNotificationRepository extends BaseRepository<EndUserNotification, Long> {

}

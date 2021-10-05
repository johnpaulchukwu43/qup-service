package com.jworks.qup.service.providers.impl;

import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.enums.QueueLocationType;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.enums.QueueStatus;
import com.jworks.qup.service.repositories.EndUserQueueRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.jworks.qup.service.providers.EntityProvider;

/**
 * @author bodmas
 * @since Oct 5, 2021.
 */
@Component
@AllArgsConstructor
public class EndUserQueueProvider implements EntityProvider<EndUserQueue> {

    private final EndUserQueueRepository endUserQueueRepository;
    private final EndUserProvider endUserProvider;
    private final BusinessProvider businessProvider;

    @Override
    public EndUserQueue provide() {
        EndUserQueue endUserQueue = EndUserQueue.builder()
                .requiresQueueForm(false)
                .queueStatus(QueueStatus.INACTIVE)
                .queuePurpose(QueuePurpose.BUSINESS)
                .queueLocationValue(faker.address().streetAddress())
                .queueLocationType(QueueLocationType.PHYSICAL)
                .queueCode(faker.code().ean13())
                .name(faker.app().name())
                .isFormDefaultSet(false)
                .expirationDate(LocalDateTime.now().plusMinutes(5))
                .endUser(endUserProvider.provide())
                .capacity(faker.number().numberBetween(2L, 10L))
                .business(businessProvider.provide())
                .build();
        return endUserQueue;
    }

    @Override
    public void preSave(EndUserQueue endUserQueue) {
        if (endUserQueue.getEndUser() != null)
            endUserProvider.save(endUserQueue.getEndUser());
        if (endUserQueue.getBusiness() != null)
            businessProvider.save(endUserQueue.getBusiness());
    }

    @Override
    public EndUserQueueRepository getRepository() {
        return endUserQueueRepository;
    }
}

package com.jworks.qup.service.providers.impl;

import com.jworks.qup.service.config.QueueIdSequenceGeneratorConfig;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.enums.QueueLocationType;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.enums.QueueStatus;
import com.jworks.qup.service.models.CreateEndUserQueueDto;
import com.jworks.qup.service.providers.DtoProvider;
import com.jworks.qup.service.repositories.EndUserQueueRepository;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.jworks.qup.service.providers.EntityProvider;
import com.jworks.qup.service.services.QueueIdSequenceGenerator;
import org.springframework.context.annotation.Import;

/**
 * @author bodmas
 * @since Oct 5, 2021.
 */
@Component
@AllArgsConstructor
@Import(QueueIdSequenceGeneratorConfig.class)
public class EndUserQueueProvider implements EntityProvider<EndUserQueue>, DtoProvider<CreateEndUserQueueDto> {

    private final EndUserQueueRepository endUserQueueRepository;
    private final EndUserProvider endUserProvider;
    private final BusinessProvider businessProvider;
    private final QueueIdSequenceGenerator queueIdSequenceGenerator;

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
                .capacity(faker.number().numberBetween(2L, 11L))
                .business(businessProvider.provide())
                .build();
        return endUserQueue;
    }

    @Override
    public EndUserQueue save(EndUserQueue endUserQueue) {
        EndUserQueue savedEndUserQueue = EntityProvider.super.save(endUserQueue);
        queueIdSequenceGenerator.ensureRegistered(savedEndUserQueue.getId());
        return savedEndUserQueue;
    }

    @Override
    public CreateEndUserQueueDto provideDto() {
        return CreateEndUserQueueDto.builder()
                .expirationDate(LocalDateTime.now().plusMinutes(5))
                .maxNumberOfUsersInPool(10L)
                .maxNumberOfUsersOnQueue(20L)
                .queueLocationType("PHYSICAL")
                .queueLocationValue(faker.address().streetAddress())
                .queueName(faker.app().name())
                .build();
    }

    @Override
    public void preSave(EndUserQueue endUserQueue) {
        if (endUserQueue.getEndUser() != null)
            endUserProvider.save(endUserQueue.getEndUser());
        if (endUserQueue.getBusiness() != null)
            businessProvider.save(endUserQueue.getBusiness());
    }

    @Override
    public boolean postDelete(EndUserQueue endUserQueue) {
        boolean isWithoutErrors = true;
        if (endUserQueue.getEndUser() != null)
            isWithoutErrors &= endUserProvider.delete(endUserQueue.getEndUser());
        if (endUserQueue.getBusiness() != null)
            isWithoutErrors &= businessProvider.delete(endUserQueue.getBusiness());
        return isWithoutErrors;
    }

    public long getNextJoinId(EndUserQueue endUserQueue) {
        return queueIdSequenceGenerator.generateNextJoinId(endUserQueue.getId());
    }

    @Override
    public EndUserQueueRepository getRepository() {
        return endUserQueueRepository;
    }
}

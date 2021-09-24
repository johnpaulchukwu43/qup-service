package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.qup.service.entities.Business;
import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.models.AssignBusinessToQueueDto;
import com.jworks.qup.service.repositories.BusinessRepository;
import com.jworks.qup.service.repositories.EndUserQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndUserQueueBusinessService {

    private final EndUserQueueRepository endUserQueueRepository;
    private final BusinessRepository businessRepository;
    private final EndUserService endUserService;


    public void attachBusinessToQueue (AssignBusinessToQueueDto assignBusinessToQueueDto, String userReference) throws UnProcessableOperationException, NotFoundRestApiException {

        EndUser ownerOfQueue = endUserService.getUserByUserReference(userReference);


        EndUserQueue endUserQueue = endUserQueueRepository.findById(assignBusinessToQueueDto.getQueueId())
                .orElseThrow(() -> new UnProcessableOperationException(String.format("Queue with id: %s does not exist.", assignBusinessToQueueDto.getQueueId())));

        Business business = businessRepository.findById(assignBusinessToQueueDto.getBusinessId())
                .orElseThrow(() -> new UnProcessableOperationException(String.format("Business with id: %s does not exist.", assignBusinessToQueueDto.getBusinessId())));

        if(!ownerOfQueue.getId().equals(endUserQueue.getEndUser().getId())) throw new UnauthorizedUserException("Cannot access queues belonging to another user.");

        if(!ownerOfQueue.getId().equals(business.getEndUser().getId())) throw new UnauthorizedUserException("Cannot access business belonging to another user.");

        endUserQueue.setBusiness(business);
        endUserQueue.setQueuePurpose(QueuePurpose.BUSINESS);

        endUserQueueRepository.save(endUserQueue);
    }

}

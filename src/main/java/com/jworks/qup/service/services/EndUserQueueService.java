package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.BadRequestException;
import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.models.PageOutput;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.app.commons.utils.ReferenceGenerator;
import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.entities.EndUserPoolConfig;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.enums.QueueLocationType;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.enums.QueueStatus;
import com.jworks.qup.service.models.*;
import com.jworks.qup.service.repositories.EndUserQueueRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.jworks.app.commons.utils.AppUtil.toLocalDateTime;
import static com.jworks.app.commons.utils.ReferenceGenerator.INTENT_QUEUE_REFERENCE;
import static com.jworks.qup.service.enums.QueueStatus.toQueueStatus;

/**
 * @author Johnpaul Chukwu.
 * @since 30/07/2021
 */

@Slf4j
@Service
public class EndUserQueueService extends ServiceBluePrintImpl<EndUserQueue, EndUserQueueDto> {

    private final EndUserQueueRepository endUserQueueRepository;
    private final EndUserService endUserService;
    private final EndUserPoolConfigService endUserPoolConfigService;

    public EndUserQueueService(EndUserQueueRepository endUserQueueRepository, EndUserService endUserService, EndUserPoolConfigService endUserPoolConfigService) {
        super(endUserQueueRepository);
        this.endUserQueueRepository = endUserQueueRepository;
        this.endUserService = endUserService;
        this.endUserPoolConfigService = endUserPoolConfigService;
    }


    @Transactional
    public EndUserQueueDto createQueue(CreateEndUserQueueDto createEndUserQueueDto, String userReference) throws NotFoundRestApiException, SystemServiceException {

        EndUser ownerOfQueue = endUserService.getUserByUserReference(userReference);

        EndUserQueue endUserQueue = EndUserQueue.builder()
                .name(createEndUserQueueDto.getQueueName())
                .capacity(createEndUserQueueDto.getMaxNumberOfUsersOnQueue())
                .endUser(ownerOfQueue)
                .expirationDate(createEndUserQueueDto.getExpirationDate())
                .queueCode(ReferenceGenerator.generateRef(INTENT_QUEUE_REFERENCE))
                .queueLocationType(QueueLocationType.valueOf(createEndUserQueueDto.getQueueLocationType()))
                .queueLocationValue(createEndUserQueueDto.getQueueLocationValue())
                .queuePurpose(QueuePurpose.PERSONAL)
                .queueStatus(QueueStatus.ACTIVE)
                .requiresQueueForm(false)
                .build();

        endUserQueue = save(endUserQueue);


        createAssociatedPoolConfigForQueue(endUserQueue, createEndUserQueueDto.getMaxNumberOfUsersInPool());

        return convertEntityToDto(endUserQueue);

    }


    public void updateQueueDetails(Long queueId, UpdateEndUserQueueDto updateEndUserQueueDto, String userReference) throws NotFoundRestApiException, SystemServiceException {

        //todo confirm do we need to check if the queue is active before making changes.

        EndUserQueue endUserQueue = endUserQueueRepository.findById(queueId)
                .orElseThrow(() -> new NotFoundRestApiException(String.format("Queue with id: %s does not exist.", queueId)));

        if(!userReference.equals(endUserQueue.getEndUser().getUserReference())) throw new UnauthorizedUserException("Cannot update queue belonging to another user.");

        endUserQueue.setExpirationDate(updateEndUserQueueDto.getExpirationDate());
        //todo what happens if we try to update the capacity to a value less than the current number of users on the queue?
        endUserQueue.setCapacity(updateEndUserQueueDto.getMaxNumberOfUsersOnQueue());
        //todo what happens if we try to update the capacity to a value less than the current number of users on the pool?
        endUserQueue.getPoolConfig().setCapacity(updateEndUserQueueDto.getMaxNumberOfUsersInPool());
        save(endUserQueue);
    }

    public void changeQueueStatus(Long queueId, EndUserQueueStatusDto endUserQueueStatusDto, String userReference) throws NotFoundRestApiException, SystemServiceException {

        EndUserQueue endUserQueue = endUserQueueRepository.findById(queueId)
                .orElseThrow(() -> new NotFoundRestApiException(String.format("Queue with id: %s does not exist.", queueId)));

        if(!userReference.equals(endUserQueue.getEndUser().getUserReference())) throw new UnauthorizedUserException("Cannot update status of queue belonging to another user.");

        QueueStatus queueStatusTobeUpdatedTo = toQueueStatus(endUserQueueStatusDto.getQueueStatus());

        endUserQueue.setQueueStatus(queueStatusTobeUpdatedTo);
        //todo check if the queueStatusTobeUpdatedTo is reset, then invalidate all reservations
        save(endUserQueue);
    }

    private void createAssociatedPoolConfigForQueue(EndUserQueue endUserQueue, Long poolCapacity) throws SystemServiceException {
        endUserPoolConfigService.save(
                new EndUserPoolConfig(poolCapacity, endUserQueue)
        );
    }

    @Override
    public EndUserQueueDto convertEntityToDto(EndUserQueue entity) {

        EndUserQueueDto endUserQueueDto = new EndUserQueueDto();
        BeanUtils.copyProperties(entity,endUserQueueDto);
        return endUserQueueDto;
    }

    public PageOutput<EndUserQueueDto> getQueueBelongingToUser(ClientSearchQueueDto clientSearchQueueDto, PageRequest pageRequest) throws BadRequestException {

        QueueStatus queueStatus = null;
        QueuePurpose queuePurpose = null;
        Timestamp createdOn = null;
        Timestamp expiryDate = null;

        if(StringUtils.isNotBlank(clientSearchQueueDto.getQueueStatus())){
            queueStatus = toQueueStatus(clientSearchQueueDto.getQueueStatus());
        }

        if(StringUtils.isNotBlank(clientSearchQueueDto.getQueuePurpose())){
            queuePurpose = QueuePurpose.toQueuePurpose(clientSearchQueueDto.getQueuePurpose());
        }

        if(StringUtils.isNotBlank(clientSearchQueueDto.getQueuePurpose())){
            LocalDateTime localDateTime = toLocalDateTime(clientSearchQueueDto.getCreatedOn(), log);
            if(localDateTime == null) throw new BadRequestException(String.format("Invalid date format for createdOn:  %s", clientSearchQueueDto.getQueuePurpose()));
            createdOn = Timestamp.valueOf(localDateTime);
        }

        if(StringUtils.isNotBlank(clientSearchQueueDto.getExpiryDate())){
            LocalDateTime localDateTime = toLocalDateTime(clientSearchQueueDto.getCreatedOn(), log);
            if(localDateTime == null) throw new BadRequestException(String.format("Invalid date format for expiryDate:  %s", clientSearchQueueDto.getExpiryDate()));
            expiryDate = Timestamp.valueOf(localDateTime);
        }


        Page<EndUserQueueDto> endUserQueues = endUserQueueRepository.getAllQueuesByUserReferenceFilteredBy(
                queueStatus, clientSearchQueueDto.getQueueCode(),
                createdOn, queuePurpose, clientSearchQueueDto.getUserReference(),
                expiryDate, pageRequest
        );

        return  PageOutput.fromPage(endUserQueues);

    }

    public EndUserQueue getQueueById(Long queueId) throws NotFoundRestApiException {
        return endUserQueueRepository.findById(queueId)
                .orElseThrow(() -> new NotFoundRestApiException(String.format("Queue with id %s not found.", queueId)));
    }

    public PageOutput<EndUserQueueInfo> searchForQueueInfo(ClientSearchQueueInfo clientSearchQueueInfo, PageRequest paginationRequest) {
        Page<EndUserQueueInfo> queueInfos = endUserQueueRepository.getAllQueueInfoFilteredBy(
                clientSearchQueueInfo.getQueueCode(),
                clientSearchQueueInfo.getBusinessName(),
                clientSearchQueueInfo.getQueueName(),
                paginationRequest
        );

        return PageOutput.fromPage(queueInfos);
    }
}

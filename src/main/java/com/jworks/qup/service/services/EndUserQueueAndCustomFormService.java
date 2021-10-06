package com.jworks.qup.service.services;

import com.jworks.app.commons.enums.EntityStatus;
import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.qup.service.entities.CustomEndUserForm;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.models.AssignDefaultCustomFormToQueueDto;
import com.jworks.qup.service.models.ToggleCustomFormRequirementDto;
import com.jworks.qup.service.repositories.CustomEndUserFormRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author Johnpaul Chukwu.
 * @since 30/07/2021
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EndUserQueueAndCustomFormService {

    private final EndUserQueueService endUserQueueService;

    private final CustomEndUserFormRepository customEndUserFormRepository;


    @Transactional
    public void setAsFormDefaultForQueue(AssignDefaultCustomFormToQueueDto assignDefaultCustomFormToQueueDto, String ownerReferenceOfQueue) throws NotFoundRestApiException, UnProcessableOperationException, SystemServiceException {

        Long queueId = assignDefaultCustomFormToQueueDto.getQueueId();

        Long customFormId = assignDefaultCustomFormToQueueDto.getCustomFormId();

        EndUserQueue endUserQueue = endUserQueueService.getQueueById(queueId);

        if (!ownerReferenceOfQueue.equalsIgnoreCase(endUserQueue.getEndUser().getUserReference()))
            throw new UnauthorizedUserException("Cannot update queue belonging to another user. Confirm you are logged in as the right user.");

        Set<CustomEndUserForm> customEndUserForms = endUserQueue.getCustomEndUserFormList();

        List<CustomEndUserForm> updatedForms = customEndUserForms.stream().peek(customEndUserForm -> {
            if (customFormId.equals(customEndUserForm.getId())) {
                customEndUserForm.setEntityStatus(EntityStatus.ACTIVE);
            } else {
                customEndUserForm.setEntityStatus(EntityStatus.INACTIVE);
            }
        }).collect(Collectors.toList());

        boolean customFormIdExists = false;

        for (CustomEndUserForm updatedForm : updatedForms) {
            if (EntityStatus.ACTIVE.equals(updatedForm.getEntityStatus())) {
                customFormIdExists = true;
                break;
            }
        }

        if (customFormIdExists) {
            customEndUserFormRepository.saveAll(updatedForms);
        } else {
            throw new UnProcessableOperationException(String.format("No form found with id: %s", customFormId));
        }

        if (!endUserQueue.isFormDefaultSet()) {
            endUserQueue.setFormDefaultSet(true);
            endUserQueueService.save(endUserQueue);
        }

    }


    public void updateIsFormRequiredOption(ToggleCustomFormRequirementDto toggleCustomFormRequirementDto, String ownerReferenceOfQueue) throws NotFoundRestApiException, UnProcessableOperationException, SystemServiceException {


        Long queueId = toggleCustomFormRequirementDto.getQueueId();

        EndUserQueue endUserQueue = endUserQueueService.getQueueById(queueId);

        if (!ownerReferenceOfQueue.equalsIgnoreCase(endUserQueue.getEndUser().getUserReference()))
            throw new UnauthorizedUserException("Cannot update queue belonging to another user. Confirm you are logged in as the right user.");

        boolean requiresQueueForm = toggleCustomFormRequirementDto.isRequiresQueueForm();

        if (requiresQueueForm && endUserQueue.isRequiresQueueForm())
            throw new UnProcessableOperationException(String.format("Queue with id: %s already requires custom form", queueId));

        if (!requiresQueueForm && !endUserQueue.isRequiresQueueForm())
            throw new UnProcessableOperationException(String.format("Queue with id: %s already, does not require custom form", queueId));

        if (requiresQueueForm) {
            Set<CustomEndUserForm> customEndUserFormList = endUserQueue.getCustomEndUserFormList();
            Long customFormId = toggleCustomFormRequirementDto.getCustomFormId();
            if (customEndUserFormList.isEmpty()) {
                customEndUserFormRepository.findById(customFormId).orElseThrow(() -> new UnProcessableOperationException(String.format("Cannot update Queue with code: %s, to require forms as no existing form records found. Please create a form first.", endUserQueue.getQueueCode())));
            }

            endUserQueue.setRequiresQueueForm(true);
        } else {
            endUserQueue.setRequiresQueueForm(false);
        }

        endUserQueueService.save(endUserQueue);
    }

}

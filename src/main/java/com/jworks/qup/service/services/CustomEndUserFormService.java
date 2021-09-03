package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.app.commons.utils.ReferenceGenerator;
import com.jworks.qup.service.entities.CustomEndUserForm;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.exceptions.CustomEndUserFormException;
import com.jworks.qup.service.models.CreateCustomEndUserFormDto;
import com.jworks.qup.service.models.CustomEndUserAnswerDto;
import com.jworks.qup.service.models.CustomEndUserFormDto;
import com.jworks.qup.service.models.UpdateCustomEndUserFormDto;
import com.jworks.qup.service.repositories.CustomEndUserFormRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jworks.app.commons.utils.ReferenceGenerator.INTENT_QUEUE_FORM;
import static com.jworks.qup.service.enums.CustomEndUserFormType.toCustomEndUserFormType;


/**
 * @author Johnpaul Chukwu.
 * @since 30/07/2021
 */

@Slf4j
@Service
public class CustomEndUserFormService extends ServiceBluePrintImpl<CustomEndUserForm, CustomEndUserFormDto> {

    private final CustomEndUserFormRepository customEndUserFormRepository;

    private final EndUserQueueService endUserQueueService;

    private final CustomEndUserQuestionService customEndUserQuestionService;

    private final CustomEndUserAnswerService customEndUserAnswerService;

    public CustomEndUserFormService(CustomEndUserFormRepository customEndUserFormRepository, EndUserQueueService endUserQueueService, CustomEndUserQuestionService customEndUserQuestionService, CustomEndUserAnswerService customEndUserAnswerService) {
        super(customEndUserFormRepository);
        this.customEndUserFormRepository = customEndUserFormRepository;
        this.endUserQueueService = endUserQueueService;
        this.customEndUserQuestionService = customEndUserQuestionService;
        this.customEndUserAnswerService = customEndUserAnswerService;
    }


    @Transactional
    public CustomEndUserFormDto createForm(CreateCustomEndUserFormDto createCustomEndUserFormDto, Long queueId, String userReference) throws NotFoundRestApiException, SystemServiceException {

        EndUserQueue queue = endUserQueueService.getQueueById(queueId);

        if (!queue.getEndUser().getUserReference().equals(userReference))
            throw new UnauthorizedUserException("Cannot create form for queue belonging to another user.");

        CustomEndUserForm customEndUserForm = CustomEndUserForm.builder()
                .name(createCustomEndUserFormDto.getName())
                .customEndUserFormType(toCustomEndUserFormType(createCustomEndUserFormDto.getFormType()))
                .description(createCustomEndUserFormDto.getDescription())
                .formCode(ReferenceGenerator.generateRef(INTENT_QUEUE_FORM))
                .endUserQueue(queue)
                .build();

        customEndUserForm = save(customEndUserForm);

        customEndUserQuestionService.createQuestions(createCustomEndUserFormDto.getQuestions(), customEndUserForm);

        return convertEntityToDto(customEndUserForm);

    }

    public void answerFormQuestions(CustomEndUserAnswerDto.Answers customEndUserAnswerDto, Long formId) throws UnProcessableOperationException, NotFoundRestApiException, CustomEndUserFormException {

        CustomEndUserForm customEndUserForm = getFormById(formId);

        customEndUserAnswerService.submitAnswersToQuestionsInForm(customEndUserAnswerDto, customEndUserForm);
    }


    public void updateForm(UpdateCustomEndUserFormDto updateCustomEndUserFormDto, Long formId, String userReference) throws UnProcessableOperationException, SystemServiceException {

        CustomEndUserForm customEndUserForm = getFormById(formId);

        String description = updateCustomEndUserFormDto.getDescription();

        if (customEndUserForm.getEndUserQueue().getEndUser().getUserReference().equals(userReference))
            throw new UnauthorizedUserException("Cannot update form belonging to another user.");

        customEndUserForm.setCustomEndUserFormType(toCustomEndUserFormType(updateCustomEndUserFormDto.getFormType()));

        if (StringUtils.isNotBlank(description)) {
            customEndUserForm.setDescription(description);
        }

        customEndUserForm.setName(updateCustomEndUserFormDto.getName());

        save(customEndUserForm);

    }

    public List<CustomEndUserFormDto> getFormsBelongingToQueue(Long queueId, String userReference) throws NotFoundRestApiException {

        EndUserQueue queue = endUserQueueService.getQueueById(queueId);

        if (!queue.getEndUser().getUserReference().equals(userReference))
            throw new UnauthorizedUserException("Cannot get forms for queue belonging to another user.");

        return customEndUserFormRepository.getFormByQueueId(queueId);
    }

    public CustomEndUserFormDto getFormByCode(String code, String userReference) throws UnProcessableOperationException {

        CustomEndUserForm customEndUserForm = customEndUserFormRepository.findByFormCode(code).
                orElseThrow(() -> new UnProcessableOperationException(String.format("No form found for %s", code)));

        if (!customEndUserForm.getEndUserQueue().getEndUser().getUserReference().equals(userReference))
            throw new UnauthorizedUserException("Cannot get form belonging to another user.");

        return convertEntityToDto(customEndUserForm);

    }

    public CustomEndUserForm getFormById(Long customEndUserFormId) throws UnProcessableOperationException {
        return customEndUserFormRepository.findById(customEndUserFormId).
                orElseThrow(() -> new UnProcessableOperationException(String.format("No form found for %s", customEndUserFormId)));
    }

    @Override
    public CustomEndUserFormDto convertEntityToDto(CustomEndUserForm entity) {
        return new CustomEndUserFormDto(entity);
    }
}

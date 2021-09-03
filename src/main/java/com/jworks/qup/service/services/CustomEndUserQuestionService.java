package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.qup.service.entities.CustomEndUserForm;
import com.jworks.qup.service.entities.CustomEndUserQuestion;
import com.jworks.qup.service.models.CreateCustomEndUserQuestionDto;
import com.jworks.qup.service.models.CustomEndUserQuestionDto;
import com.jworks.qup.service.repositories.CustomEndUserFormRepository;
import com.jworks.qup.service.repositories.CustomEndUserQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.jworks.app.commons.enums.EntityStatus.toEntityStatus;
import static com.jworks.qup.service.enums.CustomEndUserAnswerType.toCustomEndUserAnswerType;


/**
 * @author Johnpaul Chukwu.
 * @since 1/09/2021
 */

@Slf4j
@Service
public class CustomEndUserQuestionService extends ServiceBluePrintImpl<CustomEndUserQuestion, CustomEndUserQuestionDto> {

    private final CustomEndUserQuestionRepository customEndUserQuestionRepository;
    private final CustomEndUserFormRepository customEndUserFormRepository;

    public CustomEndUserQuestionService(CustomEndUserQuestionRepository customEndUserQuestionRepository, CustomEndUserFormRepository customEndUserFormRepository) {
        super(customEndUserQuestionRepository);
        this.customEndUserQuestionRepository = customEndUserQuestionRepository;
        this.customEndUserFormRepository = customEndUserFormRepository;
    }

    public void createQuestions(List<CreateCustomEndUserQuestionDto> questionRequests, Long customEndUserFormId, String userReference) throws UnProcessableOperationException {

        CustomEndUserForm customEndUserForm = getCustomEndUserForm(customEndUserFormId);

        if (!customEndUserForm.getEndUserQueue().getEndUser().getUserReference().equals(userReference)) {
            throw new UnauthorizedUserException("Cannot create question for form belonging to another user.");
        }

        createQuestions(questionRequests, customEndUserForm);
    }

    public List<CustomEndUserQuestionDto> getQuestionsInAForm(Long customEndUserFormId, String userReference) throws UnProcessableOperationException {

        CustomEndUserForm customEndUserForm = getCustomEndUserForm(customEndUserFormId);

        if (!customEndUserForm.getEndUserQueue().getEndUser().getUserReference().equals(userReference)) {
            throw new UnauthorizedUserException("Cannot get questions for form belonging to another user.");
        }

        return toCustomEndUserQuestionDtoList(customEndUserForm.getCustomEndUserQuestions());
    }

    public void updateQuestion(Long questionId, CreateCustomEndUserQuestionDto updatedQuestionBody, String userReference) throws UnProcessableOperationException, SystemServiceException {

        CustomEndUserQuestion customEndUserQuestion = getQuestionById(questionId);

        if (!customEndUserQuestion.getCustomEndUserForm().getEndUserQueue().getEndUser().getUserReference().equals(userReference)) {
            throw new UnauthorizedUserException("Cannot update question for form belonging to another user.");
        }

        customEndUserQuestion.setCustomEndUserAnswerType(toCustomEndUserAnswerType(updatedQuestionBody.getAnswerType()));
        customEndUserQuestion.setMaxAnswerLength(updatedQuestionBody.getMaxAnswerLength());
        customEndUserQuestion.setMinAnswerLength(updatedQuestionBody.getMinAnswerLength());
        customEndUserQuestion.setQuestion(updatedQuestionBody.getQuestion());
        customEndUserQuestion.setEntityStatus(toEntityStatus(updatedQuestionBody.getStatus()));

        save(customEndUserQuestion);

    }

    public CustomEndUserQuestion getQuestionById(Long questionId) throws UnProcessableOperationException {

        return customEndUserQuestionRepository.findById(questionId).
                orElseThrow(() -> new UnProcessableOperationException(String.format("No question with id: %s found", questionId)));
    }


    void createQuestions(List<CreateCustomEndUserQuestionDto> questionRequests, CustomEndUserForm customEndUserForm) {

        List<CustomEndUserQuestion> questions = new ArrayList<>();

        questionRequests.forEach(qr -> questions.add(
                CustomEndUserQuestion.builder()
                        .question(qr.getQuestion())
                        .customEndUserAnswerType(toCustomEndUserAnswerType(qr.getAnswerType()))
                        .customEndUserForm(customEndUserForm)
                        .maxAnswerLength(qr.getMaxAnswerLength())
                        .minAnswerLength(qr.getMinAnswerLength())
                        .build()));

        customEndUserQuestionRepository.saveAll(questions);

    }

    private CustomEndUserForm getCustomEndUserForm(Long customEndUserFormId) throws UnProcessableOperationException {
        return customEndUserFormRepository.findById(customEndUserFormId).
                orElseThrow(() -> new UnProcessableOperationException(String.format("No form found for %s", customEndUserFormId)));
    }

    private List<CustomEndUserQuestionDto> toCustomEndUserQuestionDtoList(List<CustomEndUserQuestion> customEndUserQuestions) {

        List<CustomEndUserQuestionDto> customEndUserQuestionDtos = new ArrayList<>();

        customEndUserQuestions.forEach(question -> customEndUserQuestionDtos.add(new CustomEndUserQuestionDto(question)));

        return customEndUserQuestionDtos;
    }
}

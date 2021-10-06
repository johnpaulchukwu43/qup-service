package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.qup.service.entities.CustomEndUserAnswer;
import com.jworks.qup.service.entities.CustomEndUserForm;
import com.jworks.qup.service.entities.CustomEndUserQuestion;
import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.exceptions.CustomEndUserFormException;
import com.jworks.qup.service.models.CustomEndUserAnswerDto;
import com.jworks.qup.service.models.CustomEndUserAnswerEncloser;
import com.jworks.qup.service.repositories.CustomEndUserAnswerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


/**
 * @author Johnpaul Chukwu.
 * @since 1/09/2021
 */

@Slf4j
@Service
public class CustomEndUserAnswerService extends ServiceBluePrintImpl<CustomEndUserAnswer, CustomEndUserAnswer> {

    private final CustomEndUserAnswerRepository customEndUserAnswerRepository;

    private final CustomEndUserAnswerValidationService customEndUserAnswerValidationService;

    private final EndUserService endUserService;

    public CustomEndUserAnswerService(CustomEndUserAnswerRepository customEndUserAnswerRepository, CustomEndUserAnswerValidationService customEndUserAnswerValidationService, EndUserService endUserService) {
        super(customEndUserAnswerRepository);
        this.customEndUserAnswerRepository = customEndUserAnswerRepository;
        this.customEndUserAnswerValidationService = customEndUserAnswerValidationService;
        this.endUserService = endUserService;
    }


    void submitAnswersToQuestionsInForm(@Valid CustomEndUserAnswerEncloser customEndUserAnswerEncloser, CustomEndUserForm customEndUserForm) throws NotFoundRestApiException, CustomEndUserFormException {

        if (customEndUserAnswerEncloser == null || customEndUserForm == null)
            throw new IllegalArgumentException("Unexpected null parameter value for customEndUserAnswerEncloser and customEndUserForm");

        HashMap<CustomEndUserQuestion, CustomEndUserAnswerDto> expectedQuestionAndProvidedQuestionAnswerMap = new HashMap<>();

        List<CustomEndUserAnswerDto.ValidationError> errorList = new ArrayList<>();

        List<CustomEndUserAnswerDto> answers = customEndUserAnswerEncloser.getUserQuestionAnswerList();


        EndUser userProvidingAnswers = endUserService.getUserByUserReference(customEndUserAnswerEncloser.getUserReferenceOfAnswerProvider());

        List<CustomEndUserQuestion> questions = customEndUserForm.getCustomEndUserQuestions();

        questions.forEach(question -> {
            boolean foundExpectedQuestion = false;

            for (CustomEndUserAnswerDto answer : answers) {
                if (question.getId().equals(answer.getQuestionId())) {
                    expectedQuestionAndProvidedQuestionAnswerMap.putIfAbsent(question, answer);
                    foundExpectedQuestion = true;
                    break;
                }
            }

            if (!foundExpectedQuestion && question.isRequired()) {
                errorList.add(
                        CustomEndUserAnswerDto.ValidationError.builder()
                                .errorMessage(String.format("Answer not provided for required question: %s", question.getQuestion()))
                                .questionId(question.getId())
                                .build()
                );
            }
        });


        if (!errorList.isEmpty())
            throw new CustomEndUserFormException("Error ! Missing answer(s), for required question (s).", errorList);

        List<CustomEndUserAnswer> customEndUserAnswers = new ArrayList<>();


        questions.forEach(question -> {

            CustomEndUserAnswerDto answerDto = expectedQuestionAndProvidedQuestionAnswerMap.get(question);

            Optional<CustomEndUserAnswerDto.ValidationError> optionalValidationError = customEndUserAnswerValidationService.checkAnswerIsValidForQuestion(answerDto, question);

            if (optionalValidationError.isPresent()) {
                errorList.add(optionalValidationError.get());
            } else customEndUserAnswers.add(
                    buildAnswerEntity(userProvidingAnswers, question, answerDto.getAnswer())
            );

        });

        if (!errorList.isEmpty())
            throw new CustomEndUserFormException("Validation error for answers provided. please review answers.", errorList);


        customEndUserAnswerRepository.saveAll(customEndUserAnswers);


    }


    private CustomEndUserAnswer buildAnswerEntity(EndUser endUser, CustomEndUserQuestion customEndUserQuestion, String answer) {
        return CustomEndUserAnswer.builder()
                .endUser(endUser)
                .customEndUserQuestion(customEndUserQuestion)
                .answer(answer)
                .build();
    }


}

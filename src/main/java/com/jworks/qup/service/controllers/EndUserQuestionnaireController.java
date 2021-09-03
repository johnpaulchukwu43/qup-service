package com.jworks.qup.service.controllers;


import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.models.ApiResponseDto;
import com.jworks.app.commons.utils.ApiUtil;
import com.jworks.app.commons.utils.RestConstants;
import com.jworks.qup.service.models.*;
import com.jworks.qup.service.services.CustomEndUserFormService;
import com.jworks.qup.service.services.CustomEndUserQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Johnpaul Chukwu.
 * @since 23/08/2021
 */

@Slf4j
@RestController
@RequestMapping(
        value = RestConstants.API_V1_PREFIX + "/questionnaire",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class EndUserQuestionnaireController {


    private final CustomEndUserFormService customEndUserFormService;

    private final CustomEndUserQuestionService customEndUserQuestionService;


    @PostMapping("forms/{queueId}")
    public ResponseEntity<ApiResponseDto> createForm(@Valid @RequestBody CreateCustomEndUserFormDto createCustomEndUserFormDto,
                                                     @PathVariable Long queueId) throws SystemServiceException, NotFoundRestApiException {

        CustomEndUserFormDto form = customEndUserFormService.createForm(createCustomEndUserFormDto, queueId, ApiUtil.getLoggedInUser());

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "Form created successfully.", form);

    }


    @GetMapping("forms")
    public ResponseEntity<ApiResponseDto> getForms(@RequestParam(name = "queueId") Long queueId) throws SystemServiceException, NotFoundRestApiException {

        List<CustomEndUserFormDto> formsBelongingToQueue = customEndUserFormService.getFormsBelongingToQueue(queueId, ApiUtil.getLoggedInUser());

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "User form(s) found.", formsBelongingToQueue);
    }

    @GetMapping("forms/{formCode}")
    public ResponseEntity<ApiResponseDto> getFormByCode(@PathVariable String formCode) throws SystemServiceException, UnProcessableOperationException {

        CustomEndUserFormDto customEndUserFormDto = customEndUserFormService.getFormByCode(formCode, ApiUtil.getLoggedInUser());

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "User form found.", customEndUserFormDto);
    }

    @PutMapping("forms/{formId}")
    public ResponseEntity<ApiResponseDto> updateForm(@Valid @RequestBody UpdateCustomEndUserFormDto updateCustomEndUserFormDto,
                                                     @PathVariable Long formId) throws SystemServiceException, UnProcessableOperationException {

        customEndUserFormService.updateForm(updateCustomEndUserFormDto, formId, ApiUtil.getLoggedInUser());

        return ApiUtil.updated("form");
    }

    @PostMapping("forms/{formId}/questions")
    public ResponseEntity<ApiResponseDto> addToQuestionsToForm(@Valid @RequestBody List<CreateCustomEndUserQuestionDto> questionRequests, @PathVariable Long formId) throws SystemServiceException, UnProcessableOperationException {

        customEndUserQuestionService.createQuestions(questionRequests, formId, ApiUtil.getLoggedInUser());
        return ApiUtil.created("questions");

    }

    @GetMapping("forms/{formId}/questions")
    public ResponseEntity<ApiResponseDto> getQuestionsInForm(@PathVariable Long formId) throws SystemServiceException, UnProcessableOperationException {

        List<CustomEndUserQuestionDto> questionsInForm = customEndUserQuestionService.getQuestionsInAForm(formId, ApiUtil.getLoggedInUser());

        Map<String, List<CustomEndUserQuestionDto>> response = new HashMap<>();

        response.put("questionsInForm", questionsInForm);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "Question(s) in form found.", response);

    }

    @PutMapping("forms/questions/{questionId}")
    public ResponseEntity<ApiResponseDto> updateQuestionInForm(@Valid @RequestBody CreateCustomEndUserQuestionDto createCustomEndUserQuestionDto, @PathVariable Long questionId) throws SystemServiceException, UnProcessableOperationException {

        customEndUserQuestionService.updateQuestion(questionId, createCustomEndUserQuestionDto, ApiUtil.getLoggedInUser());

        return ApiUtil.updated("question");

    }

    @PutMapping("forms/{formId}/answers")
    public ResponseEntity<ApiResponseDto> answerFormQuestions(@Valid @RequestBody CustomEndUserAnswerDto.Answers answersDto,
                                                              @PathVariable Long formId) throws SystemServiceException, UnProcessableOperationException, NotFoundRestApiException {

        answersDto.setUserReferenceOfAnswerProvider(ApiUtil.getLoggedInUser());

        customEndUserFormService.answerFormQuestions(answersDto, formId);

        return ApiUtil.updated("form with answers.");
    }


}

package com.jworks.qup.service.controllers;

import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.models.ApiResponseDto;
import com.jworks.app.commons.models.PageOutput;
import com.jworks.app.commons.utils.ApiUtil;
import com.jworks.app.commons.utils.RestConstants;
import com.jworks.qup.service.models.*;
import com.jworks.qup.service.services.EndUserQueueAndCustomFormService;
import com.jworks.qup.service.services.EndUserQueueBusinessService;
import com.jworks.qup.service.services.EndUserQueueService;
import com.jworks.qup.service.utils.HasAuthority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.jworks.app.commons.utils.AppUtil.fromPaginationRequest;

/**
 * @author Johnpaul Chukwu.
 * @since 18/12/2020
 */

@Slf4j
@RestController
@RequestMapping(
        value = RestConstants.API_V1_PREFIX + "/queues",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class EndUserQueueController {


    private final EndUserQueueService endUserQueueService;

    private final EndUserQueueBusinessService endUserQueueBusinessService;

    private final EndUserQueueAndCustomFormService endUserQueueAndCustomFormService;

    @PostMapping
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> createQueue(@Validated @RequestBody CreateEndUserQueueDto createEndUserQueueDto) throws SystemServiceException, NotFoundRestApiException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        EndUserQueueDto endUserQueue = endUserQueueService.createQueue(createEndUserQueueDto, loggedInUserReference);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "Successfully created queue.", endUserQueue);
    }

    @GetMapping("{userReference}")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> getQueueBelongingToUser(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                                  @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                                  @RequestParam(name = "queueStatus", required = false) String queueStatus,
                                                                  @RequestParam(name = "queueCode", required = false) String queueCode,
                                                                  @RequestParam(name = "queuePurpose", required = false) String queuePurpose,
                                                                  @RequestParam(name = "createdOnStartDate", required = false) String createdOnStartDate,
                                                                  @RequestParam(name = "createdOnEndDate", required = false) String createdOnEndDate,
                                                                  @RequestParam(name = "expiryStartDate", required = false) String expiryStartDate,
                                                                  @RequestParam(name = "expiryEndDate", required = false) String expiryEndDate,
                                                                  @PathVariable String userReference) throws SystemServiceException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        if(!loggedInUserReference.equalsIgnoreCase(userReference)) throw new UnauthorizedUserException("Cannot access queues belonging to another user. Confirm the userReference passed is yours.");

        ClientSearchQueueDto clientSearchQueueDto = ClientSearchQueueDto.builder()
                .createdOnStartDate(createdOnStartDate)
                .createdOnEndDate(createdOnEndDate)
                .expiryStartDate(expiryStartDate)
                .expiryEndDate(expiryEndDate)
                .queueStatus(queueStatus)
                .queueCode(queueCode)
                .queuePurpose(queuePurpose)
                .userReference(userReference)
                .build();


        PageOutput<EndUserQueueDto> queuesBelongingToUser = endUserQueueService.getQueueBelongingToUser(clientSearchQueueDto, fromPaginationRequest(page, size));

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success,"Queue records found",queuesBelongingToUser);

    }

    @PutMapping("{queueId}")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> updateQueue(@Validated @RequestBody UpdateEndUserQueueDto updateEndUserQueueDto, @PathVariable Long queueId) throws SystemServiceException, NotFoundRestApiException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        endUserQueueService.updateQueueDetails(queueId,updateEndUserQueueDto, loggedInUserReference);

        return ApiUtil.updated("Queue");

    }

    @PutMapping("assign-business")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> assignBusinessToQueue(@Validated @RequestBody AssignBusinessToQueueDto assignBusinessToQueueDto) throws SystemServiceException, UnProcessableOperationException, NotFoundRestApiException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        endUserQueueBusinessService.attachBusinessToQueue(assignBusinessToQueueDto, loggedInUserReference);

        return ApiUtil.updated("Queue");

    }


    @PutMapping("toggle-custom-form-required-option")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> toggleIsCustomFormRequired(@Validated @RequestBody ToggleCustomFormRequirementDto toggleCustomFormRequirementDto) throws SystemServiceException, UnProcessableOperationException, NotFoundRestApiException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        endUserQueueAndCustomFormService.updateIsFormRequiredOption(toggleCustomFormRequirementDto, loggedInUserReference);

        return ApiUtil.updated("Queue form required option.");

    }

    @PutMapping("assign-default-custom-form")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> assignDefaultCustomForm(@Validated @RequestBody AssignDefaultCustomFormToQueueDto assignDefaultCustomFormToQueueDto) throws SystemServiceException, UnProcessableOperationException, NotFoundRestApiException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        endUserQueueAndCustomFormService.setAsFormDefaultForQueue(assignDefaultCustomFormToQueueDto, loggedInUserReference);

        return ApiUtil.updated("Queue Custom form default.");

    }

    @PutMapping("{queueId}/change-status")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> updateQueueStatus(@Validated @RequestBody EndUserQueueStatusDto endUserQueueStatusDto, @PathVariable Long queueId) throws SystemServiceException, NotFoundRestApiException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        endUserQueueService.changeQueueStatus(queueId,endUserQueueStatusDto, loggedInUserReference);

        return ApiUtil.updated("Queue status");

    }
}

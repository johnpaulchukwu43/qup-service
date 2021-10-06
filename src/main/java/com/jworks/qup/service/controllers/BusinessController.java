package com.jworks.qup.service.controllers;

import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.models.ApiResponseDto;
import com.jworks.app.commons.utils.ApiUtil;
import com.jworks.app.commons.utils.RestConstants;
import com.jworks.qup.service.models.BusinessDto;
import com.jworks.qup.service.models.CreateBusinessDto;
import com.jworks.qup.service.services.BusinessService;
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

import java.util.List;

/**
 * @author Johnpaul Chukwu.
 * @since 18/12/2020
 */

@Slf4j
@RestController
@RequestMapping(
        value = RestConstants.API_V1_PREFIX + "/businesses",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class BusinessController {


    private final BusinessService businessService;

    @PostMapping
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> createBusiness(@Validated @RequestBody CreateBusinessDto createBusinessDto) throws SystemServiceException, NotFoundRestApiException, UnProcessableOperationException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        BusinessDto business = businessService.createBusiness(createBusinessDto, loggedInUserReference);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "Successfully created business.", business);
    }


    @GetMapping("{userReference}")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> getBusinessBelongingToUser(@PathVariable String userReference) throws NotFoundRestApiException, SystemServiceException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        if (!loggedInUserReference.equalsIgnoreCase(userReference))
            throw new UnauthorizedUserException("Cannot access business belonging to another user. Confirm you are logged in as expected user");

        List<BusinessDto> businessBelongingToUser = businessService.getBusinessBelongingToUser(userReference);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "Found business", businessBelongingToUser);
    }
}

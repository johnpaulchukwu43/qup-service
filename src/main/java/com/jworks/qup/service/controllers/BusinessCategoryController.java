package com.jworks.qup.service.controllers;

import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.models.ApiResponseDto;
import com.jworks.app.commons.utils.ApiUtil;
import com.jworks.app.commons.utils.RestConstants;
import com.jworks.qup.service.models.BusinessCategoryDto;
import com.jworks.qup.service.models.BusinessDto;
import com.jworks.qup.service.services.BusinessCategoryService;
import com.jworks.qup.service.utils.HasAuthority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */

@Slf4j
@RestController
@RequestMapping(
        value = RestConstants.API_V1_PREFIX + "/businessCategories",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class BusinessCategoryController {


    private final BusinessCategoryService businessCategoryService;

    @PostMapping
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> createBusinessCategory(@Validated @RequestBody BusinessCategoryDto businessCategoryDto) throws SystemServiceException, NotFoundRestApiException, UnProcessableOperationException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        BusinessCategoryDto businessCategory = businessCategoryService.createBusinessCategory(businessCategoryDto, loggedInUserReference);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "Successfully created business category.", businessCategory);
    }
}

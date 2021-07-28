package com.jworks.qup.service.controllers;

import com.jworks.app.commons.exceptions.BadRequestException;
import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.models.ApiResponseDto;
import com.jworks.app.commons.models.AuthenticationResponse;
import com.jworks.app.commons.utils.ApiUtil;
import com.jworks.app.commons.utils.RestConstants;
import com.jworks.qup.service.models.EndUserLoginDto;
import com.jworks.qup.service.models.EndUserOnboardRequestDto;
import com.jworks.qup.service.models.EndUserVerifyDto;
import com.jworks.qup.service.services.EndUserAuthenticationService;
import com.jworks.qup.service.services.EndUserOnBoardService;
import com.jworks.qup.service.services.EndUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Johnpaul Chukwu.
 * @since 18/12/2020
 */

@Slf4j
@RestController
@RequestMapping(
        value = RestConstants.API_V1_PREFIX + "/user",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class EndUserController {


    private final EndUserOnBoardService endUserOnBoardService;
    private final EndUserService endUserService;
    private final EndUserAuthenticationService endUserAuthenticationService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto> createEndUserRequest(@Validated @RequestBody EndUserOnboardRequestDto endUserOnboardRequestDto) throws SystemServiceException, NotFoundRestApiException {

        String userReference = endUserOnBoardService.performUserOnboardProcess(endUserOnboardRequestDto);

        Map<String, String> response = new HashMap<>();
        response.put("userReference", userReference);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "User onboard request", response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponseDto> verifyEndUser(@Validated @RequestBody EndUserVerifyDto endUserVerifyDto) throws SystemServiceException, NotFoundRestApiException, UnProcessableOperationException {

         endUserOnBoardService.performUserVerification(endUserVerifyDto);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "User verification complete.");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponseDto> authenticateUser(@Validated @RequestBody EndUserLoginDto endUserLoginDto) throws  BadRequestException {
        AuthenticationResponse authenticationResponse = endUserAuthenticationService.authenticateUser(endUserLoginDto);
        return ApiUtil.authenticated(authenticationResponse);
    }

}

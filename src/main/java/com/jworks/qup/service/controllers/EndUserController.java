package com.jworks.qup.service.controllers;

import com.jworks.app.commons.exceptions.BadRequestException;
import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.models.ApiResponseDto;
import com.jworks.app.commons.models.AuthenticationResponse;
import com.jworks.app.commons.models.PasswordResetDto;
import com.jworks.app.commons.utils.ApiUtil;
import com.jworks.app.commons.utils.RestConstants;
import com.jworks.qup.service.models.EndUserLoginDto;
import com.jworks.qup.service.models.EndUserOnboardRequestDto;
import com.jworks.qup.service.models.EndUserVerifyDto;
import com.jworks.qup.service.services.EndUserAuthenticationService;
import com.jworks.qup.service.services.EndUserOnBoardService;
import com.jworks.qup.service.services.EndUserService;
import com.jworks.qup.service.utils.HasAuthority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Johnpaul Chukwu.
 * @since 18/12/2020
 */

@Slf4j
@RestController
@RequestMapping(
        value = RestConstants.API_V1_PREFIX + "/users",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class EndUserController {


    private final EndUserOnBoardService endUserOnBoardService;
    private final EndUserService endUserService;
    private final EndUserAuthenticationService endUserAuthenticationService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto> createEndUserRequest(@Validated @RequestBody EndUserOnboardRequestDto endUserOnboardRequestDto) throws SystemServiceException {

        String userReference = endUserOnBoardService.performUserOnboardProcess(endUserOnboardRequestDto);

        Map<String, String> response = new HashMap<>();
        response.put("userReference", userReference);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "User onboard request", response);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponseDto> verifyEndUser(@Validated @RequestBody EndUserVerifyDto endUserVerifyDto) throws SystemServiceException, UnProcessableOperationException {

         endUserOnBoardService.performUserVerification(endUserVerifyDto);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "User verification complete.");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponseDto> authenticateUser(@Validated @RequestBody EndUserLoginDto endUserLoginDto) throws  BadRequestException {
        AuthenticationResponse authenticationResponse = endUserAuthenticationService.authenticateUser(endUserLoginDto);
        return ApiUtil.authenticated(authenticationResponse);
    }

    @PutMapping("/reset-password")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> updateUserPassword(@Valid @RequestBody PasswordResetDto passwordResetDto) throws SystemServiceException, NotFoundRestApiException {
        String userReference = ApiUtil.getLoggedInUser();

        endUserService.resetUserPassword(passwordResetDto,userReference);

        String whatWasUpdated = String.format("password for user with reference: %s.", userReference);

        return ApiUtil.updated(whatWasUpdated);

    }

}

package com.jworks.qup.service.controllers;

import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.models.ApiResponseDto;
import com.jworks.app.commons.models.PageOutput;
import com.jworks.app.commons.utils.ApiUtil;
import com.jworks.app.commons.utils.RestConstants;
import com.jworks.qup.service.models.ClientSearchReservationDto;
import com.jworks.qup.service.models.CreateReservationDto;
import com.jworks.qup.service.models.EndUserReservationDto;
import com.jworks.qup.service.models.EndUserReservationStatusDto;
import com.jworks.qup.service.services.EndUserReservationService;
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

import java.util.HashMap;
import java.util.Map;

import static com.jworks.app.commons.utils.AppUtil.fromPaginationRequest;
import static com.jworks.qup.service.enums.GetReservationAction.BY_USER_OWNER;

/**
 * @author Johnpaul Chukwu.
 * @since 18/12/2020
 */

@Slf4j
@RestController
@RequestMapping(
        value = RestConstants.API_V1_PREFIX + "/reservations",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class EndUserReservationController {


    private final EndUserReservationService endUserReservationService;


    @PostMapping
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> createReservation(@Validated @RequestBody CreateReservationDto createReservationDto) throws SystemServiceException, NotFoundRestApiException, UnProcessableOperationException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        String reservationCode = endUserReservationService.createReservation(createReservationDto, loggedInUserReference);

        Map response = new HashMap<String,String>();
        response.put("reservationCode",reservationCode);

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success, "Successfully created reservation.", response);
    }

    @GetMapping("/user/{userReference}")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> getReservationsBelongingToUser(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                                  @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                                  @RequestParam(name = "reservationStatus", required = false) String reservationStatus,
                                                                  @RequestParam(name = "queueCode", required = false) String queueCode,
                                                                  @RequestParam(name = "reservationCode", required = false) String reservationCode,
                                                                  @RequestParam(name = "createdOnEndDate", required = false) String createdOnEndDate,
                                                                  @RequestParam(name = "createdOnStartDate", required = false) String createdOnStartDate,
                                                                         @PathVariable String userReference) throws SystemServiceException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        if(!loggedInUserReference.equalsIgnoreCase(userReference)) throw new UnauthorizedUserException("Cannot access reservations belonging to another user. Confirm the userReference passed is yours.");

        ClientSearchReservationDto clientSearchReservationDto = ClientSearchReservationDto.builder()
                .queueCode(queueCode)
                .reservationCode(reservationCode)
                .createdOnEndDate(createdOnEndDate)
                .createdOnStartDate(createdOnStartDate)
                .reservationStatus(reservationStatus)
                .build();


        PageOutput<EndUserReservationDto> reservationsBelongingToUser = endUserReservationService.getReservations(clientSearchReservationDto, loggedInUserReference, BY_USER_OWNER,fromPaginationRequest(page, size));

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success,"Your reservation(s) found",reservationsBelongingToUser);

    }

    @GetMapping("/queue/{queueId}")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> getReservationsBelongingToUser(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                                         @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
                                                                         @RequestParam(name = "reservationStatus", required = false) String reservationStatus,
                                                                         @RequestParam(name = "queueCode", required = false) String queueCode,
                                                                         @RequestParam(name = "reservationCode", required = false) String reservationCode,
                                                                         @RequestParam(name = "createdOnEndDate", required = false) String createdOnEndDate,
                                                                         @RequestParam(name = "createdOnStartDate", required = false) String createdOnStartDate,
                                                                         @PathVariable long queueId) throws SystemServiceException, NotFoundRestApiException {

        String loggedInUserReference = ApiUtil.getLoggedInUser();

        ClientSearchReservationDto clientSearchReservationDto = ClientSearchReservationDto.builder()
                .queueCode(queueCode)
                .reservationCode(reservationCode)
                .createdOnEndDate(createdOnEndDate)
                .createdOnStartDate(createdOnStartDate)
                .reservationStatus(reservationStatus)
                .build();


        PageOutput<EndUserReservationDto> reservationsAssociatedWithQueue = endUserReservationService.getReservationByQueue(clientSearchReservationDto, loggedInUserReference, queueId,fromPaginationRequest(page, size));

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success,"Reservation(s) associated with queue found",reservationsAssociatedWithQueue);

    }

    @PutMapping("{reservationCode}/change-status")
    @PreAuthorize(HasAuthority.OF_USER_OR_ADMIN)
    public ResponseEntity<ApiResponseDto> updateReservationStatus(@Validated @RequestBody EndUserReservationStatusDto endUserReservationStatusDto, @PathVariable String reservationCode) throws SystemServiceException, NotFoundRestApiException {

         endUserReservationService.updateReservationStatus(endUserReservationStatusDto, reservationCode);

        return ApiUtil.updated("Reservation status");
    }

}

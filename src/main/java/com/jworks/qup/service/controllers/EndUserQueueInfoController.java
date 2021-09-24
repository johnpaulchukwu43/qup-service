package com.jworks.qup.service.controllers;


import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.models.ApiResponseDto;
import com.jworks.app.commons.models.PageOutput;
import com.jworks.app.commons.utils.ApiUtil;
import com.jworks.app.commons.utils.RestConstants;
import com.jworks.qup.service.models.*;
import com.jworks.qup.service.services.EndUserQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import static com.jworks.app.commons.utils.AppUtil.fromPaginationRequest;

/**
 * @author Johnpaul Chukwu.
 * @since 23/08/2021
 */

@Slf4j
@RestController
@RequestMapping(
        value = RestConstants.API_V1_PREFIX + "/queues-info",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
public class EndUserQueueInfoController {


    private final EndUserQueueService endUserQueueService;

    @GetMapping
    public ResponseEntity<ApiResponseDto> searchForQueueInfo(@RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
                                                                  @RequestParam(name = "size", required = false, defaultValue = "50") Integer size,
                                                                  @RequestParam(name = "queueName", required = false) String queueName,
                                                                  @RequestParam(name = "queueCode", required = false) String queueCode,
                                                                  @RequestParam(name = "businessName", required = false) String businessName) throws SystemServiceException {

        ClientSearchQueueInfo clientSearchQueueInfo = ClientSearchQueueInfo.builder()
                .queueCode(queueCode)
                .businessName(businessName)
                .queueName(queueName)
                .build();

        PageOutput<EndUserQueueInfo> queueInfos = endUserQueueService.searchForQueueInfo(clientSearchQueueInfo, fromPaginationRequest(page, size));

        return ApiUtil.response(HttpStatus.OK, ApiResponseDto.Status.success,"Queue records found",queueInfos);

    }


}

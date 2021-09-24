package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.app.commons.models.NotificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

import static com.jworks.app.commons.models.NotificationStatus.ERROR;


@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderResponseData<T> {
    private NotificationStatus status;
    private String statusMessage;
    private String code;
    private BigDecimal price;
    private T data;

    public static ProviderResponseData defaultErrorResponse() {
        ProviderResponseData responseData = new ProviderResponseData();
        responseData.setStatus(ERROR);
        responseData.setStatusMessage("Error occurred while processing request. Please try again later.");
        return responseData;
    }

    public static ProviderResponseData defaultErrorResponse(String statusMessage) {
        ProviderResponseData responseData = new ProviderResponseData();
        responseData.setStatus(ERROR);
        responseData.setStatusMessage(statusMessage);
        return responseData;
    }

    public static ProviderResponseData notFoundResponse() {
        ProviderResponseData responseData = defaultErrorResponse();
        responseData.setStatusMessage("Reference not found");
        return responseData;
    }
}

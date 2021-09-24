package com.jworks.qup.service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jworks.app.commons.models.CallResponse;
import com.jworks.app.commons.models.SendGridErrorData;
import com.jworks.app.commons.models.SendGridFieldError;
import com.jworks.qup.service.models.ProviderResponseData;
import com.sendgrid.helpers.mail.Mail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Collections;

import static com.jworks.app.commons.models.NotificationStatus.COMPLETED;
import static com.jworks.app.commons.models.NotificationStatus.FAILED;
import static com.jworks.app.commons.utils.AppUtil.logDataExchange;
import static com.jworks.app.commons.utils.AppUtil.stripExtraSlash;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendGridApiService {
    private final RestTemplate serviceRestTemplate;
    private final ObjectMapper objectMapper;
    @Value("${sendgrid.base-url}")
    private String baseUrl;
    @Value("${sendgrid.api-key}")
    private String apiKey;

    public ProviderResponseData sendEmail(Mail mailDto) {
        String targetUrl = String.format("%s/mail/send", baseUrl);
        targetUrl = stripExtraSlash(targetUrl);

        return processSendGridResponse(makeApiCall(targetUrl, mailDto, HttpMethod.POST));
    }


    private ProviderResponseData processSendGridResponse(CallResponse response) {
        ProviderResponseData.ProviderResponseDataBuilder providerResponseDataBuilder = ProviderResponseData.builder();
        if (!response.isSuccessful()) {
            String errorString = response.getErrorString();
            try {
                SendGridErrorData sendGridErrorData = objectMapper.readValue(errorString, SendGridErrorData.class);
                if (sendGridErrorData != null && !sendGridErrorData.getErrors().isEmpty()) {
                    SendGridFieldError topError = sendGridErrorData.getErrors().get(0);
                    return providerResponseDataBuilder
                            .status(FAILED)
                            .statusMessage("failed.")
                            .build();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                return ProviderResponseData.defaultErrorResponse(e.getMessage());
            }

            return ProviderResponseData.defaultErrorResponse();
        }


        return ProviderResponseData.builder()
                .price(BigDecimal.ZERO)
                .status(COMPLETED)
                .statusMessage("Ok. Completed.")
                .build();
    }

    private CallResponse makeApiCall(String targetUrl, Object requestData, HttpMethod method) {
        CallResponse result = new CallResponse();
        result.setSuccessful(false);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add("Authorization", String.format("Bearer %s", apiKey));

        try {
            ResponseEntity<Object> response = serviceRestTemplate.exchange(targetUrl, method,
                    new HttpEntity<>(requestData, requestHeaders), Object.class);

            result.setSuccessful(true);
            result.setHttpStatus(response.getStatusCode());
            result.setData(response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                result.setSuccessful(false);
            }

            logDataExchange(log, targetUrl, requestData, response);
            return result;
        } catch (ResourceAccessException ex) {
            Throwable cause = ex.getRootCause();
            if (cause instanceof ConnectException) {
                result.setHttpStatus(HttpStatus.SERVICE_UNAVAILABLE);
                result.setErrorString(cause.getMessage());
                return result;
            } else if (cause instanceof SocketTimeoutException) {
                result.setHttpStatus(HttpStatus.GATEWAY_TIMEOUT);
                result.setErrorString(cause.getMessage());
                return result;
            } else {
                result.setErrorString(ex.getMessage());
            }
            return result;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            result.setHttpStatus(e.getStatusCode());
            result.setData(e.getResponseBodyAsString());
            result.setErrorString(e.getResponseBodyAsString());
            log.error(e.getMessage());
            log.error(e.getResponseBodyAsString());
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            result.setErrorString(e.getMessage());
            return result;
        }
    }
}

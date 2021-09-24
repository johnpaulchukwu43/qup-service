package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.app.commons.models.NotificationType;
import com.jworks.qup.service.entities.BaseEndUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 27/04/2021
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EndUserNotificationDto<T extends BaseEndUser> implements Serializable {

    private T user;

    private String recipient;

    private String sender;

    private String senderAddress;

    private String subject;

    private String message;

    private String reference;

    private NotificationType notificationType;

    private String providerTemplateCode;
}
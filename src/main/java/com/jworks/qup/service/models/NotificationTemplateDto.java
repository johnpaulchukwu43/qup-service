package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.app.commons.models.NotificationType;
import com.jworks.app.commons.validator.ValidEnum;
import com.jworks.qup.service.entities.BaseEndUser;
import com.jworks.qup.service.entities.EndUserNotificationTemplate;
import com.jworks.qup.service.entities.EndUserOnboardNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 27/04/2021
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotificationTemplateDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ValidEnum(enumClass = EndUserNotificationTemplateCode.class, message = "invalid value for user notification code.")
    private String code;


    @ValidEnum(enumClass = NotificationType.class, message = "invalid value for notification type")
    private String notificationType;

    @NotBlank(message = "Sender is required")
    @Length(min = 2, max = 255, message = "Must be between 2 and 255 characters")
    private String sender;

    private String senderAddress;

    @Length(max = 255, message = "Maximum characters allowed is 255")
    private String subject;

    @NotBlank(message = "Body is required")
    @Length(min = 2, message = "Body must have at least 2 characters")
    private String body;

    @Length(min = 2, message = "Provider template code must have at least 2 characters")
    private String providerTemplateCode;


    public static NotificationTemplateDto toNotificationTemplateDto(EndUserNotificationTemplate endUserNotificationTemplate) {

        NotificationTemplateDto notificationTemplateDto = new NotificationTemplateDto();

        BeanUtils.copyProperties(endUserNotificationTemplate, notificationTemplateDto);

        notificationTemplateDto.setNotificationType(endUserNotificationTemplate.getNotificationType().name());
        notificationTemplateDto.setCode(endUserNotificationTemplate.getCode().getName());

        return notificationTemplateDto;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MetaData implements Serializable {

        private String firstName;

        private String lastName;

        private String userReference;

        private String verificationCode;

        public static MetaData toMetaData(Object info) {

            MetaData metaData = new MetaData();

            if (info instanceof BaseEndUser) {
                BaseEndUser baseEndUser = (BaseEndUser) info;
                metaData.setFirstName(baseEndUser.getFirstName());
                metaData.setFirstName(baseEndUser.getLastName());
                metaData.setUserReference(baseEndUser.getUserReference());
            }

            if (info instanceof EndUserOnboardNotification) {
                EndUserOnboardNotification endUserOnboardNotification = (EndUserOnboardNotification) info;
                metaData.setVerificationCode(endUserOnboardNotification.getEndUserOnboardRequest().getVerificationCode());
            }

            BeanUtils.copyProperties(info, metaData);

            return metaData;
        }


    }


}

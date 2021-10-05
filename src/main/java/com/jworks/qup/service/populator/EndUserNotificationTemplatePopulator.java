package com.jworks.qup.service.populator;

import com.jworks.app.commons.models.NotificationType;
import com.jworks.qup.service.entities.EndUserNotificationTemplate;
import com.jworks.qup.service.models.EndUserNotificationTemplateCode;
import com.jworks.qup.service.services.EndUserNotificationTemplateService;
import com.jworks.qup.service.utils.QupServiceConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Johnpaul Chukwu.
 * @since 08/09/2021
 */


@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "qup.service", name = "initialize-default-notification-templates")
public class EndUserNotificationTemplatePopulator {

    private final EndUserNotificationTemplateService endUserNotificationTemplateService;

    @Value("${qup.service.email.sender.email-address}")
    protected String senderEmailAddress;
    @Value("${qup.service.email.sender.name}")
    protected String senderName;
    @Value("${sendgrid.user-notification-template-code}")
    private String providerTemplateCode;

    @PostConstruct
    public void init() {
        createNotificationTemplate();
    }


    private void createNotificationTemplate() {
        createEmailNotificationTemplate();
        createSMSNotificationTemplate();
    }

    private void createSMSNotificationTemplate() {
        //todo work on sms template
    }

    private void createEmailNotificationTemplate() {

        log.info("creating user email notification templates");

        List<EndUserNotificationTemplate> endUserNotificationTemplates = new ArrayList<>();


        EndUserNotificationTemplate.EndUserNotificationTemplateBuilder<?, ?> templateBuilder = EndUserNotificationTemplate.builder()
                .notificationType(NotificationType.EMAIL)
                .providerTemplateCode(providerTemplateCode)
                .sender(senderName)
                .senderAddress(senderEmailAddress);


        EndUserNotificationTemplate verificationEmailTemplate = templateBuilder
                .code(EndUserNotificationTemplateCode.USER_EMAIL_VERIFICATION_NOTIFICATION)
                .subject(QupServiceConstants.USER_EMAIL_VERIFICATION_NOTIFICATION_TITLE)
                .body("Hello {{firstName}}, {{lastName}} ! \n" +
                        "\n" +
                        "You are receiving this mail because you requested to join us on QUP. \n" +
                        "\n" +
                        "Enter {{verificationCode}} as verification code to complete your sign up process. \n" +
                        "\n" +
                        "If you did not authorize this sign up process. Please ignore."
                )
                .build();

        endUserNotificationTemplates.add(verificationEmailTemplate);

        endUserNotificationTemplateService.createNotificationTemplates(endUserNotificationTemplates);
    }


}

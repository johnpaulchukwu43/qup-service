package com.jworks.qup.service.services;

import com.jworks.app.commons.models.NotificationType;
import com.jworks.qup.service.entities.BaseNotification;
import com.jworks.qup.service.models.ProviderResponseData;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class EndUserEmailNotificationDispatcherService {
    private final SendGridApiService apiService;
    @Value("${sendgrid.default-sender-address}")
    private String defaultSenderAddress;

    public NotificationType getNotificationType() {
        return NotificationType.EMAIL;
    }


    public <T extends BaseNotification> ProviderResponseData sendMessage(T userNotification) {
        String senderAddress = userNotification.getSenderAddress();

        Email from = new Email(senderAddress, userNotification.getSender());
        Email recipient = new Email(userNotification.getRecipient());
        Content content = new Content("text/html", userNotification.getMessage());
        Personalization personalization = new Personalization();
        personalization.setSubject(userNotification.getSubject());
        personalization.addTo(recipient);

        Mail mail = new Mail();

        if (StringUtils.isNotBlank(userNotification.getProviderTemplateCode())) {
            personalization.addDynamicTemplateData("alert_message", userNotification.getMessage());
            personalization.addDynamicTemplateData("subject", userNotification.getSubject());
            mail.setTemplateId(userNotification.getProviderTemplateCode());
        }

        mail.setFrom(from);
        mail.setSubject(userNotification.getSubject());
        mail.addContent(content);
        mail.addPersonalization(personalization);

        return apiService.sendEmail(mail);


    }

}

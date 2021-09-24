package com.jworks.qup.service.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jworks.app.commons.models.NotificationType;
import com.jworks.qup.service.models.EndUserNotificationTemplateCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "end_user_notification_templates", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"notification_type", "code"}, name = "UQ_NOTIFICATION_TEMPLATE_TYPE")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndUserNotificationTemplate extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, length = 80)
    private EndUserNotificationTemplateCode code;

    @Column(name = "sender", nullable = false, columnDefinition = "VARCHAR(255)")
    private String sender;

    @Column(name = "sender_address", columnDefinition = "VARCHAR(255)")
    private String senderAddress;

    @Column(name = "subject", columnDefinition = "VARCHAR(255)")
    private String subject;

    @Column(name = "body", columnDefinition = "LONGTEXT")
    private String body;

    @Column(name = "provider_template_code", columnDefinition = "VARCHAR(255)")
    private String providerTemplateCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 20)
    private NotificationType notificationType;
}

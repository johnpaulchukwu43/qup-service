package com.jworks.qup.service.entities;

import com.jworks.app.commons.models.NotificationStatus;
import com.jworks.app.commons.models.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;


/**
 * @author Johnpaul Chukwu.
 * @since 07/05/2021
 */


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class BaseNotification extends BaseEntity {

    @Column(name = "recipient", nullable = false, updatable = false, columnDefinition = "TEXT")
    private String recipient;

    @Column(name = "sender", nullable = false, updatable = false, columnDefinition = "VARCHAR(255)")
    private String sender;

    @Column(name = "sender_address", updatable = false, columnDefinition = "VARCHAR(255)")
    private String senderAddress;

    @Column(name = "subject", updatable = false, columnDefinition = "TEXT")
    private String subject;

    @Column(name = "message", updatable = false, columnDefinition = "LONGTEXT")
    private String message;

    @ColumnDefault("'PENDING'")
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_status", nullable = false, length = 20)
    private NotificationStatus notificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_notification_status")
    private NotificationStatus providerNotificationStatus;


    @Column(name = "notification_attempts", nullable = false, columnDefinition = "BIGINT")
    @ColumnDefault("0")
    private Long notificationAttempts;

    @Column(name = "reference", unique = true, nullable = false, updatable = false, columnDefinition = "VARCHAR(255)")
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 20)
    private NotificationType notificationType;


    @Column(name = "provider_template_code", columnDefinition = "VARCHAR(255)")
    private String providerTemplateCode;

    @Column(name = "completed_on", columnDefinition = "DATETIME")
    private Timestamp completedOn;

    @Column(name = "is_being_sent", nullable = false)
    private boolean isBeingSent;

}

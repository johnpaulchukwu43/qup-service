package com.jworks.qup.service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;


/**
 * @author Johnpaul Chukwu.
 * @since 07/05/2021
 */

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "end_user_notifications", indexes = {
        @Index(name = "NOTIFICATION_STATUS_IDX", columnList = "notification_status"),
        @Index(name = "NOTIFICATION_TYPE_IDX", columnList = "notification_type"),
        @Index(name = "IS_BEING_SENT_IDX", columnList = "is_being_sent"),
        @Index(name = "REFERENCE_IDX", columnList = "reference")})
public class EndUserOnboardNotification extends BaseNotification {

    @ManyToOne
    @JoinColumn(name = "end_user_onboard_request_id", referencedColumnName = "id")
    private EndUserOnboardRequest endUserOnboardRequest;
}

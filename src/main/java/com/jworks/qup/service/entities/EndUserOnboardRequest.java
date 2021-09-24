package com.jworks.qup.service.entities;

import com.jworks.qup.service.enums.EndUserOnBoardVerificationOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "end_user_onboard_requests")
public class EndUserOnboardRequest extends BaseEndUser implements Serializable {

    private static final long serialVersionUID = 1L;


    @Column(name = "verification_code", nullable = false, length = 70, unique = true, updatable = false)
    private String verificationCode;

    @Column(name = "is_verification_complete",nullable = false)
    @ColumnDefault("0")
    private boolean isVerificationComplete;

    @Column(name = "is_notification_sent",nullable = false)
    @ColumnDefault("0")
    private boolean isNotificationSent;

    @Column(name = "verification_option", nullable = false,length = 20)
    @Enumerated(EnumType.STRING)
    private EndUserOnBoardVerificationOption endUserOnBoardVerificationOption;

}

package com.jworks.qup.service.entities;

import com.jworks.qup.service.enums.EndUserOnBoardVerificationOption;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Entity
@Data
@Builder
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "end_user_onboard_requests")
public class EndUserOnboardRequest extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "verification_code", nullable = false, length = 70, unique = true, updatable = false)
    private String verificationCode;

    @Column(name = "email_address", unique = true)
    private String emailAddress;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @Column(name = "is_verification_complete",nullable = false)
    @ColumnDefault("0")
    private boolean isVerificationComplete;

    @Column(name = "is_notification_sent",nullable = false)
    @ColumnDefault("0")
    private boolean isNotificationSent;

    @Column(name = "verification_option", nullable = false,length = 20)
    @Enumerated(EnumType.STRING)
    private EndUserOnBoardVerificationOption endUserOnBoardVerificationOption;

    @Column(name = "user_reference", nullable = false, length = 70, unique = true, updatable = false)
    private String userReference;

}

package com.jworks.qup.service.models;

import com.jworks.app.commons.validator.ConditionalInputSanitizer;
import com.jworks.app.commons.validator.ValidEnum;
import com.jworks.qup.service.entities.BaseEntity;
import com.jworks.qup.service.entities.Business;
import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.entities.EndUserPoolConfig;
import com.jworks.qup.service.enums.EndUserOnBoardVerificationOption;
import com.jworks.qup.service.enums.QueueLocationType;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.enums.QueueStatus;
import lombok.*;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEndUserQueueDto implements Serializable {

    private static final long serialVersionUID = 1L;


    @ConditionalInputSanitizer(min = 2, max= 70, message = "queueLocationValue must be between 2-70 characters long.")
    private String queueName;

    @PositiveOrZero(message = "maxNumberOfUsersOnQueue cannot be less than o")
    private Long maxNumberOfUsersOnQueue;

    @Future
    @NotNull(message =  "maxNumberOfUsersInPool is required")
    private LocalDateTime expirationDate;

    @ValidEnum(enumClass = QueueLocationType.class)
    private String queueLocationType;

    @ConditionalInputSanitizer(min = 10, message = "queueLocationValue must be at least 10 characters long.")
    private String queueLocationValue;

    @PositiveOrZero(message = "maxNumberOfUsersInPool cannot be less than o")
    private Long maxNumberOfUsersInPool;

}

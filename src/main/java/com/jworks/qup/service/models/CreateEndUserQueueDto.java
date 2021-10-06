package com.jworks.qup.service.models;

import com.jworks.app.commons.validator.ConditionalInputSanitizer;
import com.jworks.app.commons.validator.ValidEnum;
import com.jworks.qup.service.enums.QueueLocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;

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


    @ConditionalInputSanitizer(min = 2, max = 70)
    private String queueName;

    @PositiveOrZero(message = "maxNumberOfUsersOnQueue cannot be less than o")
    private Long maxNumberOfUsersOnQueue;

    @NotNull(message = "expirationDateTime is required")
    private String expirationDateTime;

    @ValidEnum(enumClass = QueueLocationType.class)
    private String queueLocationType;

    @ConditionalInputSanitizer(min = 10)
    private String queueLocationValue;

    @PositiveOrZero(message = "maxNumberOfUsersInPool cannot be less than 0")
    private Long maxNumberOfUsersInPool;

}

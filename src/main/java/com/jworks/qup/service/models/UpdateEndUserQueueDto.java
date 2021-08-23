package com.jworks.qup.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
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
public class UpdateEndUserQueueDto implements Serializable {

    private static final long serialVersionUID = 1L;


    @PositiveOrZero(message = "maxNumberOfUsersOnQueue cannot be less than 0")
    private Long maxNumberOfUsersOnQueue;

    @Future
    @NotNull(message =  "maxNumberOfUsersInPool is required")
    private LocalDateTime expirationDate;

    @PositiveOrZero(message = "maxNumberOfUsersInPool cannot be less than 0")
    private Long maxNumberOfUsersInPool;

}

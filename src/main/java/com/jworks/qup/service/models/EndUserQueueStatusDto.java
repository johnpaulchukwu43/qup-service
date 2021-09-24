package com.jworks.qup.service.models;

import com.jworks.app.commons.validator.ValidEnum;
import com.jworks.qup.service.enums.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndUserQueueStatusDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ValidEnum(enumClass = QueueStatus.class)
    private String queueStatus;

}

package com.jworks.qup.service.models;

import com.jworks.qup.service.entities.BaseEntity;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.enums.QueueLocationType;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.enums.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Indexed;

import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Data
@Builder
@Indexed
@NoArgsConstructor
@AllArgsConstructor
public class EndUserQueueInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private QueueStatus queueStatus;

    private String queueCode;

    private QueuePurpose queuePurpose;

    private QueueLocationType queueLocationType;

    private String queueLocationValue;

    public EndUserQueueInfo(EndUserQueue endUserQueue){
        BeanUtils.copyProperties(endUserQueue,this);
    }

}

package com.jworks.qup.service.models;

import com.jworks.qup.service.entities.EndUserPoolConfig;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.enums.QueueLocationType;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.enums.QueueStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndUserQueueDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Long capacity;

    private LocalDateTime expirationDate;

    private QueueStatus queueStatus;

    private String queueCode;

    private QueuePurpose queuePurpose;

    private QueueLocationType queueLocationType;

    private String queueLocationValue;

    private EndUserPoolConfig poolConfig;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private BusinessDto business;

    public EndUserQueueDto (EndUserQueue endUserQueue){
        BeanUtils.copyProperties(endUserQueue,this);
        this.setBusiness(new BusinessDto(endUserQueue.getBusiness()));
    }

}

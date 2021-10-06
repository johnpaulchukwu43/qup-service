package com.jworks.qup.service.enums;

import com.jworks.app.commons.exceptions.BadRequestException;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum QueueStatus {
    PENDING,ACTIVE,INACTIVE,RESET;

    public static QueueStatus toQueueStatus(String queueStatus) throws BadRequestException {
        for (QueueStatus q: QueueStatus.values()) {
            if (q.name().equalsIgnoreCase(queueStatus)) {
                return q;
            }
        }

        throw new BadRequestException(String.format("Unrecognized value: %s as queue status", queueStatus));
    }
}

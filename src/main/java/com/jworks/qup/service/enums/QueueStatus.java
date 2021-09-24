package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum QueueStatus {
    PENDING,ACTIVE,INACTIVE,RESET;

    public static QueueStatus toQueueStatus(String queueStatus){
        for (QueueStatus q: QueueStatus.values()) {
            if (q.name().equalsIgnoreCase(queueStatus)) {
                return q;
            }
        }

        throw new IllegalArgumentException(String.format("Unrecognized value: %s as queue status", queueStatus));
    }
}

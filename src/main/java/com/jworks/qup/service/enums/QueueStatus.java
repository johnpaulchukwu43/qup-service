package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum QueueStatus {
    PENDING,ACTIVE,INACTIVE,RESET;

    public static boolean contains(String queueStatus){
        for (QueueStatus q: QueueStatus.values()) {
            if (q.name().equals(queueStatus)) {
                return true;
            }
        }
        return false;
    }
}

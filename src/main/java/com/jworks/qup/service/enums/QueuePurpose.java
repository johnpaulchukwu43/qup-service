package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum QueuePurpose {
    PERSONAL, BUSINESS;

    public static boolean contains(String queuePurpose){
        for (QueuePurpose q: QueuePurpose.values()) {
            if (q.name().equals(queuePurpose)) {
                return true;
            }
        }
        return false;
    }
}

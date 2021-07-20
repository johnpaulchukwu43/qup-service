package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum QueueLocationType {
    PHYSICAL, VIRTUAL;

    public static boolean contains(String queueLocationType){
        for (QueueLocationType q: QueueLocationType.values()) {
            if (q.name().equals(queueLocationType)) {
                return true;
            }
        }
        return false;
    }
}

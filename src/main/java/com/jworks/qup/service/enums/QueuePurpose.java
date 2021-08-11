package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum QueuePurpose {
    PERSONAL, BUSINESS;

    public static QueuePurpose toQueuePurpose(String queuePurpose){
        for (QueuePurpose qp: QueuePurpose.values()) {
            if (qp.name().equalsIgnoreCase(queuePurpose)) {
                return qp;
            }
        }
        throw new IllegalArgumentException(String.format("Unrecognized value: %s as queue purpose", queuePurpose));
    }
}

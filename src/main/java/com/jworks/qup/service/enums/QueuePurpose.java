package com.jworks.qup.service.enums;

import com.jworks.app.commons.exceptions.BadRequestException;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum QueuePurpose {
    PERSONAL, BUSINESS;

    public static QueuePurpose toQueuePurpose(String queuePurpose) throws BadRequestException {
        for (QueuePurpose qp: QueuePurpose.values()) {
            if (qp.name().equalsIgnoreCase(queuePurpose)) {
                return qp;
            }
        }
        throw new BadRequestException(String.format("Unrecognized value: %s as queue purpose", queuePurpose));
    }
}

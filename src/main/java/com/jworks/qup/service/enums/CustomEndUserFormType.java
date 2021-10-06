package com.jworks.qup.service.enums;

import com.jworks.app.commons.exceptions.BadRequestException;

public enum CustomEndUserFormType {

    PRE_QUEUE_FORM, POST_QUEUE_FORM;

    public static boolean contains(String queueLocationType) {
        for (CustomEndUserFormType q : CustomEndUserFormType.values()) {
            if (q.name().equals(queueLocationType)) {
                return true;
            }
        }
        return false;
    }

    public static CustomEndUserFormType toCustomEndUserFormType(String customEndUserFormType) throws BadRequestException {
        for (CustomEndUserFormType ft : CustomEndUserFormType.values()) {
            if (ft.name().equalsIgnoreCase(customEndUserFormType)) {
                return ft;
            }
        }
        throw new BadRequestException(String.format("Unrecognized value: %s as form type", customEndUserFormType));
    }
}

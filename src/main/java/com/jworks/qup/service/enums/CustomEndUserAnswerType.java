package com.jworks.qup.service.enums;


public enum CustomEndUserAnswerType {

    NUMERIC, ALPHA_NUMERIC, EMAIL, DATE, TIME;

    public static boolean contains(String queueLocationType) {
        for (CustomEndUserAnswerType q : CustomEndUserAnswerType.values()) {
            if (q.name().equals(queueLocationType)) {
                return true;
            }
        }
        return false;
    }

    public static CustomEndUserAnswerType toCustomEndUserAnswerType(String customEndUserAnswerType) {
        for (CustomEndUserAnswerType customEndUserAnswerType1 : CustomEndUserAnswerType.values()) {
            if (customEndUserAnswerType1.name().equalsIgnoreCase(customEndUserAnswerType)) {
                return customEndUserAnswerType1;
            }
        }
        throw new IllegalArgumentException(String.format("Unrecognized value: %s as form type", customEndUserAnswerType));
    }
}

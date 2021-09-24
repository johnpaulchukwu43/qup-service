package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum EndUserOnBoardVerificationOption {
    PHONE_NUMBER,EMAIL;

    public static boolean contains(String queueStatus){
        for (EndUserOnBoardVerificationOption q: EndUserOnBoardVerificationOption.values()) {
            if (q.name().equals(queueStatus)) {
                return true;
            }
        }
        return false;
    }
}

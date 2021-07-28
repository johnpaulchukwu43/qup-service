package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum LoginType {
    PHONE_NUMBER,EMAIL;

    public static boolean contains(String queueStatus){
        for (LoginType q: LoginType.values()) {
            if (q.name().equals(queueStatus)) {
                return true;
            }
        }
        return false;
    }
}

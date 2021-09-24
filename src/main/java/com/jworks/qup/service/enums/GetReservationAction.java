package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum GetReservationAction {
    BY_USER_OWNER, BY_QUEUE_OWNER;

    public static boolean contains(String queueStatus){
        for (GetReservationAction q: GetReservationAction.values()) {
            if (q.name().equals(queueStatus)) {
                return true;
            }
        }
        return false;
    }
}

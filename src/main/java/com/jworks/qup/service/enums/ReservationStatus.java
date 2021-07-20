package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum ReservationStatus {
    PENDING,ACTIVE,INACTIVE,RESET;

    public static boolean contains(String queueStatus){
        for (ReservationStatus q: ReservationStatus.values()) {
            if (q.name().equals(queueStatus)) {
                return true;
            }
        }
        return false;
    }
}

package com.jworks.qup.service.enums;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */
public enum ReservationStatus {
    WAITING,IN_PROGRESS,COMPLETED,INVALIDATED;

    public static boolean contains(String queueStatus){
        for (ReservationStatus q: ReservationStatus.values()) {
            if (q.name().equals(queueStatus)) {
                return true;
            }
        }
        return false;
    }

    public static ReservationStatus toReservationStatus(String reservationStatus){
        for (ReservationStatus rs: ReservationStatus.values()) {
            if (rs.name().equalsIgnoreCase(reservationStatus)) {
                return rs;
            }
        }
        throw new IllegalArgumentException(String.format("Unrecognized value: %s as queue status", reservationStatus));
    }
}

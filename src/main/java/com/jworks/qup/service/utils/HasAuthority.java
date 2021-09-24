package com.jworks.qup.service.utils;

/**
 * @author Johnpaul Chukwu.
 * @since 16/04/2021
 */
public interface HasAuthority {

    String HAS_AUTH_PREFIX = "hasAuthority('";
    String HAS_ANY_ROLE_PREFIX = "hasAnyRole('";
    String HAS_AUTH_SUFFIX = "')";

    String OF_ADMIN = "hasAuthority('ADMIN')";
    String OF_USER = "hasAuthority('USER')";
    String OF_USER_OR_ADMIN = "hasAnyAuthority('USER','ADMIN')";
}

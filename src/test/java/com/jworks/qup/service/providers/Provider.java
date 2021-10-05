package com.jworks.qup.service.providers;

import com.github.javafaker.Faker;

/**
 * @author bodmas
 * @param <T>
 * @since Oct 2, 2021.
 */
public interface Provider<T> {
    Faker faker = new Faker();

    T provide();
}

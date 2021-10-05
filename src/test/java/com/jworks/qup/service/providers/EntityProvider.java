package com.jworks.qup.service.providers;

import com.github.javafaker.Faker;
import com.jworks.app.commons.repositories.BaseRepository;

/**
 * @author bodmas
 * @param <T>
 * @since Oct 2, 2021.
 */
public interface EntityProvider<T> {
    Faker faker = new Faker();

    T provide();
    BaseRepository<T> getRepository();
    default void preSave(T t) {
    }

    default T save(T t) {
        preSave(t);
        return getRepository().save(t);
    }

    default T provideAndSave() {
        return save(provide());
    }
}

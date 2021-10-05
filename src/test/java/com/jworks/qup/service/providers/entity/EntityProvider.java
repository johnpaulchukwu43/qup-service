package com.jworks.qup.service.providers.entity;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.providers.Provider;

/**
 * @author bodmas
 * @param <T>
 * @since Oct 2, 2021.
 */
public interface EntityProvider<T> extends Provider<T> {
    BaseRepository<T> getRepository();

    default T save(T t) {
        return getRepository().save(t);
    }

    default T provideAndSave() {
        return save(provide());
    }
}

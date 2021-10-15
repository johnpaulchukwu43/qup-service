package com.jworks.qup.service.providers;

import com.github.javafaker.Faker;
import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.BaseEntity;
import com.jworks.qup.service.framework.CommonOperations;
import java.util.Collection;

/**
 * @author bodmas
 * @param <T>
 * @since Oct 2, 2021.
 */
public interface EntityProvider<T extends BaseEntity> {
    Faker faker = new Faker(CommonOperations.getRandom());

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
    }// TODO: Rename methods to deleteMembers, saveMembers

    // TODO: Consider renaming to deleteFully
    // Delete the entity having this id and all references to it.
    // Returns true if the delete was entirely successful without errors, false otherwise
    default boolean delete(T t) {
        return deleteSingle(t) && postDelete(t); // The ordering is important here.
    }

    // Returns true if the delete was entirely successful without errors, false otherwise
    default boolean postDelete(T t) {
        return true;
    }

    // Returns true if the delete was entirely successful without errors, false otherwise
    default boolean deleteSingle(T t) {
        try {
            getRepository().delete(t);
            return true;
        } catch (RuntimeException e) {
            System.out.println(e.getMessage() + "\nError while deleting entity "
                               + t.getId() + " from provider " + getClass().getSimpleName());
            return false;
        }
    }

    // Returns true if the delete was entirely successful without errors, false otherwise
    default boolean deleteAll() {
        return getRepository().findAll().stream().map(this::delete).reduce(Boolean.TRUE, Boolean::logicalAnd); // Avoids short-circuiting
    }

    // Returns true if the delete was entirely successful without errors, false otherwise
    default boolean deleteAll(Collection<T> tList) {
        return tList.stream().map(this::delete).reduce(Boolean.TRUE, Boolean::logicalAnd); // Avoids short-circuiting
    }
}

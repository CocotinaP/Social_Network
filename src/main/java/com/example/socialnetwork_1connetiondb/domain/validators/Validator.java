package com.example.socialnetwork_1connetiondb.domain.validators;

/**
 * Validator interface.
 * @param <T> entity to be validate
 */
public interface Validator<T> {
    /**
     * Validate an entity.
     * @param entity - the entity to be validated.
     * @throws ValidationException - if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}

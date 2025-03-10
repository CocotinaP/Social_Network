package com.example.socialnetwork_1connetiondb.repository.memory;

import com.example.socialnetwork_1connetiondb.domain.Entity;
import com.example.socialnetwork_1connetiondb.domain.validators.Validator;
import com.example.socialnetwork_1connetiondb.repository.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * CRUD operations in memory repository
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    protected Validator<E> validator;
    protected Map<ID, E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<ID, E>();
    }

    @Override
    public Optional<E> findOne(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID nu poate fi null!\n");
        }
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll(){
        return entities.values();
    }

    @Override
    public Optional<E> save (E entity){
        if (entity == null) {
            throw new IllegalArgumentException("Entity nu poate fi null!\n");
        }
        validator.validate(entity);
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity));
    }

    @Override
    public Optional<E> delete(ID id){
        if (id == null) {
            throw new IllegalArgumentException("ID nu poate fi null!\n");
        }
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update (E entity){
        if (entity == null) {
            throw new IllegalArgumentException("Entity nu poate fi null!\n");
        }
        validator.validate(entity);
        if (entities.containsKey(entity.getId())){
            entities.put(entity.getId(), entity);
            return  Optional.empty();
        }
        return Optional.of(entity);
    }
}

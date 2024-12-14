package com.example.social_network.repository.database;

import com.example.social_network.domain.Entity;
import com.example.social_network.domain.validators.Validator;
import com.example.social_network.repository.memory.InMemoryRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public abstract class AbstractDatabaseRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    protected String tableName;
    protected DataBaseAccess dataBaseAccess;

    public AbstractDatabaseRepository(Validator<E> validator, DataBaseAccess dataBaseAccess, String tableName){
        super(validator);
        this.dataBaseAccess = dataBaseAccess;
        this.tableName = tableName;
    }

    /**
     * Load dates from database.
     */
    protected abstract void loadData();

    /**
     *
     * @return all records from database.
     */
    protected abstract Iterable<E> findAll_DB();

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> entity = super.delete(id);
        if (entity.isPresent()) {
            try{
                PreparedStatement statement = dataBaseAccess.createStatement("DELETE FROM " + tableName + " WHERE \"Id\" = ?;");
                statement.setLong(1, (Long) id);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return entity;
    }
}

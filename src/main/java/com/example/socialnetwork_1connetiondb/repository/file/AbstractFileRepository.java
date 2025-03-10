package com.example.socialnetwork_1connetiondb.repository.file;

import com.example.socialnetwork_1connetiondb.domain.Entity;
import com.example.socialnetwork_1connetiondb.domain.validators.Validator;
import com.example.socialnetwork_1connetiondb.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Optional;

/**
 * CRUD operations file repository
 * @param <ID> - type E must have an attribute of type ID
 * @param <E> -  type of entities saved in repository
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String fileName;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadFromFile();
    }

    /**
     *
     * @param line - that to be transformed into entity
     * @return the corresponding entity for the line
     */
    public abstract E lineToEntity(String line);

    /**
     *
     * @param entity - that to be transformed into line
     * @return the corresponding line for the entity
     */
    public abstract String entityToLine(E entity);

    @Override
    public Optional<E> save (E entity) {
        Optional<E> e = super.save(entity);
        if (e.isEmpty()) {
            appendToFile(entity);
        }
        return e;
    }

    @Override
    public Optional<E> delete(ID id){
        Optional<E> e = super.delete(id);
        if (e.isPresent()) {
            writeToFile();
        }
        return e;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> e = super.update(entity);
        if (e.isPresent()) {
            writeToFile();
        }
        return e;
    }

    /**
     * Write all enities into file from memory.
     */
    private void writeToFile(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            entities.values().forEach(entity -> {
                try {
                    writer.write(entityToLine(entity));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load all entities into memory from file.
     */
    private void loadFromFile(){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                E entity = lineToEntity(line);
                entities.put(entity.getId(), entity);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param entity - the entity to be appended to the end of the file.
     */
    private void appendToFile(E entity){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(entityToLine(entity));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

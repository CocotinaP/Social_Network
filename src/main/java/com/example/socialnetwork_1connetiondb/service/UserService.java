package com.example.socialnetwork_1connetiondb.service;

import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.repository.Repository;
import com.example.socialnetwork_1connetiondb.utils.Utils;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User Service.
 */
public class UserService {
    private static Utils utils = new Utils();
    private Repository repository;

    /**
     * Constructor.
     * @param repository - user repository
     */
    public UserService(Repository repository){
        this.repository = repository;
    }

    /**
     * Save an user.
     * @param firstName - the first name of the user to be saved
     * @param lastName - the last name of the user to be saved
     * @param userName - the username of the user to be saved
     * @param password - the password of the user to be saved
     * @throws ValidationException
     *                 if the entity is not valid
     * @throws IllegalArgumentException
     *                  if the given entity is null.
     */
    public void save(String firstName, String lastName, String userName, String password) {
        User user = new User(firstName, lastName, userName, password);
        findAll().forEach(u -> {
            if (Objects.equals(u.getUserName(), user.getUserName())){
                throw new ValidationException("UserName indisponibil!\n");
            }
        });
        //Long id = utils.generateLongId(findAll());
        //user.setId(id);
        repository.save(user);
    }

    /**
     * Delete un user by id.
     * @param id - the id of the user to be deleted must not be null
     * @return the entity with the specified id
     * @throws IllegalArgumentException if the given id is null.
     * @throws ServiceException if there are no users with the specified id
     */
    public User delete(Long id) {
        Optional<User> utilizator = (Optional<User>) repository.findOne(id);
        if (utilizator.isEmpty()) {
            throw new ServiceException("Utilizator inexistent!\n");
        }

        //Stergem utilizatorul.
        utilizator = (Optional<User>) repository.delete(id);
        return utilizator.get();
    }

    /**
     * Update one user.
     * @param user
     *          user must not be null
     * @return an {@code Optional}
     *                - null if the entity was updated
     *                - otherwise  returns the entity  - (e.g. id does not exist).
     * @throws IllegalArgumentException
     *             if the given user is null.
     * @throws ValidationException
     *             if the user is not valid.
     */
    public Optional<User> update(User user){
        Optional<User> updatedUser = repository.update(user);
        return updatedUser;
    }

    /**
     * Find one user by name.
     * @param name - the name of the user to be returned
     * @return an {@code Optional} encapsulating the entity with the given name
     */
    public Optional<User> findOneByName(String name){
        AtomicReference<Optional<User>> findUser = new AtomicReference<>(Optional.empty());
        findAll().forEach(user -> {
            if (Objects.equals(name, user.getFirstName() + ' ' + user.getLastName())){
                findUser.set(Optional.of(user));
            }
        });
        return findUser.get();
    }

    /**
     * Find one user by id.
     * @param id -the id of the user to be returned
     *           id must not be null
     * @return an {@code Optional} encapsulating the entity with the give id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    public Optional<User> findOne(Long id){
        return (Optional<User>) repository.findOne(id);
    }

    /**
     * Login an user.
     * @param username - the username of the person to be logged
     * @param password - the password of the person to be logged
     * @return an {@code Optional} encapsulating the logged entity
     * @throws ServiceException if the password is wrong
     */
    public Optional<User> login(String username, String password){
        AtomicReference<Optional<User>> user = new AtomicReference<>(Optional.empty());
        findAll().forEach(u->{
            if (Objects.equals(u.getUserName(), username)){
                if (Objects.equals(u.getPassword(), password)){
                    user.set(Optional.of(u));
                }
                else{
                    throw new ServiceException("Incorrect password!\n");
                }
            }
        });
        return user.get();
    }

    /**
     *
     * @return all users
     */
    public Iterable<User> findAll(){
        return repository.findAll();
    }
}

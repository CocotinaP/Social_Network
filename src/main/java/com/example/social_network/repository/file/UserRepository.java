package com.example.social_network.repository.file;

import com.example.social_network.domain.User;
import com.example.social_network.domain.validators.Validator;

/**
 * CRUD operations user file repository
 */
public class UserRepository extends AbstractFileRepository<Long, User> {

    public UserRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public User lineToEntity(String line) {
        String[] parts = line.split(";");
        User u = new User(parts[1], parts[2], parts[3], parts[4]);
        u.setId(Long.parseLong(parts[0]));
        return u;
    }

    @Override
    public String entityToLine(User entity) {
        String line =  entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName() +
                ";" + entity.getUserName() + ";" + entity.getPassword();
        return line;
    }
}

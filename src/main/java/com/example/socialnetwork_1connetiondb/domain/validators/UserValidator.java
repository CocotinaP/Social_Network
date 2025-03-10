package com.example.socialnetwork_1connetiondb.domain.validators;

import com.example.socialnetwork_1connetiondb.domain.User;

/**
 * The user validation class.
 */
public class UserValidator implements  Validator<User> {

    @Override
    public void validate(User entity) throws ValidationException {
        if (entity.getFirstName().isEmpty() || !entity.getFirstName().matches("[a-zA-Z\\s-]+")){
            throw new ValidationException("Utilizatorul nu este valid!\n");
        }
        if (entity.getLastName().isEmpty() || !entity.getLastName().matches("[a-zA-Z\\s-]+")){
            throw new ValidationException("Utilizatorul nu este valid!\n");
        }
        if (entity.getUserName().isEmpty()){
            throw new ValidationException("UserName invalid!\n");
        }
        if (entity.getPassword().isEmpty()){
            throw new ValidationException("Parola invalida!\n");
        }
    }
}
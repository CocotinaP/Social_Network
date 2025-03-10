package com.example.socialnetwork_1connetiondb.domain.validators;


import com.example.socialnetwork_1connetiondb.domain.FriendshipDTO;

/**
 * The friendship validation class.
 */
public class FriendshipValidator implements Validator<FriendshipDTO> {
    @Override
    public void validate(FriendshipDTO entity) throws ValidationException {
        if (entity.getIdUser1() == null || entity.getIdUser2() == null) {
            throw new ValidationException("Prietenie invalida!\n");
        }
    }
}

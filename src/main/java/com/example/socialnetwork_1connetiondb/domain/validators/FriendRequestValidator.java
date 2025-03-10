package com.example.socialnetwork_1connetiondb.domain.validators;

import com.example.socialnetwork_1connetiondb.domain.FriendRequestDTO;

public class FriendRequestValidator implements Validator<FriendRequestDTO>{
    @Override
    public void validate(FriendRequestDTO entity) throws ValidationException {
        if (entity.getSendingUserId() == null || entity.getReceivingUserId() == null){
            throw new ValidationException("Invalid friend request!\n");
        }
    }
}

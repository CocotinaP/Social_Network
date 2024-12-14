package com.example.social_network.domain.validators;

import com.example.social_network.domain.FriendRequestDTO;

public class FriendRequestValidator implements Validator<FriendRequestDTO>{
    @Override
    public void validate(FriendRequestDTO entity) throws ValidationException {
        if (entity.getSendingUserId() == null || entity.getReceivingUserId() == null){
            throw new ValidationException("Invalid friend request!\n");
        }
    }
}

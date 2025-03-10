package com.example.socialnetwork_1connetiondb.domain.validators;

import com.example.socialnetwork_1connetiondb.domain.NotificationDTO;

public class NotificationValidator implements Validator<NotificationDTO> {

    @Override
    public void validate(NotificationDTO entity) throws ValidationException {
        StringBuilder message = new StringBuilder();

        if (entity.getSendingUserId() == null || entity.getReceivingUserId() == null){
            message.append("One of the user does not exist!");
        }
        if (entity.getMessage().isEmpty()){
            message.append("The notification message can not be empty!");
        }
    }
}

package com.example.socialnetwork_1connetiondb.domain.validators;

import com.example.socialnetwork_1connetiondb.domain.MessageDTO;

public class MessageValidator implements Validator<MessageDTO> {
    @Override
    public void validate(MessageDTO entity) throws ValidationException {
        StringBuilder message = new StringBuilder();

        if (entity.getFromId() == null){
            message.append("Sending user does not exist!\n");
        }

        if (entity.getToId() == null){
            message.append("Recipient user does not exist!\n");
        }

        if (entity.getMessage().isEmpty()){
            message.append("You can not send a empty message!\n");
        }
    }
}

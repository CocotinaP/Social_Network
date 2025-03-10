package com.example.socialnetwork_1connetiondb.service;

import com.example.socialnetwork_1connetiondb.domain.Message;
import com.example.socialnetwork_1connetiondb.domain.MessageDTO;
import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService {
    private Repository messageRepository;
    private Repository userRepository;

    public MessageService(Repository messageRepository, Repository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a message.
     * @param fromUserId - the id of the user who send message
     * @param toUserId - the id of the user who receive message
     * @param message - the message to be sent
     * @return the saved message
     * @throws ServiceException
     *      *      if one of the users does not exist
     * @throws ValidationException
     *      if the entity is not valid
     * @throws IllegalArgumentException
     *      if the given entity is null.
     */
    public Message save(Long fromUserId, Long toUserId, String message){
        Optional<User> fromUser = userRepository.findOne(fromUserId);
        Optional<User> toUser = userRepository.findOne(toUserId);

        if (fromUser.isEmpty() || toUser.isEmpty()){
            throw new ServiceException("One of the user does not exist!\n");
        }

        MessageDTO messageDTO = new MessageDTO(fromUser.get().getId(), toUser.get().getId(), message, LocalDateTime.now());
        messageRepository.save(messageDTO);
        Message message1 = new Message(fromUser.get(), toUser.get(), message, messageDTO.getDate());
        message1.setId(messageDTO.getId());
        return message1;
    }

    /**
     * Save a message.
     * @param fromUserId - the id of the user who send message
     * @param toUserId - the id of the user who receive message
     * @param message - the message to be sent
     * @param replyMessageId - the id oht the message to reply
     * @return the saved message
     * @throws ServiceException
     *      *      if one of the users or message to reply does not exist
     * @throws ValidationException
     *      if the entity is not valid
     * @throws IllegalArgumentException
     *      if the given entity is null.
     */
    public Message save(Long fromUserId, Long toUserId, String message, Long replyMessageId){
        Optional<User> fromUser = userRepository.findOne(fromUserId);
        Optional<User> toUser = userRepository.findOne(toUserId);
        Optional<MessageDTO> replyMessageDTO = messageRepository.findOne(replyMessageId);


        if (fromUser.isEmpty() || toUser.isEmpty()){
            throw new ServiceException("One of the user does not exist!\n");
        }
        if (replyMessageDTO.isEmpty()){
            throw new ServiceException("The replied message does not exist!\n");
        }

        if ((!replyMessageDTO.get().getFromId().equals(fromUser.get().getId()) && !replyMessageDTO.get().getToId().equals(toUser.get().getId()))
                && (!replyMessageDTO.get().getFromId().equals(toUser.get().getId()) && !replyMessageDTO.get().getToId().equals(fromUser.get().getId()))){
            throw new ServiceException("You can reply a message from another conversation!\n");
        }

        Optional<User> u1 = userRepository.findOne(replyMessageDTO.get().getToId());
        Optional<User> u2 = userRepository.findOne(replyMessageDTO.get().getFromId());
        if (u1.isEmpty() || u2.isEmpty()){
            throw new ServiceException("One of the user does not exist!\n");
        }

        Message replyMessage = new Message(u1.get(), u2.get(), replyMessageDTO.get().getMessage(), replyMessageDTO.get().getDate());
        replyMessage.setId(replyMessageDTO.get().getId());

        MessageDTO messageDTO = new MessageDTO(fromUser.get().getId(), toUser.get().getId(), message, LocalDateTime.now(), replyMessageDTO.get().getId());
        messageRepository.save(messageDTO);
        Message message1 = new Message(fromUser.get(), toUser.get(), message, messageDTO.getDate(), replyMessage);
        message1.setId(messageDTO.getId());
        return message1;
    }

    /**
     *
     * @return all messages
     */
    public Iterable<Message> findAll(){
        List<Message> messages = new ArrayList<>();
        Iterable<MessageDTO> messagesDTO = messageRepository.findAll();
        messagesDTO.forEach(messageDTO->{
            Optional<User> fromUser = userRepository.findOne(messageDTO.getFromId());
            Optional<User> toUser = userRepository.findOne(messageDTO.getToId());
            Optional<MessageDTO> replyDTO = messageRepository.findOne(messageDTO.getReplyId());
            Message message;
            if (replyDTO.isEmpty()) {
                message = new Message(fromUser.get(), toUser.get(), messageDTO.getMessage(), messageDTO.getDate());
            }
            else{
                Optional<User> u1 = userRepository.findOne(replyDTO.get().getFromId());
                Optional<User> u2 = userRepository.findOne(replyDTO.get().getToId());
                Message reply = new Message(u1.get(), u2.get(), replyDTO.get().getMessage(), replyDTO.get().getDate());
                reply.setId(replyDTO.get().getId());
                message = new Message(fromUser.get(), toUser.get(), messageDTO.getMessage(), messageDTO.getDate(), reply);
            }
            message.setId(messageDTO.getId());
            messages.add(message);
        });
        return messages;
    }

    /*
    Optional<Message> findOne(Long id){
        Optional<MessageDTO> messageDTO = messageRepository.findOne(id);
        Optional<Message> message;
        if (messageDTO.isEmpty()){
            message = Optional.empty();
        }
        Optional<User> u1 = userRepository.findOne(messageDTO.get().getFromId());
        Optional<User> u2 = userRepository.findOne(messageDTO.get().getToId());

        Message msg = new Message(u1.get(), u2.get(), messageDTO.get().getMessage(), messageDTO.get().getDate());
        msg.setId(messageDTO.get().getId());
        message = Optional.of(msg);

        return  message;
    }*/
}

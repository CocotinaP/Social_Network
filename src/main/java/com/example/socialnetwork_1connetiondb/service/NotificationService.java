package com.example.socialnetwork_1connetiondb.service;

import com.example.socialnetwork_1connetiondb.domain.Notification;
import com.example.socialnetwork_1connetiondb.domain.NotificationDTO;
import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class NotificationService {
    private Repository notificationRepository;
    private Repository userRepository;

    public NotificationService(Repository notificationRepository, Repository userRepository){
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a notification.
     * @param sendingUserId - id of the sending user
     * @param receivingUserId - id of the receiving user
     * @param message - the notification message
     * @return the saved notification
     * @throws ServiceException
     *      if one of the users does not exist
     * @throws ValidationException
     *      if the entity is not valid
     * @throws IllegalArgumentException
     *      if the given entity is null.
     */
    public Notification save(Long sendingUserId, Long receivingUserId, String message){
        Optional<User> sendingUser = userRepository.findOne(sendingUserId);
        Optional<User> receivingUser = userRepository.findOne(receivingUserId);

        if (sendingUser.isEmpty() || receivingUser.isEmpty()){
            throw new ServiceException("One of the user does not exist!");
        }

        NotificationDTO notificationDTO = new NotificationDTO(sendingUserId, receivingUserId, message, false, LocalDateTime.now());
        notificationRepository.save(notificationDTO);
        Notification notification = new Notification(sendingUser.get(), receivingUser.get(), message, notificationDTO.getSeenStatus(), notificationDTO.getDate());
        notification.setId(notificationDTO.getId());
        return notification;
    }

    /**
     * Delete a notification.
     * @param id - the id of the notification to be deleted.
     * @return the removed notification
     * @throws IllegalArgumentException if the given id is null.
     * @throws ServiceException if there are no notification with the specified id
     */
    public Notification delete(Long id){
        Optional<NotificationDTO> notificationDTO = notificationRepository.delete(id);
        if (notificationDTO.isEmpty()){
            throw new ServiceException("The notification does not exist!\n");
        }
        Optional<User> sendingUser = userRepository.findOne(notificationDTO.get().getSendingUserId());
        Optional<User> receivingUser = userRepository.findOne(notificationDTO.get().getReceivingUserId());

        Notification notification = new Notification(sendingUser.get(), receivingUser.get(), notificationDTO.get().getMessage(),
                notificationDTO.get().getSeenStatus(), notificationDTO.get().getDate());
        notification.setId(notificationDTO.get().getId());
        return  notification;
    }

    /**
     * Seen a notification.
     * @param id - the id of the notification to be seen.
     * @return the seen notification.
     */
    public Notification seen(Long id){
        Optional<NotificationDTO> notificationDTO = notificationRepository.findOne(id);
        if (notificationDTO.isEmpty()){
            throw new ServiceException("The notification does not exist!\n");
        }

        NotificationDTO notificationDTO1 = new NotificationDTO(notificationDTO.get().getSendingUserId(),
                notificationDTO.get().getReceivingUserId(), notificationDTO.get().getMessage(),
                true, notificationDTO.get().getDate());
        notificationDTO1.setId(notificationDTO.get().getId());
        notificationRepository.update(notificationDTO1);

        Optional<User> sendingUser = userRepository.findOne(notificationDTO1.getSendingUserId());
        Optional<User> receivingUser = userRepository.findOne(notificationDTO1.getReceivingUserId());

        Notification notification = new Notification(sendingUser.get(), receivingUser.get(),
                notificationDTO1.getMessage(), notificationDTO1.getSeenStatus(), notificationDTO1.getDate());
        notification.setId(notificationDTO1.getId());
        return notification;
    }

    /**
     *
     * @return all notifications.
     */
    public Iterable<Notification> findAll(){
        ArrayList<Notification> notifications = new ArrayList<>();
        Iterable<NotificationDTO> notificationDTOS = notificationRepository.findAll();
        notificationDTOS.forEach(notificationDTO ->{
            Optional<User> sendingUser = userRepository.findOne(notificationDTO.getSendingUserId());
            Optional<User> receivingUser = userRepository.findOne(notificationDTO.getReceivingUserId());
            Notification notification = new Notification(sendingUser.get(), receivingUser.get(),
                    notificationDTO.getMessage(), notificationDTO.getSeenStatus(), notificationDTO.getDate());
            notification.setId(notificationDTO.getId());
            notifications.add(notification);
        });
        return notifications;
    }
}

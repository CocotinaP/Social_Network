package com.example.socialnetwork_1connetiondb.service;

import com.example.socialnetwork_1connetiondb.domain.FriendRequest;
import com.example.socialnetwork_1connetiondb.domain.FriendRequestDTO;
import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.repository.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class FriendRequestService {
    private Repository friendRequestRepository;
    private Repository userRepository;

    public FriendRequestService(Repository friendRequestRepository, Repository userRepository){
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a friend request.
     * @param sendingUserId - the id of the user who send the request.
     * @param receivingUserID - the id of the user whom the request is sent.
     * @return the saved friend request.
     * @throws ServiceException
     *      if one of the users does not exist
     * @throws ValidationException
     *      if the entity is not valid
     * @throws IllegalArgumentException
     *      if the given entity is null.
     */
    public FriendRequest save(Long sendingUserId, Long receivingUserID){
        Optional<User> sendingUser = userRepository.findOne(sendingUserId);
        Optional<User> receivingUser = userRepository.findOne(receivingUserID);

        if (sendingUser.isEmpty() || receivingUser.isEmpty()){
            throw new ServiceException("One of the users does not exist!\n");
        }

        if (sendingUser.get() == receivingUser.get()){
            throw new ServiceException("It is not possible to be friend with yourself!\n");
        }

        FriendRequestDTO friendRequestDTO = new FriendRequestDTO(sendingUser.get().getId(), receivingUser.get().getId(),
                "not accepted", LocalDateTime.now());
        friendRequestRepository.save(friendRequestDTO);
        FriendRequest friendRequest = new FriendRequest(sendingUser.get(), receivingUser.get(), friendRequestDTO.getStatus(), friendRequestDTO.getSubmissionDate());
        friendRequest.setId(friendRequestDTO.getId());
        return friendRequest;
    }

    /**
     * Delete a friend request
     * @param id - the id of the friend request to be deleted.
     * @return the deleted friend request.
     * @throws IllegalArgumentException if the given id is null.
     * @throws ServiceException if there are no friend request with the specified id
     */
    public FriendRequest delete(Long id){
        Optional<FriendRequestDTO> friendRequestDTO = friendRequestRepository.delete(id);
        if (friendRequestDTO.isEmpty()){
            throw new ServiceException("The friend request does not exist!\n");
        }
        Long sendingUserId = friendRequestDTO.get().getSendingUserId();
        Long receivingUserId = friendRequestDTO.get().getReceivingUserId();

        Optional<User> sendingUser = userRepository.findOne(sendingUserId);
        Optional<User> receivingUser = userRepository.findOne(receivingUserId);

        FriendRequest friendRequest = new FriendRequest(sendingUser.get(), receivingUser.get(),
                friendRequestDTO.get().getStatus(), friendRequestDTO.get().getSubmissionDate());
        return friendRequest;
    }

    /**
     * Accept a friend request.
     * @param id - the id of the friend request to be accepted.
     * @return the accepted friend request
     *
     */
    public FriendRequest accept(Long id){
        Optional<FriendRequestDTO> friendRequestDTO = friendRequestRepository.findOne(id);
        if (friendRequestDTO.isEmpty()){
            throw new ServiceException("The friend request does not exist!\n");
        }
        if (Objects.equals(friendRequestDTO.get().getStatus(), "accepted")){
            throw new ServiceException("This friend request has been already accepted!\n");
        }

        FriendRequestDTO newFriendRequest = new FriendRequestDTO(friendRequestDTO.get().getSendingUserId(),
                friendRequestDTO.get().getReceivingUserId(), "accepted", friendRequestDTO.get().getSubmissionDate());

        newFriendRequest.setId(friendRequestDTO.get().getId());
        friendRequestRepository.update(newFriendRequest);

        Optional<User> sendingUser = userRepository.findOne(newFriendRequest.getSendingUserId());
        Optional<User> receivingUser = userRepository.findOne(newFriendRequest.getReceivingUserId());
        FriendRequest friendRequest = new FriendRequest(sendingUser.get(), receivingUser.get(),
                newFriendRequest.getStatus(), newFriendRequest.getSubmissionDate());
        friendRequest.setId(newFriendRequest.getId());
        return friendRequest;
    }

    Iterable<FriendRequest> findAll(){
        ArrayList<FriendRequest> friendRequests = new ArrayList<>();
        Iterable<FriendRequestDTO> friendRequestDTOS = friendRequestRepository.findAll();
        friendRequestDTOS.forEach(friendRequestDTO -> {
            Optional<User> sendingUser = userRepository.findOne(friendRequestDTO.getSendingUserId());
            Optional<User> receivingUser = userRepository.findOne(friendRequestDTO.getReceivingUserId());
            FriendRequest friendRequest = new FriendRequest(sendingUser.get(), receivingUser.get(),
                    friendRequestDTO.getStatus(), friendRequestDTO.getSubmissionDate());
            friendRequest.setId(friendRequestDTO.getId());
            friendRequests.add(friendRequest);
        });
        return  friendRequests;
    }
}

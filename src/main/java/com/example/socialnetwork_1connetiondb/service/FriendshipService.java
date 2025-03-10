package com.example.socialnetwork_1connetiondb.service;

import com.example.socialnetwork_1connetiondb.domain.Friendship;
import com.example.socialnetwork_1connetiondb.domain.FriendshipDTO;
import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.repository.Repository;
import com.example.socialnetwork_1connetiondb.repository.database.FriendshipDatabaseRepository;
import com.example.socialnetwork_1connetiondb.utils.Utils;
import com.example.socialnetwork_1connetiondb.utils.paging.Page;
import com.example.socialnetwork_1connetiondb.utils.paging.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Friendship Service.
 */
public class FriendshipService {
    private FriendshipDatabaseRepository friendshipRepository;
    private Repository userRepository;
    private static Utils utils = new Utils();

    /**
     * Constructor.
     * @param friendshipRepository the friendship repository
     * @param userRepository the user repository
     */
    public FriendshipService(FriendshipDatabaseRepository friendshipRepository, Repository userRepository){
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a friendship.
     * @param idUser1 - the id of the first friend of the friendship to be saved
     * @param idUser2 - the id of the second friend of the firendship to be saved
     * @return the saved friendship
     * @throws ServiceException
     *                  if one of the users does not exist
     * @throws ValidationException
     *                  if the entity is not valid
     * @throws IllegalArgumentException
     *                   if the given entity is null.
     */
    public Friendship save(Long idUser1, Long idUser2) {
        Optional<User> user1 = (Optional<User>) userRepository.findOne(idUser1);
        Optional<User> user2 = (Optional<User>) userRepository.findOne(idUser2);
        if (user1.isEmpty() || user2.isEmpty()){
            throw new ServiceException("Utilizatori inexistenti!\n");
        }

        FriendshipDTO friendshipDTO = new FriendshipDTO(user1.get().getId(), user2.get().getId(), LocalDateTime.now());
        //Long friendshipId = utils.generateLongId(findAll());
        //friendshipDTO.setId(friendshipId);
        friendshipRepository.save(friendshipDTO);
        Friendship friendship = new Friendship(user1.get(), user2.get(), friendshipDTO.getFriendsFrom());
        friendship.setId(friendshipDTO.getId());
        return friendship;
    }

    /**
     * Delete a friendship
     * @param friendshipId - the id of the friendship to be deleted must not be null
     * @return the removed friendship or null if there is no entity with given id
     * @throws IllegalArgumentException if the given id is null.
     * @throws ServiceException if there are no firenships with the specified id
     */
    public Friendship delete(Long friendshipId) {
        Optional<FriendshipDTO> friendshipDTO = (Optional<FriendshipDTO>) friendshipRepository.delete(friendshipId);
        if (friendshipDTO.isEmpty()){
            throw new ServiceException("Prietenie inexistenta!\n");
        }
        Long idUser1 = friendshipDTO.get().getIdUser1();
        Long idUser2 = friendshipDTO.get().getIdUser2();
        Optional<User> user1 = (Optional<User>) userRepository.findOne(idUser1);
        Optional<User> user2 = (Optional<User>) userRepository.findOne(idUser2);
        Friendship friendship = null;
        if (user1.isPresent() && user2.isPresent()) {
            friendship = new Friendship(user1.get(), user2.get(), friendshipDTO.get().getFriendsFrom());
        }
        return friendship;
    }

    /**
     *
     * @return all friendships
     */
    public Iterable<Friendship> findAll(){
        ArrayList<Friendship> friendships = new ArrayList<>();
        Iterable<FriendshipDTO> friendshipsDTO = friendshipRepository.findAll();
        friendshipsDTO.forEach(friendshipDTO -> {
            Optional<User> u1 = (Optional<User>) userRepository.findOne(friendshipDTO.getIdUser1());
            Optional<User> u2 = (Optional<User>) userRepository.findOne(friendshipDTO.getIdUser2());
            Friendship friendship = new Friendship(u1.get(), u2.get(), friendshipDTO.getFriendsFrom());
            friendship.setId(friendshipDTO.getId());
            friendships.add(friendship);
        });
        return friendships;
    }

    public Page<Friendship> findAllOnPage(Long id, Pageable pageable){
        Page<FriendshipDTO> friendshipDTOPage = friendshipRepository.findAllOnPage(id, pageable);
        List<Friendship> friendships = new ArrayList<>();
        Iterable<FriendshipDTO> friendshipDTOS = friendshipDTOPage.getElementsOnPage();
        friendshipDTOS.forEach(friendshipDTO -> {
            Optional<User> u1 = (Optional<User>) userRepository.findOne(friendshipDTO.getIdUser1());
            Optional<User> u2 = (Optional<User>) userRepository.findOne(friendshipDTO.getIdUser2());
            Friendship friendship = new Friendship(u1.get(), u2.get(), friendshipDTO.getFriendsFrom());
            friendship.setId(friendshipDTO.getId());
            friendships.add(friendship);
        });
        Page<Friendship> friendshipPage = new Page<>(friendships, friendshipDTOPage.getTotalNumbersOfElements());
        return friendshipPage;
    }
}


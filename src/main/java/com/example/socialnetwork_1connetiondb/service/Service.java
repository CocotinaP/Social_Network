package com.example.socialnetwork_1connetiondb.service;

import com.example.socialnetwork_1connetiondb.domain.*;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.utils.events.*;
import com.example.socialnetwork_1connetiondb.utils.observer.Observable;
import com.example.socialnetwork_1connetiondb.utils.observer.Observer;
import com.example.socialnetwork_1connetiondb.utils.paging.Page;
import com.example.socialnetwork_1connetiondb.utils.paging.Pageable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * The main service.
 */
public class Service implements Observable<Event>{
    private FriendshipService friendshipService;
    private UserService userService;
    private FriendRequestService friendRequestService;
    private NotificationService notificationService;
    private MessageService messageService;
    private List<Observer<Event>> observers = new ArrayList<>();

    /**
     * Constructor.
     * Load the friends list for each user.
     * @param friendshipService the friendship service
     * @param userService the user service
     */
    public Service(FriendshipService friendshipService, UserService userService, FriendRequestService friendRequestService, NotificationService notificationService, MessageService messageService){
        this.friendshipService = friendshipService;
        this.userService = userService;
        this.friendRequestService = friendRequestService;
        this.notificationService = notificationService;
        this.messageService = messageService;
        loadFriendsToUser();
    }

    /**
     * Save oen user.
     * @param firstName - the first name of the user to be saved
     * @param lastName - the last name of the user to be saved
     * @param userName - the userName of the user to be saved
     * @param password - the password of the user to be saved
     * @throws ValidationException
     *                 if the entity is not valid
     * @throws IllegalArgumentException
     *                  if the given entity is null.
     */
    public void saveUser(String firstName, String lastName, String userName, String password){
        userService.save(firstName, lastName, userName, password);
    }

    /**
     * Delete one user.
     * @param id - the id of the user to be deleted must not be null
     * @return the entity with the specified id
     * @throws IllegalArgumentException if the given id is null.
     * @throws ServiceException if there are no users with the specified id
     */
    public User deleteUser(Long id) {
        Optional<User> user = (Optional<User>) userService.findOne(id);
        if (user.isEmpty()) {
            throw new ServiceException("Utilizator inexistent!\n");
        }

        //Stergem prieteniile din care facea parte persoana respectiva.
        Iterable<Friendship> friendships = friendshipService.findAll();
        Predicate<Friendship> friend1IdEqual = friendship -> friendship.getUser1().getId().equals(id);
        Predicate<Friendship> friend2IdEqual = friendship -> friendship.getUser2().getId().equals(id);
        Predicate<Friendship> friendshipPredicate = friend1IdEqual.or(friend2IdEqual);
        friendships.forEach(friendship -> {
            if (friendshipPredicate.test(friendship)){
                deleteFriendship(friendship.getId());
            }
        });

        //Stergem utilizatorul.
        User userDeleted =  (User) userService.delete(id);

        UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.DELETE, user.get(), userDeleted);
        notifyObservers(event);
        return  userDeleted;
    }

    /**
     * Save a friendship.
     * @param idUser1 - the id of the first friend of the friendship to be saved
     * @param idUser2 - the id of the second friend of the firendship to be saved
     * @throws ServiceException
     *                  if one of the users does not exist
     * @throws ValidationException
     *                  if the user is not valid
     * @throws IllegalArgumentException
     *                   if the given user is null.
     */
    public void saveFriendship(Long idUser1, Long idUser2){
        Friendship friendship = friendshipService.save(idUser1, idUser2);
        saveFriendshipToUser(friendship.getUser1(), friendship.getUser2());

        UserEntityChangeEvent event1 = new UserEntityChangeEvent(ChangeEventType.ADD, friendship.getUser1());
        UserEntityChangeEvent event2 = new UserEntityChangeEvent(ChangeEventType.ADD, friendship.getUser2());
        notifyObservers(event1);
        notifyObservers(event2);
    }

    /**
     * Delete a friendship.
     * @param friendshipId - the id of the friendship to be deleted must not be null
     * @throws IllegalArgumentException if the given id is null.
     * @throws ServiceException if there are no firenships with the specified id
     * @return the removed friendship
     */
    public Friendship deleteFriendship(Long friendshipId) {
        Friendship friendship = (Friendship) friendshipService.delete(friendshipId);
        if (friendship == null){
            throw new ServiceException("Prietenie inexistenta!\n");
        }
        //Stergem prietenia din lista fiecarui utilizator care facea parte din prieteneie.
        User user1 = friendship.getUser1();
        User user2 = friendship.getUser2();

        ArrayList<User> friends1 = user1.getFriends();
        ArrayList<User> friends2 = user2.getFriends();

        friends1.remove(user2);
        friends2.remove(user1);
        return friendship;
    }

    /**
     * Save a friend request.
     * @param sendingUserId - the id of the user who send the request.
     * @param receivingUserID - the id of the user whom the request is sent.
     * @return the saved friend request
     */
    public FriendRequest saveFriendRequest(Long sendingUserId, Long receivingUserID){
        Optional<Friendship> friendship = areFriends(sendingUserId, receivingUserID);
        if (friendship.isPresent()){
            throw new ServiceException("They are already friend!\n");
        }
        findAllFriendRequests().forEach(friendRequest -> {
            if (Objects.equals(friendRequest.getSendingUser().getId(), sendingUserId)
                    && Objects.equals(friendRequest.getReceivingUser().getId(), receivingUserID)
                    && Objects.equals(friendRequest.getStatus(), "not accepted")){
                throw new ServiceException("Friend request has already been sent!\n");
            }
            if (Objects.equals(friendRequest.getSendingUser().getId(), receivingUserID)
                    && Objects.equals(friendRequest.getReceivingUser().getId(), sendingUserId)
                    && !friendship.isPresent()){
                throw new ServiceException("The user you want to befriend has already sent you a friend request. You have to accept it!\n");
            }
        });
        FriendRequest friendRequest = friendRequestService.save(sendingUserId, receivingUserID);
        FriendRequestChangeEvent event = new FriendRequestChangeEvent(ChangeEventType.ADD, friendRequest);
        notifyObservers(event);
        return  friendRequest;
    }

    public Iterable<FriendRequest> findFriendRequests(Long id){
        List<FriendRequest> friendRequests = new ArrayList<>();
        findAllFriendRequests().forEach(friendRequest -> {
            if (Objects.equals(friendRequest.getReceivingUser().getId(), id)){
                friendRequests.add(friendRequest);
            }
        });
        return  friendRequests;
    }

    /**
     * Delete a friendship between two users.
     * @param idFriend1 - the id of the first user
     * @param idFriend2 - the id of the second user
     * @throws IllegalArgumentException if the given id is null.
     * @throws ServiceException if there are no friendships with the specified id
     *      or there are no friendship between given users.
     */
    public void deleteFriend(Long idFriend1, Long idFriend2){
        Optional<Friendship> friendship = areFriends(idFriend1, idFriend2);
        if (friendship.isEmpty()){
            throw new ServiceException("This friendship does not exist!\n");
        }
        Friendship removedFriendship = deleteFriendship(friendship.get().getId());

        UserEntityChangeEvent event1 = new UserEntityChangeEvent(ChangeEventType.DELETE, removedFriendship.getUser1(), removedFriendship.getUser1());
        UserEntityChangeEvent event2 = new UserEntityChangeEvent(ChangeEventType.DELETE, removedFriendship.getUser2(), removedFriendship.getUser2());
        notifyObservers(event1);
        notifyObservers(event2);
    }

    /**
     * Check if two users are friends.
     * @param idUser1 - the id of the first user
     * @param idUser2 - the id of the second user
     * @return {@code Optional} encapsulating the friendship between the given users
     */
    public Optional<Friendship> areFriends(Long idUser1, Long idUser2){
        AtomicReference<Optional<Friendship>> friendship = new AtomicReference<>(Optional.empty());
        findAllFriendships().forEach(f -> {
            if ((Objects.equals(f.getUser1().getId(), idUser1) && Objects.equals(f.getUser2().getId(), idUser2))
            || (Objects.equals(f.getUser1().getId(), idUser2) && Objects.equals(f.getUser2().getId(), idUser1)))
                friendship.set(Optional.of(f));
        });
        return friendship.get();
    }

    /**
     * Delete a friend request
     * @param id - the id of the friend request to be deleted.
     */
    public void deleteFriendRequest(Long id){
        FriendRequest friendRequest = friendRequestService.delete(id);
        notifyObservers(new FriendRequestChangeEvent(ChangeEventType.DELETE, friendRequest));
    }

    /**
     * Accept a friend request.
     * @param id - the id of the friend request to be accepted.
     */
    public void acceptFriendRequest(Long id){
        FriendRequest friendRequest = friendRequestService.accept(id);
        saveFriendship(friendRequest.getSendingUser().getId(), friendRequest.getReceivingUser().getId());
        FriendRequestChangeEvent event = new FriendRequestChangeEvent(ChangeEventType.UPDATE, friendRequest);
        notifyObservers(event);
    }

    /**
     * Save a notification.
     * @param sendingUserId - id of the sending user
     * @param receivingUserId - id of the receiving user
     * @param message - the notification message
     * @throws ServiceException
     *      if one of the users does not exist
     * @throws ValidationException
     *      if the entity is not valid
     * @throws IllegalArgumentException
     *      if the given entity is null.
     */
    public Notification saveNotification(Long sendingUserId, Long receivingUserId, String message){
        return notificationService.save(sendingUserId, receivingUserId, message);
    }

    /**
     * Delete a notification.
     * @param id - the id of the notification to be deleted.
     * @throws IllegalArgumentException if the given id is null.
     * @throws ServiceException if there are no notification with the specified id
     */
    public void deleteNotification(Long id){
        notificationService.delete(id);
    }

    /**
     * Seen a notification.
     * @param id - the id of the notification to be seen.
     */
    public void seenNotification(Long id){
        notificationService.seen(id);
    }

    /**
     * Save a message.
     * @param fromId - the id of the user who send message
     * @param toId - the id of the user who receive message
     * @param messageText - the message to be sent
     * @return the saved message
     * @throws ServiceException
     *      *      if one of the users does not exist
     * @throws ValidationException
     *      if the entity is not valid
     * @throws IllegalArgumentException
     *      if the given entity is null.
     */
    public Message saveMessage(Long fromId, Long toId, String messageText){
        Message message = messageService.save(fromId, toId, messageText);
        notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, message));
        return message;
    }

    /**
     * Save a message.
     * @param fromId - the id of the user who send message
     * @param toId - the id of the user who receive message
     * @param messageText - the message to be sent
     * @param replyId - the id oht the message to reply
     * @return the saved message
     * @throws ServiceException
     *      *      if one of the users or message to reply does not exist
     * @throws ValidationException
     *      if the entity is not valid
     * @throws IllegalArgumentException
     *      if the given entity is null.
     */
    public Message saveMessage(Long fromId, Long toId, String messageText, Long replyId){
        Message message = messageService.save(fromId, toId, messageText, replyId);
        notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, message));
        return message;
    }

    /**
     *
     * @return all messages
     */
    public Iterable<Message> findAllMessages(){
        return messageService.findAll();
    }

    /**
     *
     * @return all notifications
     */
    public Iterable<Notification> findAllNotifications(){
        return notificationService.findAll();
    }

    /**
     * @return all friend requests
     */
    public Iterable<FriendRequest> findAllFriendRequests(){
        return friendRequestService.findAll();
    }

    /**
     *
     * @return all users
     */
    public Iterable<User> findAllUsers(){
        return userService.findAll();
    }

    /**
     *
     * @return all friendships
     */
    public Iterable<Friendship> findAllFriendships(){
        return friendshipService.findAll();
    }

    /**
     * Find all community.
     * @return all coumunities.
     */
    public List<List<User>> communities() {
        //Construim lista de adiacenta.
        Iterable<Friendship> friendships = friendshipService.findAll();
        Map<String, List<String>> graph = new HashMap<>();

        friendships.forEach(friendship -> {
            String node1 = friendship.getUser1().getId().toString();
            String node2 = friendship.getUser2().getId().toString();
            graph.putIfAbsent(node1, new ArrayList<>());
            graph.putIfAbsent(node2, new ArrayList<>());
            graph.get(node1).add(node2);
            graph.get(node2).add(node1);
        });

        //Adaugam nodurile utilizatorilor fara prieteni.
        Iterable<User> users = userService.findAll();
        users.forEach(user -> {
            String node = user.getId().toString();
            graph.putIfAbsent(node, new ArrayList<>());
        });

        Map<String, Boolean> visited = new HashMap<>();
        List<List<User>> communities = new ArrayList<>();

        graph.keySet().forEach(node ->{
            if (!visited.getOrDefault(node, false)) {
                List<String> community = new ArrayList<>();
                dfs(node, visited, graph, community);

                //Convertim in utilizatori.
                List<User> comp = new ArrayList<>();
                community.forEach(c ->{
                    comp.add(userService.findOne(Long.parseLong(c)).get());
                });
                communities.add(comp);
            }
        });

        return communities;
    }

    /**
     * Performs a depth-first search (DFS) on the graph starting from a given node.
     *
     * @param node - the starting node for the DFS traversal.
     * @param visited - a map that keeps track of visited nodes. The key is the node, and the value is true if the node has been visited.
     * @param graph - the graph represented as an adjacency list. The key is a node, and the value is a list of adjacent nodes.
     * @param community - a list that collects the nodes in the current connected component.
     */
    private void dfs(String node, Map<String, Boolean> visited, Map<String, List<String>> graph, List<String> community) {
        visited.put(node, true);
        community.add(node);
        graph.get(node).forEach(neighbor->{
            if (!visited.getOrDefault(neighbor, false)) {
                dfs(neighbor, visited, graph, community);
            }
        });
    }

    /**
     * Update each user's friends list.
     * @param user1 the first friend of the friendship that was saved to the friend list of the second friend
     * @param user2 the second friend of the friendship that was saved to the friend list of the first friend
     * @throws ServiceException if the firendship already exists
     * @throws IllegalArgumentException - if one of the users has null id
     * @throws ValidationException - if one of the user is not valid
     */
    private void saveFriendshipToUser(User user1, User user2) {
        ArrayList<User> friends1 = user1.getFriends();
        ArrayList<User> friends2 = user2.getFriends();
        if (friends1.contains(user2)) {
            throw new ServiceException("Prietenie deja existenta!\n");
        }
        friends1.add(user2);
        if (friends2.contains(user1)) {
            throw new ServiceException("Prietenie deja existenta!\n");
        }
        friends2.add(user1);

        user1.setFriends(friends1);
        user2.setFriends(friends2);

        userService.update(user1);
        userService.update(user2);
    }

    /**
     * Find the most sociable community.
     * @return the most sociable community
     */
    public List<List<User>> theMostSociableCommunity(){
        List<List<User>> communties = communities();
        final int[] lengthMax = {0};
        List<List<User>> theMostSociableCommunity = new ArrayList<>();

        communties.forEach(community->{
            if (community.size() > lengthMax[0]){
                lengthMax[0] = community.size();
                theMostSociableCommunity.clear();
                theMostSociableCommunity.add(community);
            } else if (community.size() == lengthMax[0]) {
                theMostSociableCommunity.add(community);
            }
        });
        return theMostSociableCommunity;
    }

    /**
     * Find one user by name.
     * @param name - the first name of the user to be returned
     * @return an {@code Optional} encapsulating the entity with the given name
     */
    public Optional<User> findOneUserByName(String name){
        return userService.findOneByName(name);
    }

    /**
     * Login an user.
     * @param username - the username of the person to be logged
     * @param password - the password of the person to be logged
     * @return an {@code Optional} encapsulating the logged entity
     * @throws ServiceException if the password is wrong
     */
    public Optional<User> login(String username, String password){
        return userService.login(username, password);
    }

    /**
     * Load all friend list for each user.
     */
    private void loadFriendsToUser() {
        Iterable<Friendship> friednships = friendshipService.findAll();
        friednships.forEach(friendship -> {
            User user1 = friendship.getUser1();
            User user2 = friendship.getUser2();
            saveFriendshipToUser(user1, user2);
        });
    }

    public Page<Friendship> findAllFriendshipOnPage(Long id, Pageable pageable){
        return friendshipService.findAllOnPage(id, pageable);
    }

    public Optional<User> findOneUser(Long id){
        return userService.findOne(id);
    }

    @Override
    public void addObserver(Observer<Event> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<Event> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(Event t) {
        observers.forEach(x->x.update(t));
    }
}

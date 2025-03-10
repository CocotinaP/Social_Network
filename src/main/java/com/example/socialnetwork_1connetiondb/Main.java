package com.example.socialnetwork_1connetiondb;

public class Main {
    public static void main(String[] args) {
        /*
        String user = "postgres";
        String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
        String password = "oamenidezapada";
        ValidatorFactory validator = ValidatorFactory.getInstance();
        //Repository userRepository = new UserRepository(validator.getValidator(ValidatorStrategy.USER), "./data/users");
        //Repository friendshipRepository = new FriendshipRepository(validator.getValidator(ValidatorStrategy.FRIENDSHIP), "./data/friendships");
        Repository userRepository = new UserDataBaseRepository(url, user, password, (UserValidator) validator.getValidator(ValidatorStrategy.USER));
        Repository friendshipRepository = new FriendshipDatabaseRepository(url, user, password, (FriendshipValidator) validator.getValidator(ValidatorStrategy.FRIENDSHIP));
        Repository friendRequestRepository = new FriendRequestDatabaseRepository(url, user, password, new FriendRequestValidator());
        UserService userService = new UserService(userRepository);
        FriendshipService friendshipService = new FriendshipService(friendshipRepository, userRepository);
        FriendRequestService friendRequestService = new FriendRequestService(friendRequestRepository, userRepository);
        NotificationService notificationService = new NotificationService(new NotificationDatabaseRepository(url, user, password, new NotificationValidator()), userRepository);
        MessageService messageService = new MessageService(new MessageDatabaseRepository(url, user, password, new MessageValidator()), userRepository);
        Service service = new Service(friendshipService, userService, friendRequestService, notificationService, messageService);
        Console console = new Console(service);
        console.show();*/

        HelloApplication.main(args);
    }
}

package com.example.socialnetwork_1connetiondb.repository.database;

import com.example.socialnetwork_1connetiondb.domain.validators.*;

public class DatabaseRepositoryFactory {
    private final DataBaseAccess dataBaseAccess;
    private static DatabaseRepositoryFactory instance;

    private DatabaseRepositoryFactory(DataBaseAccess dataBaseAccess){
        this.dataBaseAccess = dataBaseAccess;
    }

    public AbstractDatabaseRepository getRepository(DatabaseRepositoryStrategy strategy, Validator validator) {
        if (strategy == null || validator == null) {
            throw new IllegalArgumentException("Strategy and validator cannot be null");
        }
        return switch (strategy) {
            case USERS -> new UserDataBaseRepository((UserValidator) validator, dataBaseAccess);
            case FRIENDSHIPS -> new FriendshipDatabaseRepository((FriendshipValidator) validator, dataBaseAccess);
            case FRIENDREQUESTS ->
                    new FriendRequestDatabaseRepository((FriendRequestValidator) validator, dataBaseAccess);
            case MESSAGES -> new MessageDatabaseRepository((MessageValidator) validator, dataBaseAccess);
            case NOTIFICATIONS -> new NotificationDatabaseRepository((NotificationValidator) validator, dataBaseAccess);
            default -> null;
        };
    }

    public static DatabaseRepositoryFactory getInstance(DataBaseAccess dataBaseAccess){
        if (instance == null){
            instance = new DatabaseRepositoryFactory(dataBaseAccess);
        }
        return instance;
    }
}

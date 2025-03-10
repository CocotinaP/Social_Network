package com.example.socialnetwork_1connetiondb;

import com.example.socialnetwork_1connetiondb.controller.LoginController;
import com.example.socialnetwork_1connetiondb.controller.StartController;
import com.example.socialnetwork_1connetiondb.domain.validators.*;
import com.example.socialnetwork_1connetiondb.repository.Repository;
import com.example.socialnetwork_1connetiondb.repository.database.*;
import com.example.socialnetwork_1connetiondb.service.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

/*
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("newAccount.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        scene.getStylesheets().add(String.valueOf(Objects.requireNonNull(HelloApplication.class.getResource("login.css")).toExternalForm()));
        stage.setTitle("Create account");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}*/

public class HelloApplication extends Application{

    private Repository userRepository;
    private Repository friendshipRepository;
    private Repository friendRequestRepository;
    private Repository messageRepository;
    private Repository notificationRepository;
    private Service service;
    private UserService userService;
    private FriendshipService friendshipService;
    private FriendRequestService friendRequestService;
    private NotificationService notificationService;
    private MessageService messageService;
    private DataBaseAccess dataBaseAccess;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Reading data from file...");
        String username = "postgres";
        String password = "oamenidezapada";
        String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
        dataBaseAccess = new DataBaseAccess(url, password, username);
        try{
            dataBaseAccess.createConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        userRepository = DatabaseRepositoryFactory.getInstance(dataBaseAccess).getRepository(DatabaseRepositoryStrategy.USERS, new UserValidator());
        friendshipRepository = DatabaseRepositoryFactory.getInstance(dataBaseAccess).getRepository(DatabaseRepositoryStrategy.FRIENDSHIPS, new FriendshipValidator());
        friendRequestRepository = DatabaseRepositoryFactory.getInstance(dataBaseAccess).getRepository(DatabaseRepositoryStrategy.FRIENDREQUESTS, new FriendRequestValidator());
        messageRepository = DatabaseRepositoryFactory.getInstance(dataBaseAccess).getRepository(DatabaseRepositoryStrategy.MESSAGES, new MessageValidator());
        notificationRepository = DatabaseRepositoryFactory.getInstance(dataBaseAccess).getRepository(DatabaseRepositoryStrategy.NOTIFICATIONS, new NotificationValidator());
        userService = new UserService(userRepository);
        friendshipService = new FriendshipService((FriendshipDatabaseRepository) friendshipRepository, userRepository);
        friendRequestService = new FriendRequestService(friendRequestRepository, userRepository);
        notificationService = new NotificationService(notificationRepository, userRepository);
        messageService = new MessageService(messageRepository, userRepository);

        service = new Service(friendshipService, userService, friendRequestService, notificationService, messageService);
        FXMLLoader fxmlLoader2 = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));

        BorderPane loginLayout = fxmlLoader2.load();
        Stage secondaryStage = new Stage();
        secondaryStage.setScene(new Scene(loginLayout));

        LoginController loginController = fxmlLoader2.getController();
        loginController.setService(service);
        secondaryStage.setWidth(800);
        secondaryStage.setHeight(600);
        secondaryStage.setTitle("Log in");
        secondaryStage.show();

        FXMLLoader fxmlLoader1 = new FXMLLoader(HelloApplication.class.getResource("start-view.fxml"));

        BorderPane userLayout = fxmlLoader1.load();
        primaryStage.setScene(new Scene(userLayout));

        StartController startController = fxmlLoader1.getController();
        startController.set(service, loginController);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.setTitle("Start");
        primaryStage.show();
    }
}
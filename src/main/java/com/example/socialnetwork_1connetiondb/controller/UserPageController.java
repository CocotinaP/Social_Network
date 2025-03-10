package com.example.socialnetwork_1connetiondb.controller;

import com.example.socialnetwork_1connetiondb.domain.*;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.service.Service;
import com.example.socialnetwork_1connetiondb.service.ServiceException;
import com.example.socialnetwork_1connetiondb.utils.events.*;
import com.example.socialnetwork_1connetiondb.utils.observer.Observer;
import com.example.socialnetwork_1connetiondb.utils.paging.Page;
import com.example.socialnetwork_1connetiondb.utils.paging.Pageable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserPageController implements Observer<Event> {
    private Service service;
    ObservableList<User> tableModel = FXCollections.observableArrayList();
    ObservableList<String> comboBoxModel = FXCollections.observableArrayList();
    ObservableList<String> notificationModel = FXCollections.observableArrayList();
    ObservableList<Message> messagesModel = FXCollections.observableArrayList();
    User user;

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> firstNameCell;
    @FXML
    TableColumn<User, String> lastNameCell;
    @FXML
    Label userProfileLabel;
    @FXML
    Button addFriendButton;
    @FXML
    Button removeFriendButton;
    @FXML
    Button viewFriendRequestsButton;
    @FXML
    ComboBox<String> comboBox;
    @FXML
    MenuItem logOutMenuItem;
    @FXML
    MenuItem removeAccountMenuItem;
    @FXML
    Menu notificationMenu;
    @FXML
    Label notificationLabel;
    @FXML
    ListView<Message> messageList;
    @FXML
    Label chatLabel;
    @FXML
    TextField messageTextField;
    @FXML
    Button sendButton;
    @FXML
    Button previousPageButton;
    @FXML
    Button nextPageButton;
    @FXML
    Label pageNumberLabel;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 2;

    public void set(Service service, User user){
        this.service = service;
        this.user = user;
        userProfileLabel.setText("Welcome, " + user.getFirstName() + " " + user.getLastName() + "!");
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize(){
        firstNameCell.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameCell.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableView.setItems(tableModel);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        comboBox.setItems(comboBoxModel);

        messageList.setItems(messagesModel);

        setCellFactoryMessageList();

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            if (newValue != null){
                initMessageModel(newValue);
                chatLabel.setText("Chat with " + newValue.getFirstName() + ' ' + newValue.getLastName());
                messageTextField.setVisible(true);
                sendButton.setVisible(true);
            }
        });

        messagesModel.addListener((ListChangeListener<Message>) change->{
            while (change.next()){
                if (change.wasAdded()){
                    messageList.scrollTo(change.getFrom());
                }
            }
        } );
    }

    private void setCellFactoryMessageList(){
            messageList.setCellFactory(param-> new ListCell<Message>(){
            @Override
            protected void updateItem(Message message, boolean empty){
                super.updateItem(message, empty);
                if (empty || message == null){
                    setText(null);
                    setGraphic(null);
                }else{
                    HBox hBox = new HBox();
                    hBox.getStyleClass().add("hboxMessage");
                    VBox vBox = new VBox();
                    vBox.setSpacing(-10);
                    Label label = new Label(message.getMessage());
                    Label labelReply = null;
                    label.setWrapText(true);

                    if (message.getReply() != null) {
                        labelReply = new Label(message.getReply().getMessage());
                        labelReply.setWrapText(true);
                        labelReply.getStyleClass().add("replyMessageLabel");
                        vBox.getChildren().add(labelReply);
                    }

                    vBox.getChildren().add(label);

                    if (message.getFrom().getId().equals(user.getId())){
                        hBox.setAlignment(Pos.CENTER_RIGHT);
                        label.getStyleClass().add("messageLabelSent");
                    }else{
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        label.getStyleClass().add("messageLabelReceived");
                    }

                    Button replyButton = new Button();
                    replyButton.setPrefSize(10, 10);
                    Image image = new Image(getClass().getResourceAsStream("/com/example/socialnetwork_1connetiondb/images/reply.png"));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(10);
                    imageView.setFitHeight(10);
                    imageView.setPreserveRatio(true);
                    replyButton.setGraphic(imageView);
                    replyButton.setTooltip(new Tooltip("Reply"));
                    replyButton.setOnAction(event -> handleReplyMessage(event, message));


                    if (hBox.getAlignment() == Pos.CENTER_LEFT) {
                        hBox.getChildren().addAll(vBox, replyButton);
                        replyButton.getStyleClass().add("replyButtonLeft");
                    }else{
                        hBox.getChildren().addAll(replyButton, vBox);
                        replyButton.getStyleClass().add("replyButtonRight");
                    }
                    setGraphic(hBox);
                }
            }
        });
    }

    private void initFriendModel(){
        /*
        Iterable<User> messages = user.getFriends();
        List<User> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        tableModel.setAll(users);*/
        Page<Friendship> page = service.findAllFriendshipOnPage(user.getId(), new Pageable(PAGE_SIZE, currentPage));
        List<Friendship> friends = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        List<User> users = new ArrayList<>();
        friends.forEach(friend->{
            if (Objects.equals(friend.getUser1().getId(), user.getId())) {
                users.add(friend.getUser2());
            } else{
                users.add(friend.getUser1());
            }
        });
        tableModel.setAll(users);

        int numberOfPages = (int) Math.ceil((double) page.getTotalNumbersOfElements()/PAGE_SIZE);

        pageNumberLabel.setText("Page " + (currentPage + 1) + " of " + numberOfPages);

        nextPageButton.setDisable(numberOfPages == currentPage + 1 || numberOfPages == 0);
        previousPageButton.setDisable(currentPage == 0);
    }

    private void initComboBoxModel(){
        Iterable<User> messages = service.findAllUsers();
        List<User> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        comboBoxModel.clear();
        users.forEach(u->{
            if (u != user) {
                comboBoxModel.add(u.getFirstName() + ' ' + u.getLastName());
            }
        });
    }

    private void initNotificationModel(){
        Iterable<Notification> notificationMessages = service.findAllNotifications();
        List<MenuItem> notifications = StreamSupport.stream(notificationMessages.spliterator(), false)
                .filter(notification -> !notification.getSeenStatus() && notification.getReceivingUser() == user)
                .map(notification -> {
                    MenuItem menuItem =  new MenuItem(notification.getSendingUser().getFirstName() + ' ' + notification.getSendingUser().getLastName()
                            + " sent you a friend request!\n");
                    menuItem.setOnAction(e->{
                        notificationMenu.getItems().remove(menuItem);
                        service.seenNotification(notification.getId());
                        int nr = Integer.parseInt(notificationLabel.getText());
                        nr--;
                        notificationLabel.setText(Integer.toString(nr));
                        if (notificationLabel.getText().equals("0")){
                            notificationLabel.setVisible(false);
                        }
                        else {
                            notificationLabel.setVisible(true);
                        }
                    });
                    return menuItem;
                })
                .collect(Collectors.toList());
        notificationMenu.getItems().setAll(notifications);

        notificationLabel.setText(Integer.toString(notifications.size()));
        if (notificationLabel.getText().equals("0")){
            notificationLabel.setVisible(false);
        }
        else {
            notificationLabel.setVisible(true);
        }

        messageTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER){
                sendButton.fire();
            }
        });
    }

    private void initMessageModel(User friend){
        messagesModel.clear();
        Iterable<Message> messages = service.findAllMessages();

        messages.forEach(message -> {
            if ((message.getFrom().getId().equals(user.getId()) && message.getTo().getId().equals(friend.getId()))
                    || (message.getFrom().getId().equals(friend.getId()) && message.getTo().getId().equals(user.getId()))) {
                messagesModel.add(message);
            }
        });
    }

    public void initModel(){
        initFriendModel();
        initComboBoxModel();
        initNotificationModel();
    }

    @Override
    public void update(Event event) {
        if (event instanceof UserEntityChangeEvent || event instanceof MessageChangeEvent){
            initModel();
        } else if (event instanceof FriendRequestChangeEvent) {
            if (Objects.equals(((FriendRequestChangeEvent) event).getType(), ChangeEventType.ADD)) {
                FriendRequest friendRequest = ((FriendRequestChangeEvent) event).getData();
                if (Objects.equals(friendRequest.getReceivingUser(), user)) {
                    notification(friendRequest);
                }
            }
        }
        if (notificationLabel.getText().equals("0")){
            notificationLabel.setVisible(false);
        }
    }

    public void handleDeleteButton(ActionEvent actionEvent){
        User userToDelete = tableView.getSelectionModel().getSelectedItem();

        Alert sure = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove friendship with "
                + userToDelete.getFirstName() + ' ' + userToDelete.getLastName() + "?", ButtonType.YES, ButtonType.NO);
        sure.setHeaderText(null);
        sure.setTitle("Are you sure?");

        sure.showAndWait().ifPresent(response->{
            if (response == ButtonType.YES){
                try {
                    service.deleteFriend(user.getId(), userToDelete.getId());

                    Alert success = new Alert(Alert.AlertType.INFORMATION, "User removed successfully!", ButtonType.OK);
                    success.setTitle("Information");
                    success.setHeaderText(null);
                    success.showAndWait();
                }catch(IllegalArgumentException | ValidationException | ServiceException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    if (e instanceof IllegalArgumentException){
                        alert.setTitle("IllegalArgumentException");
                    } else if (e instanceof ValidationException) {
                        alert.setTitle("ValidationException");
                    }else {
                        alert.setTitle("ServiceException");
                    }
                    alert.setHeaderText(null);
                    alert.showAndWait();
                }
            }
        });
    }

    public void notification(FriendRequest friendRequest){
        int numberNotifications = Integer.parseInt(notificationLabel.getText());
        numberNotifications++;
        notificationLabel.setText(Integer.toString(numberNotifications));

        String notificationText = friendRequest.getSendingUser().getFirstName() + ' ' + friendRequest.getSendingUser().getLastName() + " sent you a friend request!";
        MenuItem notificationItem = new MenuItem(notificationText);
        notificationMenu.getItems().add(notificationItem);

        Notification notification = service.saveNotification(friendRequest.getSendingUser().getId(), friendRequest.getReceivingUser().getId(), notificationText);
        notificationItem.setOnAction(e->{
            notificationMenu.getItems().remove(notificationItem);
            service.seenNotification(notification.getId());
            int nr = Integer.parseInt(notificationLabel.getText());
            nr--;
            notificationLabel.setText(Integer.toString(nr));
            if (notificationLabel.getText().equals("0")){
                notificationLabel.setVisible(false);
            }
            else {
                notificationLabel.setVisible(true);
            }
        });
        if (notificationMenu.getItems().isEmpty()){
            notificationLabel.setText("0");
            notificationLabel.setVisible(false);
        }
        else{
            notificationLabel.setText(Integer.toString(notificationMenu.getItems().size()));
            notificationLabel.setVisible(true);
        }
    }

    public void handleAddFriendButton(ActionEvent actionEvent){
        String valueComboBox = comboBox.getValue();
        Optional<User> findUser = service.findOneUserByName(valueComboBox);
        if (findUser.isPresent()){
            try {
                FriendRequest friendRequest = service.saveFriendRequest(user.getId(), findUser.get().getId());
            }catch(IllegalArgumentException | ValidationException | ServiceException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                if (e instanceof IllegalArgumentException){
                    alert.setTitle("IllegalArgumentException");
                } else if (e instanceof ValidationException) {
                    alert.setTitle("ValidationException");
                }else {
                    alert.setTitle("ServiceException");
                }
                alert.setHeaderText(null);
                alert.showAndWait();
            }
        }
        else{
            Alert alertBox = new Alert(Alert.AlertType.INFORMATION, "User not found!", ButtonType.OK);
            alertBox.setTitle("Information");
            alertBox.setHeaderText(null);
            alertBox.showAndWait();
        }
    }

    public void handleSearchButton(ActionEvent actionEvent) throws IOException {
        String valueComboBox = comboBox.getValue();
        Optional<User> findUser = service.findOneUserByName(valueComboBox);
        if (findUser.isPresent()){
            FXMLLoader fxmlLoader1 = new FXMLLoader(LoginController.class.getResource("../profilePage.fxml"));

            BorderPane layout = fxmlLoader1.load();

            ProfilePageController profilePageController = fxmlLoader1.getController();
            profilePageController.set(service, user, findUser.get());

            Scene nextScene = new Scene(layout);

            Stage window = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            window.setScene(nextScene);
            window.setWidth(800);
            window.setHeight(600);
            window.setTitle(valueComboBox);

            window.sizeToScene();
            window.show();
        }
    }

    public void handleViewFriendRequestsButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader1 = new FXMLLoader(LoginController.class.getResource("../friend-requests.fxml"));
        //Stage primaryStage = new Stage();

        BorderPane userLayout = fxmlLoader1.load();
        //primaryStage.setScene(new Scene(userLayout));

        FriendRequestsController startController = fxmlLoader1.getController();
        startController.set(service, user);

        Scene nextScene = new Scene(userLayout);

        Stage window = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

        window.setScene(nextScene);
        window.setWidth(800);
        window.setHeight(600);
        window.setTitle("Friend Requests");

        window.sizeToScene();
        window.show();

        /*
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.show();*/
    }

    public void handleLogOut(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../login.fxml"));
        BorderPane sceneParent = loader.load();

        LoginController loginController = loader.getController();
        loginController.setService(service);

        Scene scene = new Scene(sceneParent);

        Stage window = (Stage) ((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow();

        window.setScene(scene);
        window.setTitle("Log in");
        window.setWidth(800);
        window.setHeight(600);
        window.sizeToScene();
        window.show();
    }

    public void handleSendMessage(ActionEvent actionEvent){
        String messageText = messageTextField.getText();
        if (messageText.isEmpty()){
            sendButton.setDisable(false);
        }else{
            User friend = tableView.getSelectionModel().getSelectedItem();
            service.saveMessage(user.getId(), friend.getId(), messageText);
            messageTextField.clear();
        }
    }

    public void handleSendMessage(ActionEvent actionEvent, Message message){
        String messageText = messageTextField.getText();
        if (messageText.isEmpty()){
            sendButton.setDisable(false);
        }else{
            User friend = tableView.getSelectionModel().getSelectedItem();
            if (message == null) {
                service.saveMessage(user.getId(), friend.getId(), messageText);
            }else{
                service.saveMessage(user.getId(), friend.getId(), messageText, message.getId());
            }
            sendButton.setOnAction(UserPageController.this::handleSendMessage);
            messageTextField.clear();
        }
    }

    public void onNextPage(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }

    public void handleReplyMessage(ActionEvent actionEvent, Message message){
        sendButton.setOnAction(event -> handleSendMessage(event, message));
    }

    public void handleRemoveAccount(ActionEvent actionEvent){
        System.out.println("Remove account...");
    }
}

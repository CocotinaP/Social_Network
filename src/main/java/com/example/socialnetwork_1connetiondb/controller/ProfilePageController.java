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

public class ProfilePageController implements Observer<Event> {
    private Service service;
    ObservableList<User> tableModel = FXCollections.observableArrayList();
    User user;
    User searchedUser;

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
    Button previousPageButton;
    @FXML
    Button nextPageButton;
    @FXML
    Label pageNumberLabel;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 2;

    public void set(Service service, User user, User searchedUser){
        this.service = service;
        this.user = user;
        this.searchedUser = searchedUser;
        userProfileLabel.setText(searchedUser.getFirstName() + " " + searchedUser.getLastName() + "!");
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize(){
        firstNameCell.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameCell.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableView.setItems(tableModel);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void initFriendModel(){
        /*
        Iterable<User> messages = user.getFriends();
        List<User> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        tableModel.setAll(users);*/
        Page<Friendship> page = service.findAllFriendshipOnPage(searchedUser.getId(), new Pageable(PAGE_SIZE, currentPage));
        List<Friendship> friends = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        List<User> users = new ArrayList<>();
        friends.forEach(friend->{
            if (Objects.equals(friend.getUser1().getId(), searchedUser.getId())) {
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

    public void initModel(){
        initFriendModel();
    }

    @Override
    public void update(Event event) {
        if (event instanceof UserEntityChangeEvent){
            initModel();
        }
    }

    public void handleDeleteButton(ActionEvent actionEvent){
        User userToDelete = searchedUser;

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

    public void handleAddFriendButton(ActionEvent actionEvent){
        try {
            FriendRequest friendRequest = service.saveFriendRequest(user.getId(), searchedUser.getId());
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

    public void onNextPage(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }

    public void handleBackButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../userPage.fxml"));
        BorderPane previousSceneParent = loader.load();

        UserPageController userPageController = loader.getController();
        userPageController.set(service, user);

        Scene previousScene = new Scene(previousSceneParent, 800, 600);

        Stage window = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

        window.setScene(previousScene);
        window.setWidth(800);
        window.setHeight(600);
        window.setTitle("Home");
        window.sizeToScene();
        window.show();
    }

}

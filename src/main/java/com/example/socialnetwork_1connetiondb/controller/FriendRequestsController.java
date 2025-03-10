package com.example.socialnetwork_1connetiondb.controller;

import com.example.socialnetwork_1connetiondb.domain.FriendRequest;
import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.service.Service;
import com.example.socialnetwork_1connetiondb.service.ServiceException;
import com.example.socialnetwork_1connetiondb.utils.events.Event;
import com.example.socialnetwork_1connetiondb.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestsController implements Observer<Event> {
    private Service service;
    private User user;

    @FXML
    ListView<FriendRequest> listView;
    @FXML
    Button acceptButton;
    ObservableList<FriendRequest> listModel = FXCollections.observableArrayList();
    @FXML
    private Label userProfileLabel;
    @FXML
    private Label emptyListMessage;
    @FXML
    private Button backButton;
    @FXML
    private Button deleteButton;

    public void set(Service service, User user){
        this.service = service;
        this.user = user;
        initModel();
        service.addObserver(this);

        userProfileLabel.setText("Welcome,  " + user.getFirstName() + " " + user.getLastName() + "!");
    }

    @FXML
    public void initialize(){
        listView.setItems(listModel);

        listView.setCellFactory(new Callback<>(){
            @Override
            public ListCell<FriendRequest> call(ListView<FriendRequest> listView){
                return new ListCell<>(){
                    @Override
                    protected void updateItem(FriendRequest friendRequest, boolean empty){
                        super.updateItem(friendRequest, empty);
                        if (empty || friendRequest == null){
                            setText(null);
                        }else{
                            setText("(" + friendRequest.getSubmissionDate().format(DateTimeFormatter.ofPattern("dd/MMMM/yy HH:mm")) + ") "
                                    +friendRequest.getSendingUser().getFirstName() + ' '
                            +friendRequest.getSendingUser().getLastName()
                            + " - " + friendRequest.getStatus());
                        }
                    }
                };
            }
        });
    }

    public void initModel(){
        Iterable<FriendRequest> messages = service.findFriendRequests(user.getId());
        List<FriendRequest> friendRequests = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        listModel.setAll(friendRequests);

        emptyListMessage.setVisible(listModel.isEmpty());
    }

    public void handleAcceptButton(ActionEvent actionEvent){
        FriendRequest friendRequest = listView.getSelectionModel().getSelectedItem();
        try {
            service.acceptFriendRequest(friendRequest.getId());
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

    public void handleRemoveButton(ActionEvent actionEvent){
        FriendRequest friendRequest = listView.getSelectionModel().getSelectedItem();
        Alert sure = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this friend request?\n", ButtonType.YES, ButtonType.NO);
        sure.setHeaderText(null);
        sure.setTitle("Are you sure?");

        sure.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    service.deleteFriendRequest(friendRequest.getId());
                } catch (IllegalArgumentException | ValidationException | ServiceException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    if (e instanceof IllegalArgumentException) {
                        alert.setTitle("IllegalArgumentException");
                    } else if (e instanceof ValidationException) {
                        alert.setTitle("ValidationException");
                    } else {
                        alert.setTitle("ServiceException");
                    }
                    alert.setHeaderText(null);
                    alert.showAndWait();
                }
            }
        });
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

    @Override
    public void update(Event event) {
        initModel();
    }
}

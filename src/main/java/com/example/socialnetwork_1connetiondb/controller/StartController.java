package com.example.socialnetwork_1connetiondb.controller;

import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.service.Service;
import com.example.socialnetwork_1connetiondb.utils.events.Event;
import com.example.socialnetwork_1connetiondb.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StartController implements Observer<Event> {
    private Service service;
    ObservableList<User> model = FXCollections.observableArrayList();
    private LoginController loginController;

    @FXML
    private Label logo;
    @FXML
    private TableView<User> tableView;
    @FXML
    private TableColumn<User, Long> idCell;
    @FXML
    private TableColumn<User, String> firstNameCell;
    @FXML
    private TableColumn<User, String> lastNameCell;
    @FXML
    private TableColumn<User, String> usernameCell;
    @FXML
    private TableColumn<User, String> passwordCell;

    public void set(Service service, LoginController loginController){
        this.service = service;
        this.loginController = loginController;
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize(){
        idCell.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        firstNameCell.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        lastNameCell.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        usernameCell.setCellValueFactory(new PropertyValueFactory<User,String>("userName"));
        passwordCell.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        idCell.setVisible(false);

        tableView.setItems(model);

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
            if (newValue != null){
                loginController.login(newValue);
            }
            else{
                loginController.login(null);
            }
        });
    }

    public void initModel(){
        Iterable<User> messages = service.findAllUsers();
        List<User> users = StreamSupport.stream(messages.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(users);
    }

    @Override
    public void update(Event event) {
        initModel();
    }
}

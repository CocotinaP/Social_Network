package com.example.socialnetwork_1connetiondb.controller;

import com.example.socialnetwork_1connetiondb.domain.User;
import com.example.socialnetwork_1connetiondb.domain.validators.ValidationException;
import com.example.socialnetwork_1connetiondb.service.Service;
import com.example.socialnetwork_1connetiondb.service.ServiceException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    private Service service;
    User user;

    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField textFieldPassword;
    @FXML
    private Button buttonLogin;
    @FXML
    private Button buttonCreateNewAccount;
    @FXML
    private Label usernameErrorLabel;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private TextField textFieldFirstNameCreateAccount;
    @FXML
    private TextField textFieldLastNameCreateAccount;
    @FXML
    private TextField textFieldUsernameCreateAccount;
    @FXML
    private TextField textFieldPasswordCreateAccount;
    @FXML
    private Button buttonRegisterNewAccount;

    public void setService(Service service){
        this.service = service;
    }

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        usernameErrorLabel.setText("");
        passwordErrorLabel.setText("");
        String username = textFieldUsername.getText();
        String password = textFieldPassword.getText();
        try {
            Optional<User> user = service.login(username, password);
            if (user.isEmpty()){
                textFieldUsername.clear();
                textFieldPassword.clear();
                usernameErrorLabel.textProperty().set("User not found!");
            }
            else{
                FXMLLoader fxmlLoader1 = new FXMLLoader(LoginController.class.getResource("../userPage.fxml"));
                Stage primaryStage = new Stage();

                BorderPane userLayout = fxmlLoader1.load();
                primaryStage.setScene(new Scene(userLayout));

                UserPageController startController = fxmlLoader1.getController();
                startController.set(service, user.get());
                primaryStage.setWidth(800);
                primaryStage.setHeight(600);
                primaryStage.setTitle("Home");
                primaryStage.show();
            }
        } catch (ServiceException e) {
            textFieldPassword.clear();
            passwordErrorLabel.textProperty().set(e.getMessage());
        }
    }

    public void handleCreateNewAccount(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("../newAccount.fxml"));
        BorderPane createAccountLayout = loader.load();

        Scene createAccountScene = new Scene(createAccountLayout);

        Stage window = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

        window.setScene(createAccountScene);
        window.setWidth(800);
        window.setHeight(600);
        window.setTitle("Create new account");

        window.sizeToScene();
        window.show();
    }

    public void handleRegisternewAccount(ActionEvent actionEvent) throws IOException {
        try {
            /*
            String firstName = textFieldFirstNameCreateAccount.getText();
            String lastName = textFieldLastNameCreateAccount.getText();
            String username = textFieldUsernameCreateAccount.getText();
            String password = textFieldPasswordCreateAccount.getText();
            service.saveUser(firstName, lastName, username, password);*/
            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("../login.fxml"));
            BorderPane loginLayout = loader.load();

            Scene loginScene = new Scene(loginLayout);

            Stage window = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();

            window.setScene(loginScene);
            window.setWidth(800);
            window.setHeight(600);
            window.setTitle("Log in");

            window.sizeToScene();
            window.show();
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

    public void login(User user){
        this.user = user;
        textFieldUsername.setText(user.getUserName());
        textFieldPassword.setText(user.getPassword());
    }
}

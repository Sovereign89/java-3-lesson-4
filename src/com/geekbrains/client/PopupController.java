package com.geekbrains.client;

import com.geekbrains.server.ServerCommandConstants;
import com.geekbrains.server.authorization.AuthenticationData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PopupController implements Initializable {
    @FXML
    private TextField nicknameField;
    @FXML
    private Button closeButton;

    private Network network;

    public void changeUsername(ActionEvent event) {
        String new_nickname = nicknameField.getText();
        Stage stage = (Stage) closeButton.getScene().getWindow();
        network.sendMessage(ServerCommandConstants.CHANGE_NICKNAME + " " + network.getUserLogin() + " " + new_nickname);
        nicknameField.clear();
        stage.close();
    }

    public void close() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

}

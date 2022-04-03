package com.geekbrains.client;

import com.geekbrains.server.ServerCommandConstants;
import com.geekbrains.server.authorization.AuthenticationData;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private TextField messageField;
    @FXML
    private TextField loginField;
    @FXML
    private HBox messagePanel;
    @FXML
    private HBox authPanel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ListView<String> clientList;
    @FXML
    private Button changeNickname;
    @FXML
    private Button sendButton;
    @FXML
    private Button authButton;

    private final Network network;

    public ChatController() {
        this.network = new Network(this);
    }

    public void setAuthenticated(boolean authenticated) {
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        messagePanel.setVisible(authenticated);
        messagePanel.setManaged(authenticated);
        clientList.setVisible(authenticated);
        clientList.setManaged(authenticated);
        messageField.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setAuthenticated(false);
    }

    public void displayMessage(String text) {
        if (textArea.getText().isEmpty()) {
            textArea.setText(text);
        } else {
            textArea.setText(textArea.getText() + "\n" + text);
        }
    }

    public void displayMessage(String text, String color) {
        if (textArea.getText().isEmpty()) {
            textArea.setText(text);
        } else {
            textArea.setText(textArea.getText() + "\n" + text);
        }
    }

    public void displayClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().add(nickName);
            }
        });
    }

    public void removeClient(String nickName) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                clientList.getItems().remove(nickName);
            }
        });
    }


    public void sendAuth(ActionEvent event) {
        AuthenticationData authenticationData = network.sendAuth(loginField.getText(), passwordField.getText());
        if(authenticationData.isAuthenticated()) {
            loginField.clear();
            passwordField.clear();
            setAuthenticated(true);
            Stage stage = (Stage) authButton.getScene().getWindow();
        }
    }

    public void changeNickname(ActionEvent event) {
        final double width = 400;
        final double height = 30;
        try {
            FXMLLoader popup = new FXMLLoader(getClass().getResource("popup.fxml"));
            Parent root1 = (Parent) popup.load();
            Stage stage = new Stage();
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - width) / 2);
            stage.setY((screenBounds.getHeight() - height) / 2);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Сменить ник");
            stage.getIcons().add(new Image("resources/change_nickname.png"));
            stage.setScene(new Scene(root1,width,height));

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                        stage.close();
                }
            });

            stage.show();

            PopupController popupController = popup.getController();
            popupController.setNetwork(network);
        } catch (Exception e) {
            Platform.exit();
        }
    }

    public void sendMessage(ActionEvent event) {
        if (clientList.getSelectionModel().getSelectedItem() == null) {
            network.sendMessage(messageField.getText());
        } else {
            network.sendMessage(ServerCommandConstants.PRIVATE_MESSAGE + " " + clientList.getSelectionModel().getSelectedItem().replace("\n","") + " " + messageField.getText());
        }
        messageField.clear();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            System.out.println("Выбран " + clientList.getSelectionModel().getSelectedItem());
        } else {
            clientList.getSelectionModel().clearSelection();
        }
    }

    public void close() {
        network.closeConnection();
    }

}

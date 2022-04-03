package com.geekbrains.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainClient extends Application {

    static final double WIDTH = 600;
    static final double HEIGHT = 400;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - WIDTH) / 2);
        stage.setY((screenBounds.getHeight() - HEIGHT) / 2);
        stage.setTitle("Chat");
        stage.getIcons().add(new Image("resources/application.png"));
        stage.setScene(new Scene(root,WIDTH,HEIGHT));
        stage.show();

        ChatController chatController = loader.getController();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                try {
                    chatController.close();
                } finally {
                    Platform.exit();
                }
            }
        });
    }

}

package cn.edu.sustech.cs209.chatting.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class privateChooseControllerApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(privateChooseControllerApplication.class.getResource("designPrivateChooseController.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),  251.0, 468.0);
        stage.setTitle("Create a private chat");
        stage.setScene(scene);
        stage.show();
    }
}

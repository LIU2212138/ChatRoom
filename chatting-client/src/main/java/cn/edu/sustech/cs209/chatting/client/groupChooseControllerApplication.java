package cn.edu.sustech.cs209.chatting.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class groupChooseControllerApplication  extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader =
                new FXMLLoader(groupChooseControllerApplication.class.getResource(
                        "designGroupChooseController.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),  234.0, 535.0);
        stage.setTitle("Create a group chat");
        stage.setScene(scene);
        stage.show();
    }
}
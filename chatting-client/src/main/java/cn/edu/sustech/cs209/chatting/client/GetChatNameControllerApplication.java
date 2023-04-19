package cn.edu.sustech.cs209.chatting.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GetChatNameControllerApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader =
                new FXMLLoader(GetChatNameControllerApplication.class.getResource(
                        "designGerChatName.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),  468.0, 205.0);
        stage.setTitle("Please Input the Chat Name");
        stage.setScene(scene);
        stage.show();
    }
}

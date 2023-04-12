package cn.edu.sustech.cs209.chatting.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatRoomApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatRoomApplication.class.getResource("designChatRoom.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),  894.0, 622.0);
        stage.setTitle("ChatRoom");
        stage.setScene(scene);
        stage.show();
    }
}

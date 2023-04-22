package cn.edu.sustech.cs209.chatting.client;

/**
 * Sample Skeleton for 'Untitled' Controller Class
 */

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MyObjectInputStream;
import cn.edu.sustech.cs209.chatting.common.MyObjectOutputStream;
import cn.edu.sustech.cs209.chatting.common.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class LoginController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="loginBotton"
    private Button loginBotton; // Value injected by FXMLLoader

    @FXML // fx:id="passwordInput"
    private PasswordField passwordInput; // Value injected by FXMLLoader

    @FXML // fx:id="usernameInput"
    private TextField usernameInput; // Value injected by FXMLLoader

    @FXML // fx:id="wrongPasswordText"
    private Text wrongPasswordText; // Value injected by FXMLLoader

    public Socket socket;

    private MyObjectOutputStream objectOutputStream;

    private MyObjectInputStream objectInputStream;

    public User user;
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() throws IOException {
        assert loginBotton != null : "fx:id=\"loginBotton\" was not injected: check your FXML file 'Untitled'.";
        assert passwordInput != null : "fx:id=\"passwordInput\" was not injected: check your FXML file 'Untitled'.";
        assert usernameInput != null : "fx:id=\"usernameInput\" was not injected: check your FXML file 'Untitled'.";
        assert wrongPasswordText != null : "fx:id=\"wrongPasswordText\" was not injected: check your FXML file 'Untitled'.";
        socket = new Socket("localhost", 8080);
        objectOutputStream = new MyObjectOutputStream(socket.getOutputStream());
    }

    public void login() throws Exception {
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        Message message = new Message(new Date().getTime(), username, "0", "LOGIN " + username + " " + password);
        if (socket.isConnected()) {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } else {
            socket = new Socket("localhost", 8080);
            objectOutputStream = new MyObjectOutputStream(socket.getOutputStream());
            objectInputStream = new MyObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        }

        objectOutputStream.flush();
        objectInputStream = new MyObjectInputStream(socket.getInputStream());
        Object o = objectInputStream.readObject();
        if (! (o instanceof User)) {
            String messageReceive = (String) o;
            wrongPasswordText.setText(messageReceive);
            usernameInput.clear();
            passwordInput.clear();
        } else {
            user = (User) o;
            
            FXMLLoader fxmlLoader = new FXMLLoader(ChatRoomApplication.class.getResource("designChatRoom.fxml"));

            Scene scene = new Scene(fxmlLoader.load(),  894.0, 622.0);
            ChatRoomController controller = fxmlLoader.getController();

            Stage stage = (Stage) loginBotton.getScene().getWindow();
            controller.setLoginStage(stage);
            controller.setUser(user);
            controller.setSocket(socket);

            controller.hideLogin();
            Stage newStage = new Stage();

            controller.setSelfStage(newStage);
            controller.init();

            newStage.setTitle("ChatRoom");
            newStage.setScene(scene);
            newStage.show();
        }
    }

}


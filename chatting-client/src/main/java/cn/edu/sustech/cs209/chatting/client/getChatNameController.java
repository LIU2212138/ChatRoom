package cn.edu.sustech.cs209.chatting.client;
/**
 * Sample Skeleton for 'Untitled' Controller Class
 */

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class getChatNameController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="cancelButton"
    private Button cancelButton; // Value injected by FXMLLoader

    @FXML // fx:id="chatNameTextInput"
    private TextField chatNameTextInput; // Value injected by FXMLLoader

    @FXML // fx:id="yesButton"
    private Button yesButton; // Value injected by FXMLLoader

    private List<String> text;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert cancelButton != null : "fx:id=\"cancelButton\" was not injected: check your FXML file 'Untitled'.";
        assert chatNameTextInput != null : "fx:id=\"chatNameTextInput\" was not injected: check your FXML file 'Untitled'.";
        assert yesButton != null : "fx:id=\"yesButton\" was not injected: check your FXML file 'Untitled'.";

    }

    void init(String defaultName, List<String> chooseName) {
        chatNameTextInput.setText(defaultName);
        this.text = chooseName;
        System.out.println(text);
    }

    @FXML
    void yesClick() {
        System.out.println(chatNameTextInput.getText());
        System.out.println(text);
        text.add(chatNameTextInput.getText());
        System.out.println(text.get(0));
        Stage thisStage = (Stage) chatNameTextInput.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    void cancelClick() {
        text.add("");
        Stage thisStage = (Stage) chatNameTextInput.getScene().getWindow();
        thisStage.close();
    }



}
package cn.edu.sustech.cs209.chatting.client;
/**
 * Sample Skeleton for 'Untitled' Controller Class
 */

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import cn.edu.sustech.cs209.chatting.common.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class groupChooseController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="Choose"
    private Button Choose; // Value injected by FXMLLoader

    @FXML // fx:id="grpupChooseListview"
    private ListView<User> grpupChooseListview; // Value injected by FXMLLoader
    private List<User> users;

    private ObservableList<User> observableList;

    private User self;

    private List<User> chooseUser;
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert Choose != null : "fx:id=\"Choose\" was not injected: check your FXML file 'Untitled'.";
        assert grpupChooseListview != null : "fx:id=\"grpupChooseListview\" was not injected: check your FXML file 'Untitled'.";

    }
    boolean init(List<User> users, User self, List<User> chooseUser) throws IOException, ClassNotFoundException {
        this.chooseUser = chooseUser;
        this.users = users;
        this.self = self;
        observableList = FXCollections.observableArrayList();
        grpupChooseListview.setCellFactory(new privateChooseController.UserFactory());
        grpupChooseListview.setItems(observableList);
        observableList.setAll(users);
        MultipleSelectionModel<User> selectionModel = grpupChooseListview.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        return true;
    }

    @FXML
    void choose() throws IOException, ClassNotFoundException {
        MultipleSelectionModel<User> selectionModel = grpupChooseListview.getSelectionModel();
        ObservableList<User> User = selectionModel.getSelectedItems();
        chooseUser.addAll(User);
        System.out.println(chooseUser);
        Stage thisStage = (Stage) grpupChooseListview.getScene().getWindow();
        thisStage.close();
    }

//    public User getChooseUser() {
//        if (chooseUser != null) {
//            Stage thisStage = (Stage) chooseListView.getScene().getWindow();
//            thisStage.close();
//            return chooseUser;
//        } else {
//            return null;
//        }
//
//    }




}

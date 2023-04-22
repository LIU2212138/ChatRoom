package cn.edu.sustech.cs209.chatting.client;
/**
 * Sample Skeleton for 'Untitled' Controller Class
 */

import cn.edu.sustech.cs209.chatting.common.User;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class groupChooseController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="Choose"
    private Button Choose; // Value injected by FXMLLoader

    @FXML // fx:id="grpupChooseListview"
    private ListView<User> grpupChooseListview; // Value injected by FXMLLoader

    @FXML
    private TextField getNameField;
    private List<User> users;

    private ObservableList<User> observableList;

    private User self;

    private List<String> chooseName;
    private List<User> chooseUser;

    private String userName;

    Stage selfStage;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert Choose != null : "fx:id=\"Choose\" was not injected: check your FXML file 'Untitled'.";
        assert grpupChooseListview != null : "fx:id=\"grpupChooseListview\" was not injected: check your FXML file 'Untitled'.";

    }

    boolean init(List<User> users, User self, List<User> chooseUser, List<String> chooseName, String userName, Stage selfStage) throws IOException, ClassNotFoundException {
        this.chooseUser = chooseUser;
        this.users = users;
        this.self = self;
        this.chooseName = chooseName;
        this.userName = userName;
        this.selfStage = selfStage;
        observableList = FXCollections.observableArrayList();
        grpupChooseListview.setCellFactory(new privateChooseController.UserFactory());
        grpupChooseListview.setItems(observableList);
        observableList.setAll(users);
        MultipleSelectionModel<User> selectionModel = grpupChooseListview.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        selfStage.setOnCloseRequest(event -> {
            chooseName.add(null);
            chooseUser.add(null);
            selfStage.close();
        });
        return true;
    }

    @FXML
    void changeDefaultName() {
        MultipleSelectionModel<User> selectionModel = grpupChooseListview.getSelectionModel();
        ObservableList<User> user = selectionModel.getSelectedItems();
        StringBuilder name = new StringBuilder("ChatRoomWith:" + self.getName());
        for (User user1 : user) {
            name.append(",");
            name.append(user1.getName());
        }
        getNameField.setText(name.toString());
    }

    @FXML
    void choose() throws IOException, ClassNotFoundException {
        MultipleSelectionModel<User> selectionModel = grpupChooseListview.getSelectionModel();
        ObservableList<User> user = selectionModel.getSelectedItems();
        String name = getNameField.getText();
        System.out.println(name);
        chooseName.add(name);
        chooseUser.addAll(user);
        System.out.println(chooseUser);
        Stage thisStage = (Stage) grpupChooseListview.getScene().getWindow();
        thisStage.close();
    }






}

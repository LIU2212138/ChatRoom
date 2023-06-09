package cn.edu.sustech.cs209.chatting.client;

/**
 * Sample Skeleton for 'Untitled' Controller Class
 */

import cn.edu.sustech.cs209.chatting.common.User;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;


public class privateChooseController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="chooseListView"
    private ListView<User> chooseListView; // Value injected by FXMLLoader

    private List<User> users;

    private ObservableList<User> observableList;

    private User self;

    private List<User> chooseUser;

    Stage selfStage;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert chooseListView != null : "fx:id=\"chooseListView\" was not injected: check your FXML file 'Untitled'.";

    }
    boolean init(List<User> users, User self, List<User> chooseUser, Stage selfStage) throws IOException, ClassNotFoundException {
        this.chooseUser = chooseUser;
        this.users = users;
        this.self = self;
        this.selfStage = selfStage;
        observableList = FXCollections.observableArrayList();
        chooseListView.setCellFactory(new UserFactory());
        chooseListView.setItems(observableList);
        observableList.setAll(users);
        MultipleSelectionModel<User> selectionModel = chooseListView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        selfStage.setOnCloseRequest(event -> {
            chooseUser.add(null);
            selfStage.close();
        });
        return true;
    }

    @FXML
    void choose() throws IOException, ClassNotFoundException {
        MultipleSelectionModel<User> selectionModel = chooseListView.getSelectionModel();
        User User = selectionModel.getSelectedItem();
        chooseUser.add(User);
        System.out.println(chooseUser);
        Stage thisStage = (Stage) chooseListView.getScene().getWindow();
        thisStage.close();
    }

    static class UserFactory implements Callback<ListView<User>, ListCell<User>> {
        @Override
        public ListCell<User> call(ListView<User> chatBoxListView) {
            return new ListCell<User>() {
                @Override
                protected void updateItem(User user, boolean b) {
                    super.updateItem(user, b);
                    if (b || Objects.isNull(user)) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }
                    VBox wrapper = new VBox();
                    Label nameLabel = new Label(user.getName() + "\n");
                    wrapper.getChildren().addAll(nameLabel);
                    wrapper.setAlignment(Pos.CENTER);
                    setGraphic(wrapper);
                }
            };
        }
    }



}


package cn.edu.sustech.cs209.chatting.client;

/**
 * Sample Skeleton for 'Design.fxml' Controller Class
 */


import cn.edu.sustech.cs209.chatting.common.ChatBox;
import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChatRoomController implements Initializable  {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="BasicPane"
    private AnchorPane BasicPane; // Value injected by FXMLLoader

    @FXML // fx:id="basicVBox"
    private VBox basicVBox; // Value injected by FXMLLoader

    @FXML // fx:id="chatBoxList"
    private VBox chatBoxList; // Value injected by FXMLLoader

    @FXML // fx:id="chatBoxListView"
    private ListView<ChatBox> chatBoxListView; // Value injected by FXMLLoader

    @FXML // fx:id="createGroupChat"
    private MenuItem createGroupChat; // Value injected by FXMLLoader

    @FXML // fx:id="createPrivateChat"
    private MenuItem createPrivateChat; // Value injected by FXMLLoader

    @FXML // fx:id="currentTalkingText"
    private Text currentTalkingText; // Value injected by FXMLLoader

    @FXML // fx:id="menu"
    private Menu menu; // Value injected by FXMLLoader

    @FXML // fx:id="menuBar"
    private MenuBar menuBar; // Value injected by FXMLLoader

    @FXML // fx:id="messageDisplayArea"
    private AnchorPane messageDisplayArea; // Value injected by FXMLLoader

    @FXML // fx:id="messageListView"
    private ListView<Message> messageListView; // Value injected by FXMLLoader

    @FXML // fx:id="sendMeeage"
    private Button sendMeeage; // Value injected by FXMLLoader

    @FXML // fx:id="splitPane"
    private SplitPane splitPane; // Value injected by FXMLLoader

    @FXML // fx:id="textAncho"
    private AnchorPane textAncho; // Value injected by FXMLLoader

    @FXML // fx:id="textArea"
    private TextArea textArea; // Value injected by FXMLLoader

    private Socket socket;

    private User user;

    private Stage loginStage;

    private ObservableList<ChatBox> cardList;

    private ObservableList<Message> messageObservableList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



    }

    public void test() {
        cardList = FXCollections.observableArrayList();
        chatBoxListView.setItems(cardList);
        chatBoxListView.setCellFactory(new ChatBosFactory());
        messageObservableList = FXCollections.observableArrayList();
        messageListView.setItems(messageObservableList);
        messageListView.setCellFactory(new MessageCellFactory());

        Message message = new Message(new Date().getTime(), user.getName(), "0", "Test");

        messageObservableList.add(message);

        User user1 = new User("user!", "iiiii");
        ChatBox chatBox = new ChatBox();
        chatBox.setChatName("TestChat");
        chatBox.setId(1);
        chatBox.addUser(user1);
        chatBox.addUser(user);
        chatBox.addHistory(message);
        cardList.add(chatBox);
    }

    public void hideLogin() {
        loginStage.hide();
    }

    public void doSendMessage() {
        String data = textArea.getText();
        textArea.clear();
        System.out.println(data);
    }


    private class ChatBosFactory implements  Callback<ListView<ChatBox>, ListCell<ChatBox>> {

        @Override
        public ListCell<ChatBox> call(ListView<ChatBox> chatBoxListView) {
            return new ListCell<ChatBox>() {
                @Override
                protected void updateItem(ChatBox chatBox, boolean b) {
                    super.updateItem(chatBox, b);
                    if (chatBox == null) {
                        return;
                    }
                    HBox wrapper = new HBox();
                    Label nameLabel = new Label(chatBox.getChatName());
                    List<Message> History = chatBox.getHistory();
                    String latestMsg = History.get(History.size() - 1).getData();
                    String msgPlay = latestMsg.length() > 8 ? latestMsg.substring(0, 8) + "..." : latestMsg;
                    Label msgLabel = new Label(msgPlay);

                    wrapper.getChildren().addAll(nameLabel, msgLabel);
                    wrapper.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(wrapper);

                }
            };
        }
    }

    private class MessageCellFactory implements Callback<ListView<Message>, ListCell<Message>> {
        @Override
        public ListCell<Message> call(ListView<Message> param) {
            return new ListCell<Message>() {

                @Override
                public void updateItem(Message msg, boolean empty) {
                    super.updateItem(msg, empty);
                    if (empty || Objects.isNull(msg)) {
                        return;
                    }

                    HBox wrapper = new HBox();
                    Label nameLabel = new Label(msg.getSentBy());
                    Label msgLabel = new Label(msg.getData());

                    nameLabel.setPrefSize(50, 20);
                    nameLabel.setWrapText(true);
                    nameLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    if (user.getName().equals(msg.getSentBy())) {
                        wrapper.setAlignment(Pos.TOP_RIGHT);
                        wrapper.getChildren().addAll(msgLabel, nameLabel);
                        msgLabel.setPadding(new Insets(0, 20, 0, 0));
                    } else {
                        wrapper.setAlignment(Pos.TOP_LEFT);
                        wrapper.getChildren().addAll(nameLabel, msgLabel);
                        msgLabel.setPadding(new Insets(0, 0, 0, 20));
                    }

                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(wrapper);
                }
            };
        }
    }







    public void setLoginStage(Stage stage) {
        this.loginStage = stage;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }


}
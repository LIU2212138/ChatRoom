package cn.edu.sustech.cs209.chatting.client;

/**
 * Sample Skeleton for 'Design.fxml' Controller Class
 */


import cn.edu.sustech.cs209.chatting.common.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.*;

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

    private ChatBox currentChatBox;

    private MyObjectInputStream objectInputStream;
    private MyObjectOutputStream objectOutputStream;
    private Thread receiveDataThread;

    private List<User> currentOnlineUser;
    private int testCount = 0;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void init() throws IOException {
        objectOutputStream = new MyObjectOutputStream(socket.getOutputStream());
        cardList = FXCollections.observableArrayList();
        messageObservableList = FXCollections.observableArrayList();
        chatBoxListView.setItems(cardList);
        chatBoxListView.setCellFactory(new ChatBosFactory());
        messageListView.setItems(messageObservableList);
        messageListView.setCellFactory(new MessageCellFactory());

        receiveDataThread = new Thread(() -> {
            try {
                objectInputStream = new MyObjectInputStream(socket.getInputStream());
                while (true){
                    //TODO: 持续接收消息并解析，作出对应的操作(记得回顾server的操作)
                    Object o = objectInputStream.readObject();
                    System.out.println("Thread read Object");
                    Platform.runLater(() -> {
                        if (o instanceof Message) {
                            System.out.println("o is a message");
                            Message message = (Message) o;
                            String sentTo = message.getSendTo();
                            String sentBy = message.getSentBy();
                            long time = message.getTimestamp();
                            String data = message.getData();
                            if (sentTo.equals("0")) {

                            } else {
                                System.out.println("o sent to chat room");
                                System.out.println("user.getChatBoxList(): " + user.getChatBoxList());
                                for (ChatBox chatBox : user.getChatBoxList()) {
                                    System.out.println("chatBox.getId() = " + chatBox.getId());
                                    System.out.println("sentTo: " + sentTo);
                                    if (chatBox.getId() == Integer.parseInt(sentTo)) {
                                        chatBox.addHistory(message);
                                        if (chatBox.equals(currentChatBox)){
                                            flashMessages(chatBox);
                                        }
                                        System.out.println("add message to chatBox History");
                                    }
                                }
                                List<ChatBox> chatBoxes = user.getChatBoxList();
                                chatBoxes.sort((u1, u2) -> {
                                    List<Message> h1 = u1.getHistory();
                                    List<Message> h2 = u2.getHistory();
                                    Message m1 = h1.get(h1.size() - 1);
                                    Message m2 = h2.get(h2.size() - 1);
                                    if (m1.getTimestamp() < m2.getTimestamp()) {
                                        return 1;
                                    } else if (Objects.equals(m1.getTimestamp(), m2.getTimestamp())) {
                                        return 0;
                                    } else {
                                        return -1;
                                    }
                                });
                                cardList.clear();
                                cardList.addAll(chatBoxes);
                            }
                        }
                    });
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        receiveDataThread.start();

        //test---------
//        ChatBox chatBox = new ChatBox();
//        chatBox.setId(1);
//        chatBox.setChatName("TestChat0");
//        List<User> users = new ArrayList<>();
//        User user1 = new User("user1" , "123456");
//        users.add(user1);
//        users.add(user);
//        chatBox.setUsers(users);
//
//        ChatBox chatBox1 = new ChatBox();
//        chatBox1.setId(2);
//        chatBox1.setChatName("TestChat1");
//        List<User> users1 = new ArrayList<>();
//        User user2 = new User("user2" , "123456");
//        users.add(user1);
//        users.add(user);
//        users.add(user2);
//        chatBox1.setUsers(users);
//
//
//
//        Message message1 = new Message(new Date().getTime(), user.getName(), "2", "Test1");
//        Message message2 = new Message(new Date().getTime() + 1, user.getName(), "1", "Test2");
//        Message message3 = new Message(new Date().getTime() + 2, user.getName(), "2", "Test3");
//
//        chatBox1.addHistory(message1);
//        chatBox1.addHistory(message3);
//        chatBox.addHistory(message2);
////        chatBox.addHistory(new Message(new Date().getTime() + 3, user.getName(), "2", "Test4"));
//        user.addChatBox(chatBox1);
//        user.addChatBox(chatBox);

        //------------
        List<ChatBox> chatBoxes = user.getChatBoxList();
        chatBoxes.sort((u1, u2) -> {
           List<Message> h1 = u1.getHistory();
           List<Message> h2 = u2.getHistory();
           Message m1 = h1.get(h1.size() - 1);
           Message m2 = h2.get(h2.size() - 1);
           if (m1.getTimestamp() < m2.getTimestamp()) {
               return 1;
           } else if (Objects.equals(m1.getTimestamp(), m2.getTimestamp())) {
               return 0;
           } else {
               return -1;
           }
        });

        cardList.clear();
        cardList.addAll(chatBoxes);

        ChatBox latestChatBox = chatBoxes.get(0);
        List<Message> selectMessages = latestChatBox.getHistory();
        messageObservableList.clear();
        messageObservableList.addAll(selectMessages);
        MultipleSelectionModel<ChatBox> selectionModel = chatBoxListView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

    }

    void CreatePrivateChat() throws IOException {
        Thread createPrivateChatThread = new Thread(() -> {
            try {
                if (socket.isConnected()) {
                    objectOutputStream.writeObject("GETAU");
                    objectOutputStream.flush();
                } else {
                    socket = new Socket("localhost", 8080);
                    objectOutputStream = new MyObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new MyObjectInputStream(socket.getInputStream());
                    objectOutputStream.writeObject("GETAU");
                }

                Object o = objectInputStream.readObject();
                if (o instanceof List) {
                    currentOnlineUser = (List<User>) o;
                    FXMLLoader fxmlLoader = new FXMLLoader(privateChooseControllerApplication.class.getResource("designPrivateChooseController.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(),  251.0, 494.0);
                    privateChooseController privateChooseController = fxmlLoader.getController();
                    privateChooseController.init(currentOnlineUser, user);
                    Stage newStage = new Stage();
                    newStage.setScene(scene);
                    newStage.setTitle("Create A private Chat");
                    newStage.show();
                    User chooseUser = privateChooseController.getChooseUser();
                    while (chooseUser == null) {
                        chooseUser = privateChooseController.getChooseUser();
                    }
                    // TODO: 发送报文申请创建新的对话，检测返回的对话是否已存在，若存在，则直接使用之前的会话，否则创建新的
                    objectOutputStream.writeObject(new Message(new Date().getTime(), user.getName(),
                            "0", "CREATE " + user.getName() + "," + chooseUser.getName()));
                    // TODO: 注意返回的报文在报文接收线程中处理，可以创一个新的static变量，循环检测这个变量是否为空

                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        });

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
        cardList.clear();
        cardList.add(chatBox);
    }

    public void hideLogin() {
        loginStage.hide();
    }


    public void changeChatBox() {
        MultipleSelectionModel<ChatBox> selectionModel = chatBoxListView.getSelectionModel();
        currentChatBox = selectionModel.getSelectedItem();
        flashMessages(currentChatBox);
        System.out.println(currentChatBox.getChatName());
    }
    public void flashMessages(ChatBox chatBox) {
        messageObservableList.clear();
        List<Message> messageList = chatBox.getHistory();
        messageListView.getSelectionModel().clearSelection();
        messageListView.getItems().clear();
        messageObservableList.setAll(messageList);
        System.out.println(messageListView.getItems());
    }

    public void doSendMessage() throws IOException {
        String data = textArea.getText();
        textArea.clear();
        Message message = new Message(new Date().getTime(), user.getName(), "1", data);
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();
    }


    private static class ChatBosFactory implements  Callback<ListView<ChatBox>, ListCell<ChatBox>> {
        @Override
        public ListCell<ChatBox> call(ListView<ChatBox> chatBoxListView) {
            return new ListCell<ChatBox>() {
                @Override
                protected void updateItem(ChatBox chatBox, boolean b) {
                    super.updateItem(chatBox, b);
                    if (b || Objects.isNull(chatBox)) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }
                    VBox wrapper = new VBox();
                    Label nameLabel = new Label(chatBox.getChatName() + "\n");
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
                        setText(null);
                        setGraphic(null);
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
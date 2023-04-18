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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.PopupControl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.*;
import com.madeorsk.emojisfx.*;

import static com.sun.javafx.scene.control.skin.Utils.computeTextHeight;

public class ChatRoomController implements Initializable  {


    @FXML
    private Button exitChatRoomButton;

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


    @FXML // fx:id="textArea"
    private TextArea textArea; // Value injected by FXMLLoader

    @FXML
    private Button getGroupMenber;

    private Socket socket;

    private User user;

    private Stage loginStage;

    private ObservableList<ChatBox> cardList;

    private ObservableList<Message> messageObservableList;

    private ChatBox currentChatBox;

    private MyObjectInputStream objectInputStream;
    private MyObjectOutputStream objectOutputStream;
    private Thread receiveDataThread;
    private boolean hasReceivedNewChatBox;
    private List<User> currentOnlineUser;

    public List<User> choosedUser;

    public List<String> chatName;
    private Stage selfStage;

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
        hasReceivedNewChatBox = false;
        choosedUser = new ArrayList<>();
        chatName = new ArrayList<>();
        runReceiver();

        List<ChatBox> chatBoxes = user.getChatBoxList();
        System.out.println(chatBoxes);
        sortChatBoxes(chatBoxes);

        cardList.clear();
        cardList.addAll(chatBoxes);
        List<Message> selectMessages;
        if (chatBoxes.size() != 0) {
            ChatBox latestChatBox = chatBoxes.get(0);
            selectMessages = latestChatBox.getHistory();
            currentChatBox = latestChatBox;
            chatBoxListView.getSelectionModel().select(0);
        } else {
            selectMessages = new ArrayList<>();
        }
        messageObservableList.clear();
        messageObservableList.addAll(selectMessages);
        MultipleSelectionModel<ChatBox> selectionModel = chatBoxListView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        // 退出程序事件
        try {
            selfStage.setOnCloseRequest(event -> {
                try {
                    objectOutputStream.writeObject(new Message(new Date().getTime(), user.getName(),
                            "0", "EXIT " + user.getName()));
                    objectInputStream.close();
                    objectOutputStream.close();
                    socket.close();
                    receiveDataThread.interrupt();
                    selfStage.close();
                    Platform.exit();
                    System.exit(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
            Platform.exit();
            System.exit(-1);
        }
    }

    public void CreatePrivateChat() throws IOException {
        Thread createPrivateChatThread = new Thread(() -> {
            try {
                if (socket.isConnected()) {
                    objectOutputStream.writeObject(new Message(new Date().getTime(), user.getName(),
                            "0", "GETAU"));
                    objectOutputStream.flush();
                } else {
                    socket = new Socket("localhost", 8080);
                    objectOutputStream = new MyObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new MyObjectInputStream(socket.getInputStream());
                    objectOutputStream.writeObject(new Message(new Date().getTime(), user.getName(),
                            "0", "GETAU"));
                }
                while (true) {
                    if (currentOnlineUser != null) {
                        break;
                    }
                }
                System.out.println("GET the CurrentOnlineUsers");
                FXMLLoader fxmlLoader = new FXMLLoader(privateChooseControllerApplication.class.getResource("designPrivateChooseController.fxml"));
                Scene scene = new Scene(fxmlLoader.load(),  251.0, 494.0);
                privateChooseController privateChooseController = fxmlLoader.getController();
                privateChooseController.init(currentOnlineUser, user, choosedUser);
                Platform.runLater(() -> {
                    Stage newStage = new Stage();
                    newStage.setScene(scene);
                    newStage.setTitle("Create A private Chat");
                    newStage.setOnCloseRequest(event -> {
                        choosedUser.add(null);
                    });
                    newStage.show();
                });

                User chooseUser ;
                while (true) {
                    if (choosedUser.size() != 0) {
                        chooseUser = choosedUser.get(0);
                        System.out.println(choosedUser);
                        break;
                    } else {
                        System.out.println(choosedUser);
                    }
                }
                if (chooseUser != null) {
                    System.out.println("Get the chose user");

                    objectOutputStream.writeObject(new Message(new Date().getTime(), user.getName(),
                            "0", "CREATE " + user.getName() + "," + chooseUser.getName()
                            + " " + chooseUser.getName()));

                    System.out.println("Send the create message");
                    while (true) {
                        if (hasReceivedNewChatBox) {
                            break;
                        }
                    }
                    System.out.println("CREATE SUCCESSFULLY");
                }
                hasReceivedNewChatBox = false;
                currentOnlineUser = null;
                choosedUser.clear();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        });
        createPrivateChatThread.start();
    }

    public void createGroupChat() {
        Thread createPrivateChatThread = new Thread(() -> {
            try {
                if (socket.isConnected()) {
                    objectOutputStream.writeObject(new Message(new Date().getTime(), user.getName(),
                            "0", "GETAU"));
                    objectOutputStream.flush();
                } else {
                    socket = new Socket("localhost", 8080);
                    objectOutputStream = new MyObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new MyObjectInputStream(socket.getInputStream());
                    objectOutputStream.writeObject(new Message(new Date().getTime(), user.getName(),
                            "0", "GETAU"));
                }
                while (true) {
                    if (currentOnlineUser != null) {
                        break;
                    }
                }
                System.out.println("GET the CurrentOnlineUsers");
                FXMLLoader fxmlLoader = new FXMLLoader(privateChooseControllerApplication.class.getResource("designGroupChooseController.fxml"));
                Scene scene = new Scene(fxmlLoader.load(),  234.0, 535.0);
                groupChooseController groupChooseController = fxmlLoader.getController();
                groupChooseController.init(currentOnlineUser, user, choosedUser, chatName, user.getName());
                Platform.runLater(() -> {
                    Stage newStage = new Stage();
                    newStage.setScene(scene);
                    newStage.setTitle("Create A group Chat");
                    newStage.setOnCloseRequest(event -> {
                        choosedUser.add(null);
                    });
                    newStage.show();
                });

                List<User> chooseUser ;
                while (true) {
                    if (choosedUser.size() != 0) {
                        if (choosedUser.get(0) != null) {
                            chooseUser = choosedUser;
                            System.out.println(choosedUser);
                        } else {
                            chooseUser = null;
                        }
                        break;
                    } else {
//                        System.out.println(choosedUser);
                    }
                }

                //  发送报文申请创建新的对话，检测返回的对话是否已存在，若存在，则直接使用之前的会话，否则创建新的
                StringBuilder data = new StringBuilder();
                if (chooseUser != null) {
                    System.out.println("Get the chose user");
                    for (User value : chooseUser) {
                        data.append(value.getName());
                        data.append(",");
                    }
                    data.append(user.getName());
                    objectOutputStream.writeObject(new Message(new Date().getTime(), user.getName(),
                            "0", "CREATE " + data.toString()
                            + " " + chatName.get(0)));
                    System.out.println(chatName.get(0));
                    //  注意返回的报文在报文接收线程中处理，可以创一个新的static变量，循环检测这个变量是否为空

                    System.out.println("Send the create message");
                    while (true) {
                        if (hasReceivedNewChatBox) {
                            break;
                        }
                    }
                    System.out.println("CREATE SUCCESSFULLY");
                }
                hasReceivedNewChatBox = false;
                currentOnlineUser = null;
                choosedUser.clear();
                chatName.clear();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        createPrivateChatThread.start();
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
        if (currentChatBox == null) {
            return;
        }
        flashMessages(currentChatBox);
        currentTalkingText.setText("      Current Talk With: " + currentChatBox.getChatName());
        System.out.println(currentChatBox.getChatName());
    }
    public void flashMessages(ChatBox chatBox) {
        messageObservableList.clear();
        if (chatBox == null) {
            return;
        }
        List<Message> messageList = chatBox.getHistory();
        messageListView.getSelectionModel().clearSelection();
        messageListView.getItems().clear();
        messageObservableList.setAll(messageList);
        System.out.println(messageListView.getItems());
    }

    public void flashChatBox() {
        List<ChatBox> chatBoxes = user.getChatBoxList();
        sortChatBoxes(chatBoxes);
        cardList.clear();
        cardList.addAll(chatBoxes);
    }

    public void doSendMessage() throws IOException, InterruptedException {
        String data = textArea.getText();
        if (currentChatBox == null) {
            return;
        }
        if (data.equals("")) {
            return;
        }
        System.out.println(user);
        System.out.println(currentChatBox);
        Message message = new Message(new Date().getTime(), user.getName(), String.valueOf(currentChatBox.getId()), data);
        try{
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
            textArea.clear();
        } catch (SocketException e) {
            // 创建文本和按钮
            Text text = new Text("Network Error, please re-login later");
            Button button = new Button("Yes");

            // 创建一个垂直布局容器，将文本和按钮添加到其中
            VBox vbox = new VBox(10, text, button);
            vbox.setAlignment(Pos.CENTER);

            // 创建一个新的场景，将VBox作为根节点添加到其中
            Scene scene = new Scene(vbox, 300, 200);

            // 将场景设置为新的舞台，并将舞台设置为模态
            Stage popup = new Stage();
            popup.initOwner(selfStage);
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setScene(scene);

            // 将弹出窗口显示出来
            popup.show();

            // 给按钮添加点击事件处理器
            button.setOnAction(event -> {
                // 在点击按钮时关闭弹出窗口
                popup.close();
                System.exit(0);
            });
            popup.setOnCloseRequest(event -> {
                System.exit(0);
            });

        }
    }

    public void getGroupMenber() {
        ObservableList<User> users = FXCollections.observableArrayList();
        users.addAll(currentChatBox.getUsers());
        ListView<User> listView = new ListView<>();
        listView.setItems(users);
        listView.setCellFactory(new UserFactory());
        VBox vBox = new VBox();
        vBox.getChildren().addAll(listView);
        // 创建一个新的场景并将布局添加到场景中
        Scene scene = new Scene(vBox, 250, 400);

        // 创建一个新的窗口并将场景添加到窗口中
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Group Members in: " + currentChatBox.getChatName());
        stage.setOnCloseRequest(event -> {
            stage.close();
        });
        stage.show();
    }

    public void exitChatRoom() {
        Text text = new Text("Do you really want to EXIT the chat room?");
        Button buttonYse = new Button("Yes");
        Button buttonNo = new Button("No");
        // 创建一个垂直布局容器，将文本和按钮添加到其中
        VBox vbox = new VBox(10, text, buttonYse, buttonNo);
        vbox.setAlignment(Pos.CENTER);

        // 创建一个新的场景，将VBox作为根节点添加到其中
        Scene scene = new Scene(vbox, 400, 180);

        // 将场景设置为新的舞台，并将舞台设置为模态
        Stage popup = new Stage();
        popup.initOwner(selfStage);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setScene(scene);

        // 将弹出窗口显示出来
        popup.show();

        // 给按钮添加点击事件处理器
        buttonYse.setOnAction(event -> {
            // TODO: 发送删除报文。
            Message deleteMessage = new Message(new Date().getTime(),
                    String.valueOf(user.getId()),"0",
                    "DELETE " + String.valueOf(user.getId()) + " " + String.valueOf(currentChatBox.getId()));
            try {
                objectOutputStream.writeObject(deleteMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            popup.close();
        });
        buttonNo.setOnAction(evet -> {
            popup.close();
        });
        popup.setOnCloseRequest(event -> {
            // TODO: 什么都不作
            popup.close();
        });
    }

    private void runReceiver() {
        receiveDataThread = new Thread(() -> {
            try {
                System.out.println("Thread start");

                objectInputStream = new MyObjectInputStream(socket.getInputStream());
                System.out.println("create objectInputStream");
                while (!Thread.currentThread().isInterrupted()){
                    //TODO: 持续接收消息并解析，作出对应的操作(记得回顾server的操作)
                    if (socket.isConnected()) {
                        System.out.println("is connected");

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
                                if (sentBy.equals("0")) {
                                    String leaveUserName = data.split(" ")[0];
                                    if (user.getName().equals(leaveUserName)) {
                                        user.getChatBoxList().removeIf(chatBox -> chatBox.getId() == Integer.parseInt(sentTo));
                                        flashChatBox();
                                        if (user.getChatBoxList().size() > 0) {
                                            currentChatBox = user.getChatBoxList().get(0);
                                            currentTalkingText.setText("      Current Talk With: " + currentChatBox.getChatName());
                                        } else {
                                            currentChatBox = new ChatBox();
                                            currentTalkingText.setText("No Chat");
                                        }
                                        flashMessages(currentChatBox);
                                    } else {
                                        for (ChatBox chatBox : user.getChatBoxList()) {
                                            System.out.println("chatBox.getId() = " + chatBox.getId());
                                            System.out.println("sentTo: " + sentTo);
                                            if (chatBox.getId() == Integer.parseInt(sentTo)) {
                                                chatBox.addHistory(message);
                                                chatBox.getUsers().removeIf(user1 -> user1.getName().equals(leaveUserName));
                                                if (chatBox.equals(currentChatBox)){
                                                    flashMessages(chatBox);
                                                }
                                                System.out.println("add message to chatBox History");
                                            }
                                        }
                                        flashChatBox();
                                    }
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
                                    flashChatBox();
                                }
                            } else if (o instanceof ChatBox) {
                                System.out.println("Received a chatBox");
                                ChatBox chatBox = (ChatBox) o;
                                user.addChatBox(chatBox);
                                hasReceivedNewChatBox = true;
                                flashChatBox();
                                System.out.println("Create ChatBox");
                            } else if (o instanceof List) {
                                currentOnlineUser = (List<User>) o;
                            }
                        });
                    } else {
                        System.out.println("Socked is not connected, thread exit.");
                        break;
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Socked is not connected, thread interrupt.");
                Thread.currentThread().interrupt();
            }
        });
        receiveDataThread.start();
    }


    private static class UserFactory implements Callback<ListView<User>, ListCell<User>> {

        @Override
        public ListCell<User> call(ListView<User> userListView) {
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
                    Label nameLabel = new Label(user.getName());
                    nameLabel.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
                    wrapper.getChildren().addAll(nameLabel);
                    wrapper.setAlignment(Pos.TOP_CENTER);
                    setGraphic(wrapper);
                }
            };
        }
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
                    String msgPlay = "";
                    if (History.size() != 0) {
                        String latestMsg = History.get(History.size() - 1).getData();
                        msgPlay = latestMsg.length() > 8 ? latestMsg.substring(0, 8) + "..." : latestMsg;
                    }
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
//                        setMinHeight(0);
//                        setMaxHeight(0);
                        return;
                    }



                    HBox wrapper = new HBox();
                    int lines = msg.getData().split("\n").length;
                    System.out.println(lines);
                    Label nameLabel = new Label(msg.getSentBy());
                    Label msgLabel = new Label(msg.getData());
//                    msgLabel.setPrefHeight(lines * 40);
//                    setPrefHeight();
//                    setHeight(lines * 25);
                    nameLabel.setPrefSize(50, 20);
                    nameLabel.setWrapText(true);
                    nameLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

                    if (user.getName().equals(msg.getSentBy())) {
                        wrapper.setAlignment(Pos.TOP_RIGHT);
                        wrapper.getChildren().addAll(msgLabel, nameLabel);
                        msgLabel.setPadding(new Insets(0, 20, 0, 0));
                    } else if (msg.getSentBy().equals("0")) {
                        nameLabel = new Label("Notification: ");
                        nameLabel.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                        msgLabel.setStyle("-fx-border-color: blue;");
                        wrapper.setAlignment(Pos.TOP_CENTER);
                        wrapper.getChildren().addAll(nameLabel, msgLabel);
                        msgLabel.setPadding(new Insets(0, 0, 0, 20));
                    } else {
                        wrapper.setAlignment(Pos.TOP_LEFT);
                        wrapper.getChildren().addAll(nameLabel, msgLabel);
                        msgLabel.setPadding(new Insets(0, 0, 0, 20));
                    }

                    setMinHeight(Control.USE_PREF_SIZE);
                    setMaxHeight(Double.MAX_VALUE);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(wrapper);
                }
            };
        }
    }



    public void sortChatBoxes(List<ChatBox> chatBoxes) {
        chatBoxes.sort((u1, u2) -> {
            List<Message> h1 = u1.getHistory();
            List<Message> h2 = u2.getHistory();
            if (h1.size() == 0) {
                return -1;
            } else if (h2.size() == 0) {
                return 1;
            }
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

    public void setCurrentOnlineUser(List<User> users) {
        currentOnlineUser = users;
    }
    public void setSelfStage(Stage selfStage) {
        this.selfStage = selfStage;
    }

}
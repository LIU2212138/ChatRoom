package cn.edu.sustech.cs209.chatting.server;

import static cn.edu.sustech.cs209.chatting.server.Server.*;

import cn.edu.sustech.cs209.chatting.common.*;
import java.io.*;
import java.net.Socket;
import java.util.*;



public class SocketThread implements Runnable {
    private Socket socket;

    private final Service service;

    public SocketThread(Socket socket, Service service) {
        this.socket = socket;
        this.service = service;
    }
    @Override
    public void run() {
        try {
            MyObjectInputStream objectInputStream = new MyObjectInputStream(socket.getInputStream());
            while (!Thread.currentThread().isInterrupted()) {
                Message message = (Message) objectInputStream.readObject();
                System.out.println("received: " + message.getData());
                int id = Integer.parseInt(message.getSendTo());
                if (id != 0) {
                    System.out.println("received a chat message" + message.getData());
                    ChatBox current = service.selectChatBoxById(id);
                    message.setId(MSG_IDGENERATOR++);
                    service.insertMessage(message);
                    current.addHistory(message);
                    service.updateHistoryIdList(current.getHistoryIdList(), current.getId());
                    for (User user : current.getUsers()) {
                        System.out.println(user);
                        Socket socket1 = userSocMap.get(user.getName());
                        if (socket1 != null) {
                            if (socket1.isConnected()) {
                                sendMessage(socket1, message);
                            }
                        }
                    }
                } else {
                    String data = message.getData();
                    String[] dataP = data.split(" ");
                    switch (dataP[0]) {
                        // HISTORY 16
                        case "HISTORY": {
                            int CBID = Integer.parseInt(dataP[1]);
                            List<Message> History = service.selectMessageByDest(String.valueOf(CBID));
                            sendMessage(socket, History);
                            break;
                        }
                        // CREATE user1,user2,user3 chatName
                        case "CREATE":   {
                            String[] names = dataP[1].split(",");
                            List<User> createUser = new ArrayList<>();
                            List<Integer> createrUserID = new ArrayList<>();
                            for (String name : names) {
                                User temp = service.selectUserByName(name);
                                createUser.add(temp);
                                createrUserID.add(temp.getId());
                            }
                            boolean isAlreadyExist = false;
                            int mayBeID = 0;
                            List<ChatBox> allChatBox = service.selectAllBox();
                            for (ChatBox chatBox : allChatBox) {
                                if (new HashSet<>(chatBox.getUserIdList()).containsAll(createrUserID)
                                        && chatBox.getUserIdList().size() == createrUserID.size()) {
                                    isAlreadyExist = true;
                                    mayBeID = chatBox.getId();
                                    break;
                                }
                            }
                            if (!isAlreadyExist) {
                                ChatBox newChat = new ChatBox();
                                newChat.setUsers(createUser);
                                newChat.setUserIdList(createrUserID);
                                newChat.setId(CHAT_IDGENERATOR);
                                newChat.setChatName(dataP[2]);
                                service.insertChatBox(newChat);
                                CHAT_IDGENERATOR++;
                                for (User user : createUser) {
                                    Socket socket1 = userSocMap.get(user.getName());
                                    user.addChatBox(newChat);
                                    service.updateIndexOfChatBoxList(
                                            user.getIndexOfChatBoxList(), user.getId());
                                    if (socket1 != null) {
                                        if (socket1.isConnected()) {
                                            sendMessage(socket1, newChat);
                                        }
                                    }
                                }
                            } else {
                                ChatBox chatBox = service.selectChatBoxById(mayBeID);
                                for (User user : createUser) {
                                    Socket socket1 = userSocMap.get(user.getName());
                                    if (socket1 != null) {
                                        if (socket1.isConnected()) {
                                            sendMessage(socket1, chatBox);
                                        }
                                    }

                                }
                            }
                            System.out.println("Sever Create Chat successfully");
                            break;
                        }
                        // LOGIN name password
                        case "LOGIN": {
                            if (dataP.length != 3) {
                                sendMessage(socket, "User Name or password can not contain BLANKSPACE");
                            }
                            //User targetUser = nameUserMap.get(dataP[1]);
                            // 用户不存在则注册
                            User targetUser = service.selectUserByName(dataP[1]);
                            if (targetUser == null) {
                                targetUser = new User(dataP[1], dataP[2]);
                                targetUser.setId(USER_IDGENERATOR++);
//                                nameUserMap.put(dataP[1], targetUser);
                                boolean check = service.insertUser(targetUser);
                                System.out.println(check);
                                System.out.println(service.selectUserById(1));
                                userSocMap.put(dataP[1], socket);
                                sendMessage(socket, targetUser);
                            } else {
                                if (targetUser.getPassWord().equals(dataP[2])) {
                                    userSocMap.put(dataP[1], socket);
                                    sendMessage(socket, targetUser);
                                } else {
                                    sendMessage(socket, "Password wrong!");
                                }
                            }
                            break;
                        }
                        //EXIT username
                        case "EXIT": {
                            userSocMap.remove(dataP[1]);
                            objectInputStream.close();
                            Thread.currentThread().interrupt();
                            System.out.println("socked is closed");
                            break;
                        }
                        case "GETAU": {
                            System.out.println("Received GETAU");
                            List<User> users = new ArrayList<>();
                            for (String name : userSocMap.keySet()) {
                                users.add(service.selectUserByName(name));
                            }
                            sendMessage(socket, users);
                            break;
                        }
                        //delete Userid ChatBoxId
                        case "DELETE": {
                            int userId = Integer.parseInt(dataP[1]);
                            int chatBoxId = Integer.parseInt(dataP[2]);
                            User user = service.selectUserById(userId);
                            List<Integer> chatBoxListForThisUser = user.getIndexOfChatBoxList();
                            if (chatBoxListForThisUser.contains(chatBoxId)) {
                                chatBoxListForThisUser.remove(Integer.valueOf(chatBoxId));
                            }
                            List<ChatBox> chatBoxList = user.getChatBoxList();
                            chatBoxList.removeIf(chatBox -> chatBox.getId() == chatBoxId);
                            service.updateIndexOfChatBoxList(chatBoxListForThisUser, userId);

                            ChatBox chatBox = service.selectChatBoxById(chatBoxId);
                            chatBox.getUsers().removeIf(user1 -> user1.getId() == userId);
                            chatBox.getUserIdList().remove(Integer.valueOf(userId));
                            if (chatBox.getUserIdList().size() == 0) {
                                service.deleteChatBox(chatBoxId);
                                Message returnMessage = new Message(new Date().getTime(),
                                        "0", String.valueOf(chatBoxId),
                                        user.getName() + " exit the chat room.");
                                sendMessage(socket, returnMessage);
                            } else {
                                service.updateUserIdList(chatBox.getUserIdList(), chatBoxId);
                                Message returnMessage = new Message(new Date().getTime(),
                                        "0", String.valueOf(chatBoxId),
                                        user.getName() + " exit the chat room.");
                                returnMessage.setId(MSG_IDGENERATOR++);
                                service.insertMessage(returnMessage);
                                chatBox.addHistory(returnMessage);
                                service.updateHistoryIdList(chatBox.getHistoryIdList(), chatBoxId);
                                for (User userSend : chatBox.getUsers()) {
                                    System.out.println(userSend);
                                    Socket socket1 = userSocMap.get(userSend.getName());
                                    if (socket1 != null) {
                                        if (socket1.isConnected()) {
                                            sendMessage(socket1, returnMessage);
                                        }
                                    }
                                }
                                sendMessage(socket, returnMessage);
                            }
                            break;
                        }
                        default: {
                            System.out.println("Error");
                            break;
                        }
                    }
                }
            }
            System.out.println("EXIT the thread");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(Socket socketToSend, Object o) throws IOException {
        MyObjectOutputStream objectOutputStream = new MyObjectOutputStream(
                socketToSend.getOutputStream());
        objectOutputStream.writeObject(o);
        objectOutputStream.flush();
    }
}

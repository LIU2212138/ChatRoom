package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.edu.sustech.cs209.chatting.server.Server.*;

public class SocketThread implements Runnable{
    private Socket socket;

    private final Service service;

    public SocketThread(Socket socket, Service service){
        this.socket = socket;
        this.service = service;
    }
    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        PrintWriter writer = null;
        try {
            MyObjectInputStream objectInputStream = new MyObjectInputStream(socket.getInputStream());
            while (!Thread.currentThread().isInterrupted()) {
                Message message = (Message) objectInputStream.readObject();
                System.out.println("received: " + message.getData());
                int id = Integer.parseInt(message.getSendTo());
                if (id != 0) {
                    System.out.println("received a chat message" + message.getData());
//                    ChatBox current = idCBMap.get(id);
                    ChatBox current = service.selectChatBoxById(id);
                    message.setId(MSG_IDGENERATOR++);
                    service.insertMessage(message);
                    current.addHistory(message);
//                    idCBMap.replace(current.getId(), current);
                    service.updateHistoryIdList(current.getHistoryIdList(), current.getId());
//                    sendMessage(socket, message);
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
//                            List<Message> History = idCBMap.get(CBID).getHistory();
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
                                if (new HashSet<>(chatBox.getUserIdList()).containsAll(createrUserID) &&
                                        chatBox.getUserIdList().size() == createrUserID.size()) {
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
//                                idCBMap.put(CHAT_IDGENERATOR, newChat);
                                service.insertChatBox(newChat);
                                CHAT_IDGENERATOR++;
                                for (User user : createUser) {
                                    Socket socket1 = userSocMap.get(user.getName());
                                    user.addChatBox(newChat);
                                    System.out.println("user.getIndexOfChatBoxList() = " + user.getIndexOfChatBoxList());
                                    service.updateIndexOfChatBoxList(user.getIndexOfChatBoxList(), user.getId());
                                    if (socket1 != null) {
                                        if (socket1.isConnected()) {
                                            sendMessage(socket1, newChat);
                                        }
                                    }
                                }
                            } else {
//                                ChatBox chatBox = idCBMap.get(mayBeID);
                                ChatBox chatBox = service.selectChatBoxById(mayBeID);
                                for (User user : createUser) {
                                    Socket socket1 = userSocMap.get(user.getName());
                                    sendMessage(socket1, chatBox);
                                }
                            }
                            System.out.println("Sever Create Chat successfully");
                            break;
                        }
                        // LOGIN name password
                        case "LOGIN" : {
                            if (dataP.length != 3){
                                sendMessage(socket, "User Name or password can not contain BLANKSPACE");
                            }
//                            User targetUser = nameUserMap.get(dataP[1]);
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
                        case "EXIT" : {
                            userSocMap.remove(dataP[1]);
                            objectInputStream.close();
                            Thread.currentThread().interrupt();
                            System.out.println("socked is closed");
                            break;
                        }
                        case "GETAU" : {
                            System.out.println("Received GETAU");
                            List<User> users = new ArrayList<>();
                            for (String name : userSocMap.keySet()) {
                                users.add(service.selectUserByName(name));
                            }
                            sendMessage(socket, users);
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
        MyObjectOutputStream objectOutputStream = new MyObjectOutputStream(socketToSend.getOutputStream());
        objectOutputStream.writeObject(o);
        objectOutputStream.flush();
    }
}

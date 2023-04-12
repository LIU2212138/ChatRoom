package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.ChatBox;
import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static cn.edu.sustech.cs209.chatting.server.Server.*;

public class SocketThread implements Runnable{
    private Socket socket;

    public SocketThread(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        PrintWriter writer = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            while (!socket.isClosed() || socket.isConnected()) {
                Message message = (Message) objectInputStream.readObject();
                int id = Integer.parseInt(message.getSendTo());
                if (id != 0) {
                    ChatBox current = idCBMap.get(id);
                    List<Message> curHistory = current.getHistory();
                    curHistory.add(message);
                    current.setHistory(curHistory);
                    idCBMap.replace(current.getId(), current);
                    for (User user : current.getUsers()) {
                        Socket socket1 = userSocMap.get(user.getName());
                        sendMessage(socket1, message);
                    }
                } else {
                    String data = message.getData();
                    String[] dataP = data.split(" ");
                    switch (dataP[0]) {
                        // HISTORY 16
                        case "HISTORY": {
                            int CBID = Integer.parseInt(dataP[1]);
                            List<Message> History = idCBMap.get(CBID).getHistory();
                            sendMessage(socket, History);
                            break;
                        }
                        // CREATE user1,user2,user3 chatName
                        case "CREATE":   {
                            String[] names = dataP[1].split(",");
                            List<User> createUser = new ArrayList<>();
                            for (String name : names) {
                                createUser.add(nameUserMap.get(name));
                            }
                            boolean isAlreadyExist = false;
                            int mayBeID = 0;
                            for (ChatBox chatBox : idCBMap.values()) {
                                if (new HashSet<>(chatBox.getUsers()).containsAll(createUser) &&
                                        chatBox.getUsers().size() == createUser.size() ) {
                                    isAlreadyExist = true;
                                    mayBeID = chatBox.getId();
                                    break;
                                }
                            }
                            if (!isAlreadyExist) {
                                ChatBox newChat = new ChatBox();
                                newChat.setUsers(createUser);
                                newChat.setId(IDGENERATOR);
                                newChat.setChatName(dataP[2]);
                                idCBMap.put(IDGENERATOR, newChat);
                                IDGENERATOR++;
                                for (User user : createUser) {
                                    Socket socket1 = userSocMap.get(user.getName());
                                    sendMessage(socket1, newChat);
                                }

                            } else {
                                ChatBox chatBox = idCBMap.get(mayBeID);
                                for (User user : createUser) {
                                    Socket socket1 = userSocMap.get(user.getName());
                                    sendMessage(socket1, chatBox);
                                }
                            }
                            break;
                        }
                        // LOGIN name password
                        case "LOGIN" : {
                            User targetUser = nameUserMap.get(dataP[1]);
                            // 用户不存在则注册
                            if (targetUser == null) {
                                targetUser = new User(dataP[1], dataP[2]);
                                nameUserMap.put(dataP[1], targetUser);
                                userSocMap.put(dataP[1], socket);
                                sendMessage(socket, targetUser);
                            } else {
                                if (targetUser.getPassWord().equals(dataP[2])) {
                                    userSocMap.put(dataP[1], socket);
                                    sendMessage(socket, targetUser);
                                } else {
                                    sendMessage(socket, "Password wrong");
                                    socket.close();
                                }
                            }
                            break;
                        }
                        case "EXIT username" : {
                            userSocMap.remove(dataP[1]);
                            sendMessage(socket, "CLOSE");
                            socket.close();
                            break;
                        }
                        default: {
                            System.out.println("Error");
                            break;
                        }
                    }
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void sendMessage(Socket socketToSend, Object o) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socketToSend.getOutputStream());
        objectOutputStream.writeObject(o);
        objectOutputStream.flush();
    }
}

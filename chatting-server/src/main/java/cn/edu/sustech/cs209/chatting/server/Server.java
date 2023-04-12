package cn.edu.sustech.cs209.chatting.server;


import cn.edu.sustech.cs209.chatting.common.ChatBox;
import cn.edu.sustech.cs209.chatting.common.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Server {
    public static int IDGENERATOR = 1;
    public static List<Socket> socketList = new ArrayList<>(20);
    public static Map<String, Socket> userSocMap = new HashMap<>();
    public static Map<Integer, ChatBox> idCBMap = new HashMap<>();
    public static Map<String, User> nameUserMap = new HashMap<>();

    public void SeverRun () throws IOException, ClassNotFoundException {
        try(ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                try {
                    Socket socket= serverSocket.accept();
                    new Thread(new SocketThread(socket)).start();
                    System.out.println("连接成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

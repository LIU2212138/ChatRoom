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
    public static int CHAT_IDGENERATOR = 1;
    public static int USER_IDGENERATOR = 1;
    public static int MSG_IDGENERATOR = 1;
    public static List<Socket> socketList = new ArrayList<>(20);
    public static Map<String, Socket> userSocMap = new HashMap<>();
    public static Map<Integer, ChatBox> idCBMap = new HashMap<>();
    public static Map<String, User> nameUserMap = new HashMap<>();

    public Service service = new Service();

    public Server() throws IOException {
        Integer maxMsg = service.selectMaxIdForMsg();
        Integer maxUsr = service.selectMaxIdForUsr();
        Integer maxCBx = service.selectMaxIdForCBx();
        CHAT_IDGENERATOR = maxCBx == null ? 1  : maxCBx + 1;
        USER_IDGENERATOR = maxUsr == null ? 1 : maxUsr + 1;
        MSG_IDGENERATOR = maxMsg == null ? 1 : maxMsg + 1;
        System.out.println(CHAT_IDGENERATOR);
        System.out.println(USER_IDGENERATOR);
        System.out.println(MSG_IDGENERATOR);

    }

    public void SeverRun() throws IOException, ClassNotFoundException {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new Thread(new SocketThread(socket, service)).start();
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

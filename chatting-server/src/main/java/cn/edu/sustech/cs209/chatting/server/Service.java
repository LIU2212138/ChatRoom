package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.ChatBox;
import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.User;
import cn.edu.sustech.cs209.chatting.server.Dao.ChatBoxDao;
import cn.edu.sustech.cs209.chatting.server.Dao.MessageDao;
import cn.edu.sustech.cs209.chatting.server.Dao.UserDao;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class Service {
    String resource = "mybatis-config.xml";
    InputStream inputStream = Resources.getResourceAsStream(resource);
    SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    SqlSession session = sqlSessionFactory.openSession(true);
    UserDao userDao = session.getMapper(UserDao.class);
    MessageDao messageDao = session.getMapper(MessageDao.class);
    ChatBoxDao chatBoxDao = session.getMapper(ChatBoxDao.class);

    public Service() throws IOException {}

    public boolean insertUser(User user) {
        return userDao.insertUser(user);
    }

    public User selectUserById(int id) {
        User user = userDao.selectUserById(id);
        List<Integer> CBIdList = new ArrayList<>(user.getIndexOfChatBoxList());
        List<ChatBox> chatBoxes = new ArrayList<>();
        for (int CBId : CBIdList) {
            ChatBox chatBox = chatBoxDao.selectChatBoxById(CBId);
            List<Message> messageList = new ArrayList<>();
            List<Integer> messageIdList = new ArrayList<>();
            for (Message message : messageDao.selectMessageByDest(String.valueOf(chatBox.getId()))) {
                messageList.add(message);
                messageIdList.add(message.getId());
            }
            chatBox.setHistoryIdList(messageIdList);
            chatBox.setHistory(messageList);
            List<User> userList = new ArrayList<>();
            for (int usrId : chatBox.getUserIdList()) {
                userList.add(userDao.selectUserById(usrId));
            }
            chatBox.setUsers(userList);
            chatBoxes.add(chatBox);
        }
        user.setIndexOfChatBoxList(CBIdList);
        user.setChatBoxList(chatBoxes);
        return user;
    }

    public User selectUserByName(String name) {
        User user = userDao.selectUserByName(name);
        if (user == null) {
            return null;
        }
        List<Integer> CBIdList = new ArrayList<>(user.getIndexOfChatBoxList());
        List<ChatBox> chatBoxes = new ArrayList<>();
        for (int CBId : CBIdList) {
            ChatBox chatBox = chatBoxDao.selectChatBoxById(CBId);
            List<Message> messageList = new ArrayList<>();
            List<Integer> messageIdList = new ArrayList<>();
            for (Message message : messageDao.selectMessageByDest(String.valueOf(chatBox.getId()))) {
                messageList.add(message);
                messageIdList.add(message.getId());
            }
            chatBox.setHistoryIdList(messageIdList);
            chatBox.setHistory(messageList);
            List<User> userList = new ArrayList<>();
            for (int usrId : chatBox.getUserIdList()) {
                userList.add(userDao.selectUserById(usrId));
            }
            chatBox.setUsers(userList);
            chatBoxes.add(chatBox);
        }
        user.setIndexOfChatBoxList(CBIdList);
        user.setChatBoxList(chatBoxes);
        return user;
    }

    public void updateIndexOfChatBoxList(List<Integer> indexOfChatBoxList, int id) {
        userDao.updateIndexOfChatBoxList(indexOfChatBoxList, id);
    }

    public List<Message> selectMessageByDest(String sendTo) {
        return new ArrayList<>(messageDao.selectMessageByDest(sendTo));
    }

    public Message selectMessageById(int id) {
        return messageDao.selectMessageById(id);
    }

    public void insertMessage(Message message) {
        messageDao.insertMessage(message);
    }

    public Integer selectMaxIdForMsg() {
        return messageDao.selectMaxId();
    }

    public Integer selectMaxIdForUsr() {
        return userDao.selectMaxId();
    }

    public Integer selectMaxIdForCBx() {
        return chatBoxDao.selectMaxId();
    }

    public ChatBox selectChatBoxById(int id) {
        ChatBox chatBox = chatBoxDao.selectChatBoxById(id);
        List<Message> messageList = new ArrayList<>(
                messageDao.selectMessageByDest(String.valueOf(chatBox.getId())));

        List<User> userList = new ArrayList<>();
        for (int usrId : chatBox.getUserIdList()) {
            userList.add(userDao.selectUserById(usrId));
        }
        chatBox.setHistory(messageList);
        chatBox.setUsers(userList);
        chatBox.setHistoryIdList(new ArrayList<>(chatBox.getHistoryIdList()));
        chatBox.setUserIdList(new ArrayList<>(chatBox.getUserIdList()));
        return chatBox;
    }

    public List<ChatBox> selectAllBox() {
        return new ArrayList<>(chatBoxDao.selectAllChatBox());
    }

    public void updateUserIdList(List<Integer> userIdList, int id) {
        chatBoxDao.updateUserIdList(id, userIdList);
    }


    public void updateHistoryIdList(List<Integer> historyIdList, int id) {
        chatBoxDao.updateHistoryIdList(id, historyIdList);
    }

    public void insertChatBox(ChatBox chatBox) {
        chatBoxDao.insertChatBox(chatBox);
    }

    public void deleteChatBox(int id) {
        chatBoxDao.deleteChatBox(id);
    }
}

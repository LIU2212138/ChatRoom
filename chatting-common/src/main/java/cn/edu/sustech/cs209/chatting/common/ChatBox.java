package cn.edu.sustech.cs209.chatting.common;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatBox implements Serializable {
    int id;
    String chatName;
    List<User> users = new ArrayList<>();
    List<Message> History = new ArrayList<>();

    public void addUser(User user){
        users.add(user);
    }

    public void addHistory(Message message) {
        History.add(message);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Message> getHistory() {
        return History;
    }

    public void setHistory(List<Message> history) {
        History = history;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}

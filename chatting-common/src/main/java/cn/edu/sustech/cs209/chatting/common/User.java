package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    int id;
    String name;
    String passWord;
    List<ChatBox> chatBoxList = new ArrayList<>();
    List<Integer> indexOfChatBoxList = new ArrayList<>();

    public User(String name, String passWord) {
        this.name = name;
        this.passWord = passWord;
    }

    public User() {}

    public void addChatBox(ChatBox chatBox) {
        chatBoxList.add(chatBox);
        indexOfChatBoxList.add(chatBox.getId());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ChatBox> getChatBoxList() {
        return chatBoxList;
    }

    public void setChatBoxList(List<ChatBox> chatBoxList) {
        this.chatBoxList = chatBoxList;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public List<Integer> getIndexOfChatBoxList() {
        return indexOfChatBoxList;
    }

    public void setIndexOfChatBoxList(List<Integer> indexOfChatBoxList) {
        this.indexOfChatBoxList = indexOfChatBoxList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

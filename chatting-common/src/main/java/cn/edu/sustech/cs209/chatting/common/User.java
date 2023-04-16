package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User  implements Serializable {
    String name;
    String passWord;
    List<ChatBox> chatBoxList = new ArrayList<>();
    public User(String name, String passWord) {
        this.name = name;
        this.passWord = passWord;
    }

    public User() {}

    public void addChatBox(ChatBox chatBox) {
        chatBoxList.add(chatBox);
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
}

package cn.edu.sustech.cs209.chatting.server.Dao;

import cn.edu.sustech.cs209.chatting.common.Message;
import java.util.List;

public interface MessageDao {

    List<Message> selectMessageByDest(String sendTo);

    Message selectMessageById(int id);

    void insertMessage(Message message);

    Integer selectMaxId();

}

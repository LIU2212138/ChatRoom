package cn.edu.sustech.cs209.chatting.server.Dao;

import cn.edu.sustech.cs209.chatting.common.ChatBox;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ChatBoxDao {
    ChatBox selectChatBoxById(int id);

    void updateUserIdList(@Param("id")int id, @Param("useridlist")List<Integer> useridlist);

    void updateHistoryIdList(@Param("id")int id,
                             @Param("historyidlist")List<Integer> historyidlist);

    List<ChatBox> selectAllChatBox();

    void insertChatBox(ChatBox chatBox);

    Integer selectMaxId();

    void deleteChatBox(int id);
}

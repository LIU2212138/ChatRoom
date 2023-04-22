package cn.edu.sustech.cs209.chatting.server.Dao;

import java.util.List;
import cn.edu.sustech.cs209.chatting.common.User;
import org.apache.ibatis.annotations.Param;

public interface UserDao {
    User selectUserById(int id);

    User selectUserByName(String name);

    boolean insertUser(User user);

    void updateIndexOfChatBoxList(@Param("indexofchatboxlist") List<Integer> indexOfChatBoxList, @Param("id") int id);

    Integer selectMaxId();
}

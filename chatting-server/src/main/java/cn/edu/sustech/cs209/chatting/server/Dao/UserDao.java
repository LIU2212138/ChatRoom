package cn.edu.sustech.cs209.chatting.server.Dao;

import cn.edu.sustech.cs209.chatting.common.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    User selectUserById(int id);
    User selectUserByName(String name);
    boolean insertUser(User user);
    void updateIndexOfChatBoxList(@Param("indexofchatboxlist") List<Integer> indexOfChatBoxList,@Param("id") int id);
    Integer selectMaxId();
}

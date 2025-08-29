package chat.platform.plus.infrastructure.dao;

import chat.platform.plus.infrastructure.dao.po.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {

    /**
     * 通过账号获取用户
     * @param userId
     * @return
     */
    User getUserByUserId(String userId);

    /**
     * 通过邮箱获取用户
     * @param userEmail
     * @return
     */
    User getUserByUserEmail(String userEmail);


    /**
     * 新增用户
     * @param user
     * @return
     */
    int insert(User user);

}

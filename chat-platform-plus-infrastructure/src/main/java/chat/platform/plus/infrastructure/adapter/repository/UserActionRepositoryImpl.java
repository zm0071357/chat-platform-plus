package chat.platform.plus.infrastructure.adapter.repository;

import chat.platform.plus.domain.user.adapter.repository.UserActionRepository;
import chat.platform.plus.domain.user.model.entity.UserInfoEntity;
import chat.platform.plus.domain.user.model.valobj.UserMessageConstant;
import chat.platform.plus.infrastructure.dao.UserDao;
import chat.platform.plus.infrastructure.dao.po.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserActionRepositoryImpl implements UserActionRepository {

    @Resource
    private UserDao userDao;

    @Override
    public UserInfoEntity getUserInfo(String userId) {
        User user = userDao.getUserByUserId(userId);
        if (user == null) {
            return UserInfoEntity.builder()
                    .isSuccess(false)
                    .message(UserMessageConstant.Fail_User_NoExist)
                    .build();
        }
        return UserInfoEntity.builder()
                .isSuccess(true)
                .message(UserMessageConstant.Success)
                .userId(userId)
                .userName(user.getUserName())
                .userEmail(user.getUserEmail())
                .build();
    }
}

package chat.platform.plus.domain.user.adapter.repository;

import chat.platform.plus.domain.user.model.entity.UserInfoEntity;

public interface UserActionRepository {

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    UserInfoEntity getUserInfo(String userId);
}

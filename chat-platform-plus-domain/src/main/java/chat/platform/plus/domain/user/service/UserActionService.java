package chat.platform.plus.domain.user.service;

import chat.platform.plus.domain.user.model.entity.UserInfoEntity;

public interface UserActionService {

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    UserInfoEntity getUserInfo(String userId);
}

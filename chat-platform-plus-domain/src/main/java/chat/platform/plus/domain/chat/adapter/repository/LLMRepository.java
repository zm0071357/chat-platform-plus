package chat.platform.plus.domain.chat.adapter.repository;

import chat.platform.plus.domain.chat.model.entity.UserEntity;

public interface LLMRepository {

    /**
     * 根据用户ID获取校验信息
     * @param userId
     * @return
     */
    UserEntity getUserByUserId(String userId);

    /**
     * 判断服务是否降级
     * @return
     */
    boolean downgradeSwitch();

    /**
     * 判断用户ID是否在切量范围内
     * @return
     */
    boolean cutRange(String userId);

}

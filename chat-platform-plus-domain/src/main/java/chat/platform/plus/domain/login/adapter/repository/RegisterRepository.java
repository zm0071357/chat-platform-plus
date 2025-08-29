package chat.platform.plus.domain.login.adapter.repository;

import chat.platform.plus.domain.login.model.entity.RegisterEntity;
import chat.platform.plus.domain.login.model.entity.RegisterResEntity;
import chat.platform.plus.domain.login.model.entity.VCEntity;

public interface RegisterRepository {

    /**
     * 注册
     * @param registerEntity
     * @return
     */
    RegisterResEntity register(RegisterEntity registerEntity);

    /**
     * 获取验证码
     * @param userEmail
     * @return
     */
    VCEntity getVC(String userEmail);
}

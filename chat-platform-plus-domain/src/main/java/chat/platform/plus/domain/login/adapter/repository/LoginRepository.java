package chat.platform.plus.domain.login.adapter.repository;

import chat.platform.plus.domain.login.model.entity.LoginByPWEntity;
import chat.platform.plus.domain.login.model.entity.LoginByVCEntity;
import chat.platform.plus.domain.login.model.entity.LoginResEntity;

public interface LoginRepository {

    /**
     * 账密登录
     * @param loginByPWEntity
     * @return
     */
    LoginResEntity loginByPW(LoginByPWEntity loginByPWEntity);

    /**
     * 邮箱验证码登录
     * @param loginByVCEntity
     * @return
     */
    LoginResEntity loginByVC(LoginByVCEntity loginByVCEntity);
}

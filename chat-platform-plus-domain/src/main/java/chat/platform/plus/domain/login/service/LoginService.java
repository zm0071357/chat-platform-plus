package chat.platform.plus.domain.login.service;

import chat.platform.plus.domain.login.model.entity.LoginByPWEntity;
import chat.platform.plus.domain.login.model.entity.LoginByVCEntity;
import chat.platform.plus.domain.login.model.entity.LoginResEntity;
import chat.platform.plus.domain.login.model.entity.VCEntity;

public interface LoginService {

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

    /**
     * 获取验证码
     * @param userEmail
     * @return
     */
    VCEntity getVC(String userEmail);

    /**
     * 退出登录
     * @return
     */
    LoginResEntity logout();

}

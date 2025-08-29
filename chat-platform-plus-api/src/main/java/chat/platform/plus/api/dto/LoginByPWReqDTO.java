package chat.platform.plus.api.dto;

import lombok.Getter;

/**
 * 账密登录请求体
 */
@Getter
public class LoginByPWReqDTO {

    /**
     * 账号
     */
    private String userId;

    /**
     * 密码
     */
    private String password;

}

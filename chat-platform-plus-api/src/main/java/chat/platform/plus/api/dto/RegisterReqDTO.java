package chat.platform.plus.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * 注册请求体
 */
@Getter
public class RegisterReqDTO {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 绑定邮箱
     */
    private String userEmail;

    /**
     * 验证码
     */
    @JsonProperty("VC")
    private String VC;
}

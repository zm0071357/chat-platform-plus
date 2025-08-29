package chat.platform.plus.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * 邮箱验证码登录请求体
 */
@Getter
public class LoginByVCReqDTO {

    /**
     * 邮箱
     */
    private String userEmail;

    /**
     * 验证码
     */
    @JsonProperty("VC")
    private String VC;
}

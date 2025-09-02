package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResDTO {

    /**
     * 用户账号
     */
    private String userId;

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * token
     */
    private String token;

    /**
     * 文本信息
     */
    private String message;

}

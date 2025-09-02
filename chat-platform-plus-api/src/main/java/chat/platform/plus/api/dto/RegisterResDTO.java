package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResDTO {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 账号
     */
    private String userId;

    /**
     * token
     */
    private String token;

    /**
     * 文本信息
     */
    private String message;
}

package chat.platform.plus.domain.login.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResEntity {

    /**
     * 用户账号
     */
    private String userId;

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 文本信息
     */
    private String message;
}

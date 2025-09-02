package chat.platform.plus.domain.login.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册结果实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResEntity {

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

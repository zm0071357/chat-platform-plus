package chat.platform.plus.domain.login.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账密登录实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginByPWEntity {

    /**
     * 账号
     */
    private String userId;

    /**
     * 密码
     */
    private String password;

}

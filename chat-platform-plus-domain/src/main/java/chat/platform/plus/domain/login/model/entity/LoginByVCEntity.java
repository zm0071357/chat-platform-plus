package chat.platform.plus.domain.login.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮箱登录实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginByVCEntity {

    /**
     * 绑定邮箱
     */
    private String userEmail;

    /**
     * 验证码
     */
    private String VC;

    /**
     * 获取缓存Key
     * @param userEmail
     * @return
     */
    public static String getVCKey(String userEmail) {
        return userEmail + "_VC";
    }

}

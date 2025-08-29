package chat.platform.plus.domain.login.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VCEntity {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 验证码
     */
    private String VC;

    /**
     * 文本信息
     */
    private String message;

    /**
     * 获取缓存Key
     * @param userEmail
     * @return
     */
    public static String getVCKey(String userEmail) {
        return userEmail + "_VC";
    }

}


package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户校验实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 是否黑名单
     * 0 否
     * 1 是
     */
    private Integer isBlack;

    /**
     * 是否VIP用户
     * 0 否
     * 1 是
     */
    private Integer isVIP;

    /**
     * 可调用次数
     */
    private Integer count;

}

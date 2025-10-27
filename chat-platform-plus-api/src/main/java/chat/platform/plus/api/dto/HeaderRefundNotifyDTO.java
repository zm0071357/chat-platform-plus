package chat.platform.plus.api.dto;

import lombok.Data;

/**
 * 团长退单补偿回调
 */
@Data
public class HeaderRefundNotifyDTO {

    /**
     * 回调类型
     */
    private Integer type;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 拼团组队ID
     */
    private String teamId;

    /**
     * 拼团组队状态
     */
    private Integer teamStatus;

}

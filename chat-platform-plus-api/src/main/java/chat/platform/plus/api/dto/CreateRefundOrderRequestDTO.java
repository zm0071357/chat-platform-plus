package chat.platform.plus.api.dto;

import lombok.Getter;

/**
 * 创建退单订单请求体
 */
@Getter
public class CreateRefundOrderRequestDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 退单描述
     */
    private String desc;

}

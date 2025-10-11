package chat.platform.plus.api.dto;

import lombok.Getter;

/**
 * 创建支付订单请求体
 */
@Getter
public class CreatePayOrderRequestDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 组队ID
     */
    private String teamId;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 订单类型
     * 1 直接购买
     * 2 拼团购买
     */
    private Integer orderType;

}

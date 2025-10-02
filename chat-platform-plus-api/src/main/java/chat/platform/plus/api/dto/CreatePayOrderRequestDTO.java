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
     * 商品ID
     */
    private String goodsId;

}

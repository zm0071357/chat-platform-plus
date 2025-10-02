package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 创建支付订单响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePayOrderResponseDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单创建时间
     */
    private Date orderCreateTime;

    /**
     * 支付价格
     */
    private BigDecimal orderPrice;

    /**
     * 支付地址
     */
    private String payUrl;

}

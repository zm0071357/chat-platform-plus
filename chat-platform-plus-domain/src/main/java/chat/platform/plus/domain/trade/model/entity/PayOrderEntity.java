package chat.platform.plus.domain.trade.model.entity;

import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderEntity {

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
     * 订单价格
     */
    private BigDecimal orderPrice;

    /**
     * 原始价格
     */
    private BigDecimal originalPrice;

    /**
     * 订单类型枚举
     */
    private OrderTypesEnum orderTypesEnum;

    /**
     * 订单优惠价格
     */
    private BigDecimal deductionPrice;

    /**
     * 订单最终支付价格
     */
    private BigDecimal payPrice;

    /**
     * 支付地址
     */
    private String payUrl;

    /**
     * 订单状态枚举
     */
    private OrderStatusEnum orderStatusEnum;

}

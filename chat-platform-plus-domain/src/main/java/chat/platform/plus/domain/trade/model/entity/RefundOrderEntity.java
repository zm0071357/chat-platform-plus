package chat.platform.plus.domain.trade.model.entity;

import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import chat.platform.plus.domain.trade.model.valobj.RefundOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退单订单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundOrderEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 支付订单ID
     */
    private String payOrderId;

    /**
     * 退单订单ID
     */
    private String refundOrderId;

    /**
     * 退单申请时间
     */
    private Date refundOrderCreateTime;

    /**
     * 退单金额
     */
    private BigDecimal refundOrderPrice;

    /**
     * 退单状态枚举
     */
    private RefundOrderStatusEnum refundOrderStatusEnum;

    /**
     * 支付订单类型枚举
     */
    private OrderTypesEnum orderTypesEnum;

    /**
     * 退单时间
     */
    private Date refundTime;

    /**
     * 退单原因
     */
    private String desc;

}

package chat.platform.plus.domain.trade.model.entity;

import chat.platform.plus.domain.trade.model.valobj.RefundOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 退单结果实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundResEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 支付订单ID
     */
    private String orderId;

    /**
     * 退单订单ID
     */
    private String refundOrderId;

    /**
     * 退单申请时间
     */
    private Date refundOrderCreateTime;

    /**
     * 退单状态枚举
     */
    private RefundOrderStatusEnum refundOrderStatusEnum;

    /**
     * 退单消息
     */
    private String info;

}

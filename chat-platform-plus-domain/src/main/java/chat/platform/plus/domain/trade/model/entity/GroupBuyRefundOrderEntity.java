package chat.platform.plus.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 拼团退单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyRefundOrderEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 组队ID
     */
    private String teamId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 外部交易单号
     */
    private String outTradeNo;

    /**
     * 退单状态码
     */
    private String refundCode;

    /**
     * 退单状态信息
     */
    private String refundInfo;
}

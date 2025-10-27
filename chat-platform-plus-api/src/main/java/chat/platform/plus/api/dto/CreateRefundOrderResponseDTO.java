package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 创建退单订单响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRefundOrderResponseDTO {

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
     * 退单状态
     */
    private Integer status;

    /**
     * 退单消息
     */
    private String info;

}

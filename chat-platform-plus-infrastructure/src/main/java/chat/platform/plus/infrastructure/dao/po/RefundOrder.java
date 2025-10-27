package chat.platform.plus.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 退单订单
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundOrder {

    /**
     * 自增ID
     */
    private Long id;

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
     * 退单状态 - 1退款中、2退款成功、3退款失败
     */
    private Integer status;

    /**
     * 支付订单类型 - 1 直接购买  2 拼团购买
     */
    private Integer payOrderType;

    /**
     * 退单完成时间
     */
    private Date refundTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}

package chat.platform.plus.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrder {

    /**
     * 自增ID
     */
    private Long id;

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
     * 订单类型 1 直接购买、2 拼团购买
     */
    private Integer orderType;

    /**
     * 订单优惠价格
     */
    private BigDecimal deductionPrice;

    /**
     * 订单最终支付价格
     */
    private BigDecimal payPrice;

    /**
     * 订单状态 1 创建完成、2 等待支付、3 支付成功、4 交易完成、5 订单关单
     */
    private Integer status;

    /**
     * 支付地址
     */
    private String payUrl;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}

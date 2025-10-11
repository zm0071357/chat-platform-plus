package chat.platform.plus.domain.trade.model.entity;

import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 预支付订单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrePayOrderEntity {

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
     * 订单优惠价格
     */
    private BigDecimal deductionPrice;

    /**
     * 订单最终支付价格
     */
    private BigDecimal payPrice;

    /**
     * 订单状态枚举
     */
    private OrderStatusEnum orderStatusEnum;

    /**
     * 订单类型枚举
     */
    private OrderTypesEnum orderTypesEnum;

    /**
     * 生成订单ID
     * @return
     */
    public static String createOrderId() {
        return "QDLL".concat(RandomStringUtils.randomNumeric(12));
    }
}

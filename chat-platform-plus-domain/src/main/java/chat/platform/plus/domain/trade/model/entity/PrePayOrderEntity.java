package chat.platform.plus.domain.trade.model.entity;

import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
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
     * 订单状态枚举
     */
    private OrderStatusEnum orderStatusEnum;

    /**
     * 生成订单ID
     * @return
     */
    public static String createOrderId() {
        return "QDLL".concat(RandomStringUtils.randomNumeric(12));
    }
}

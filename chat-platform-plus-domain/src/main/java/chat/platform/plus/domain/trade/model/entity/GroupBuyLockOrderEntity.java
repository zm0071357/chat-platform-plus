package chat.platform.plus.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 拼团锁单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyLockOrderEntity {

    /**
     * 原始价格
     */
    private BigDecimal originalPrice;

    /**
     * 折扣价格
     */
    private BigDecimal deductionPrice;

    /**
     * 支付价格
     */
    private BigDecimal payPrice;



}

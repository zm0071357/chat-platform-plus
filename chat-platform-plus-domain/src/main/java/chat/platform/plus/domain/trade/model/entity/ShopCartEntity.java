package chat.platform.plus.domain.trade.model.entity;

import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopCartEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 组队ID
     */
    private String teamId;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 订单类型枚举
     */
    private OrderTypesEnum orderTypesEnum;

}

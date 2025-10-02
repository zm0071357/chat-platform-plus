package chat.platform.plus.domain.trade.service.pay;

import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.ShopCartEntity;

import java.util.Date;

public interface TradeService {

    /**
     * 创建订单
     * @param shopCartEntity
     * @return
     * @throws Exception
     */
    PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception;

    /**
     * 根据订单ID获取待支付订单
     * @param orderId 订单ID
     * @return
     */
    PayOrderEntity getUnPaidOrder(String orderId) throws Exception;

    /**
     * 更新订单状态为已支付
     * @param orderId 订单ID
     * @param payTime 支付时间
     */
    Integer updateOrderStatusPaySuccess(String orderId, Date payTime) throws Exception;
}

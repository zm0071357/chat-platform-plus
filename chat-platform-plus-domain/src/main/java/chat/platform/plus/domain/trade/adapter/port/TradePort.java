package chat.platform.plus.domain.trade.adapter.port;

import chat.platform.plus.domain.trade.model.entity.LockOrderEntity;
import ltzf.payments.nativepay.model.order.OrderResponse;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public interface TradePort {

    /**
     * 蓝兔支付 - 扫码支付
     * @param orderId 订单ID
     * @param orderPrice 订单价格
     * @param goodsName 商品名称
     * @param lockOrderEntity 拼团锁单结果
     * @return
     */
    PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName, LockOrderEntity lockOrderEntity) throws IOException;

    /**
     * 蓝兔支付 - 查询订单
     * @param orderId 订单ID
     * @return
     */
    OrderResponse getOrderResponse(String orderId) throws IOException;

    /**
     * 拼团营销服务 - 锁单
     * @param userId 用户ID
     * @param teamId 组队ID
     * @param goodsId 商品ID
     * @param activityId 活动ID
     * @param orderId 订单ID
     * @return
     */
    LockOrderEntity lockOrder(String userId, String teamId, String goodsId, Long activityId, String orderId);

    /**
     * 拼团营销服务 - 结算
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param orderPayTime 订单支付时间
     */
    void settleOrder(String userId, String orderId, Date orderPayTime);
}

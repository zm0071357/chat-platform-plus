package chat.platform.plus.domain.trade.adapter.port;

import chat.platform.plus.domain.trade.model.entity.GroupBuyLockOrderEntity;
import chat.platform.plus.domain.trade.model.entity.GroupBuyRefundOrderEntity;
import ltzf.payments.nativepay.model.order.OrderResponse;
import ltzf.payments.nativepay.model.order.RefundOrderResponse;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;
import ltzf.payments.nativepay.model.refund.RefundResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public interface TradePort {

    /**
     * 蓝兔支付 - 扫码支付
     * @param orderId 订单ID
     * @param orderPrice 订单价格
     * @param goodsName 商品名称
     * @param groupBuyLockOrderEntity 拼团锁单结果
     * @return
     */
    PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName, GroupBuyLockOrderEntity groupBuyLockOrderEntity) throws IOException;

    /**
     * 蓝兔支付 - 查询订单
     * @param orderId 订单ID
     * @return
     */
    OrderResponse getOrderResponse(String orderId) throws IOException;

    /**
     * 蓝兔支付 - 退款
     * @param orderId 支付订单ID
     * @param refundOrderId 退单订单ID
     * @param refundOrderPrice 退单金额
     * @param refundDesc 退单描述
     * @return
     */
    RefundResponse doRefundOrder(String orderId, String refundOrderId, BigDecimal refundOrderPrice, String refundDesc) throws IOException;

    /**
     * 蓝兔支付 - 获取退款结果
     * @param refundOrderId 退单订单ID
     * @return
     */
    RefundOrderResponse getRefundOrderResponse(String refundOrderId) throws IOException;

    /**
     * 拼团营销服务 - 锁单
     * @param userId 用户ID
     * @param teamId 组队ID
     * @param goodsId 商品ID
     * @param activityId 活动ID
     * @param orderId 订单ID
     * @param inviteId 邀请码
     * @return
     */
    GroupBuyLockOrderEntity lockOrder(String userId, String teamId, String goodsId, Long activityId, String orderId, String inviteId);

    /**
     * 拼团营销服务 - 结算
     * @param userId 用户ID
     * @param orderId 订单ID
     * @param orderPayTime 订单支付时间
     */
    void settleOrder(String userId, String orderId, Date orderPayTime);

    /**
     * 拼团营销服务 - 退单
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return
     */
    void refundOrder(String userId, String orderId);

    /**
     * 拼团营销服务 - 获取拼团状态
     * @param teamId 拼团ID
     * @return
     */
    Integer getTeamProgress(String teamId);
}

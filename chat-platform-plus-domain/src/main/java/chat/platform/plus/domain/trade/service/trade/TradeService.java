package chat.platform.plus.domain.trade.service.trade;

import chat.platform.plus.domain.trade.model.entity.*;

import java.util.Date;
import java.util.List;

public interface TradeService {

    /**
     * 创建订单
     * @param shopCartEntity
     * @return
     * @throws Exception
     */
    PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception;

    /**
     * 订单支付成功
     * @param orderId 订单ID
     * @param orderPayTime 订单支付事件
     */
    void orderPaySuccess(String orderId, Date orderPayTime) throws Exception;

    /**
     * 拼团完成
     * @param teamId 组队ID
     * @param outTradeNoList 外部交易单号集合
     */
    void orderTeamComplete(String teamId, List<String> outTradeNoList) throws Exception;

    /**
     * 退单
     * @param preRefundOrderEntity
     * @return
     * @throws Exception
     */
    RefundResEntity createRefundOrder(PreRefundOrderEntity preRefundOrderEntity) throws Exception;

    /**
     * 订单退款成功
     * @param refundOrderId 退单订单ID
     * @param refundTime 退单时间
     */
    void orderRefundSuccess(String refundOrderId, Date refundTime) throws Exception;

    /**
     * 拼团团长退单补偿
     * @param userId 用户ID
     * @param teamId 拼团组队ID
     * @param teamStatus 拼团组队状态
     */
    void headerRefundCompensate(String userId, String teamId, Integer teamStatus) throws Exception;

}

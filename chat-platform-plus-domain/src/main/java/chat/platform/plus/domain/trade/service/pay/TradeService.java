package chat.platform.plus.domain.trade.service.pay;

import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.ShopCartEntity;

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
}

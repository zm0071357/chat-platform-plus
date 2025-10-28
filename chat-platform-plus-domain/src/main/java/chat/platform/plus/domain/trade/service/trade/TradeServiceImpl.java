package chat.platform.plus.domain.trade.service.trade;

import chat.platform.plus.domain.trade.model.entity.GroupBuyLockOrderEntity;
import chat.platform.plus.domain.trade.model.entity.PrePayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.RefundOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TradeServiceImpl extends AbstractTradeService {

    @Override
    protected void savePrePayOrder(PrePayOrderEntity prePayOrderEntity) {
        tradeRepository.savePrePayOrder(prePayOrderEntity);
    }

    @Override
    public void orderPaySuccess(String orderId, Date orderPayTime) throws Exception {
        // 结算 - 更新订单状态，根据订单类型进行回调/直接发货
        tradeRepository.settle(orderId, orderPayTime);
    }

    @Override
    protected GroupBuyLockOrderEntity lockOrder(String userId, String teamId, String goodsId, Long activityId, String orderId, String inviteId) {
        return tradePort.lockOrder(userId, teamId, goodsId, activityId, orderId, inviteId);
    }

    @Override
    protected void refundOrder(String userId, String orderId) {
        tradePort.refundOrder(userId, orderId);
    }

    @Override
    public void orderTeamComplete(String teamId, List<String> outTradeNoList, List<String> inviteUserIdList) throws Exception {
        // 更新订单状态为拼团完成
        log.info("更新订单状态为拼团完成，组队ID：{}，订单集合：{}", teamId, outTradeNoList);
        tradeRepository.updateOrderStatusTeamComplete(outTradeNoList);
        // 发货
        log.info("拼团完成，进行发货，组队ID：{}，订单集合：{}", teamId, outTradeNoList);
        tradeRepository.deliverGoods(outTradeNoList);
        // 邀请返利
        tradeRepository.inviteRebate(inviteUserIdList);
    }

    @Override
    protected void saveRefundOrder(RefundOrderEntity refundOrderEntity) {
        tradeRepository.saveRefundOrder(refundOrderEntity);
    }

    @Override
    public void orderRefundSuccess(String refundOrderId, Date refundTime) throws Exception {
        tradeRepository.orderRefundSuccess(refundOrderId, refundTime);
    }

    @Override
    public void headerRefundCompensate(String userId, String teamId, Integer teamStatus) throws Exception {
        tradeRepository.saveHeaderRefundCompensateTask(userId, teamId, teamStatus);
        if (teamStatus == 1) {
            tradeRepository.headerRefundCompensate(userId, teamId, teamStatus);
        }
    }

}

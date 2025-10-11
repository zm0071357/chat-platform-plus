package chat.platform.plus.domain.trade.service.pay;

import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.entity.LockOrderEntity;
import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.PrePayOrderEntity;
import chat.platform.plus.domain.trade.model.valobj.OrderStatusEnum;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import com.alibaba.fastjson2.util.DateUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TradeServiceImpl extends AbstractTradeService{

    @Resource
    private TradeRepository tradeRepository;

    @Resource
    private TradePort tradePort;

    @Override
    protected LockOrderEntity lockOrder(String userId, String teamId, String goodsId, Long activityId, String orderId) {
        return tradePort.lockOrder(userId, teamId, goodsId, activityId, orderId);
    }

    @Override
    protected void savePrePayOrder(PrePayOrderEntity prePayOrderEntity) {
        tradeRepository.savePrePayOrder(prePayOrderEntity);
    }

    @Override
    protected PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName) throws IOException {
        return tradePort.doPrePayOrder(orderId, orderPrice, goodsName, null);
    }

    @Override
    protected PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName, LockOrderEntity lockOrderEntity) throws IOException {
        return tradePort.doPrePayOrder(orderId, orderPrice, goodsName, lockOrderEntity);
    }

    @Override
    public void orderPaySuccess(String orderId, Date orderPayTime) throws Exception {
        // 结算 - 更新订单状态，根据订单类型进行回调/直接发货
        tradeRepository.settle(orderId, orderPayTime);
    }

    @Override
    public void orderTeamComplete(String teamId, List<String> outTradeNoList) throws Exception {
        // 更新订单状态为拼团完成
        log.info("更新订单状态为拼团完成，组队ID：{}，订单集合：{}", teamId, outTradeNoList);
        tradeRepository.updateOrderStatusTeamComplete(outTradeNoList);
        // 发货
        log.info("拼团完成，进行发货，组队ID：{}，订单集合：{}", teamId, outTradeNoList);
        tradeRepository.deliverGoods(outTradeNoList);
    }

}

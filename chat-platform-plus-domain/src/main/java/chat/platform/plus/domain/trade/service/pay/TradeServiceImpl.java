package chat.platform.plus.domain.trade.service.pay;

import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.entity.PayOrderEntity;
import chat.platform.plus.domain.trade.model.entity.PrePayOrderEntity;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class TradeServiceImpl extends AbstractTradeService{

    @Resource
    private TradeRepository tradeRepository;

    @Resource
    private TradePort tradePort;

    @Override
    protected void savePrePayOrder(PrePayOrderEntity prePayOrderEntity) {
        tradeRepository.savePrePayOrder(prePayOrderEntity);
    }

    @Override
    protected PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName) throws IOException {
        return tradePort.doPrePayOrder(orderId, orderPrice, goodsName);
    }

    @Override
    public PayOrderEntity getUnPaidOrder(String orderId) throws Exception {
        return tradeRepository.getUnPaidOrder(orderId);
    }

    @Override
    public Integer updateOrderStatusPaySuccess(String orderId, Date payTime) throws Exception {
        return tradeRepository.updateOrderStatusPaySuccess(orderId, payTime);
    }
}

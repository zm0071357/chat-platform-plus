package chat.platform.plus.infrastructure.adapter.port;

import chat.platform.plus.domain.trade.adapter.port.TradePort;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import ltzf.payments.nativepay.impl.NativePayServiceImpl;
import ltzf.payments.nativepay.model.order.OrderRequest;
import ltzf.payments.nativepay.model.order.OrderResponse;
import ltzf.payments.nativepay.model.prepay.PrepayRequest;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
public class TradePortImpl implements TradePort {

    @Resource
    private NativePayServiceImpl nativePayService;

    @Value("${ltzf.sdk.config.mch_id}")
    private String mchId;

    @Value("${ltzf.sdk.config.notify_url}")
    private String notifyUrl;

    @Override
    public PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName) throws IOException{
        // 封装请求
        PrepayRequest prepayRequest = new PrepayRequest();
        prepayRequest.setMchId(mchId);
        prepayRequest.setOutTradeNo(orderId);
        prepayRequest.setTotalFee(orderPrice.toString());
        prepayRequest.setBody(goodsName);
        prepayRequest.setNotifyUrl(notifyUrl);
        // 发起请求
        return nativePayService.prePay(prepayRequest);
    }

    @Override
    public OrderResponse getOrderResponse(String orderId) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setMchId(mchId);
        orderRequest.setOutTradeNo(orderId);
        OrderResponse orderResponse = nativePayService.getOrder(orderRequest);
        log.info("订单ID：{}，订单状态：{}", orderId, JSON.toJSONString(orderResponse));
        return orderResponse;
    }


}


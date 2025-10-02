package chat.platform.plus.domain.trade.adapter.port;

import ltzf.payments.nativepay.model.order.OrderResponse;
import ltzf.payments.nativepay.model.prepay.PrepayResponse;

import java.io.IOException;
import java.math.BigDecimal;

public interface TradePort {

    /**
     * 蓝兔支付 - 扫码支付
     * @param orderId 订单ID
     * @param orderPrice 订单价格
     * @param goodsName 商品名称
     * @return
     */
    PrepayResponse doPrePayOrder(String orderId, BigDecimal orderPrice, String goodsName) throws IOException;

    /**
     * 蓝兔支付 - 查询订单
     * @param orderId 订单ID
     * @return
     */
    OrderResponse getOrderResponse(String orderId) throws IOException;
}

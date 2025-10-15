package chat.platform.plus.trigger.listener;

import chat.platform.plus.domain.trade.adapter.event.OrderPaySuccessMessageEvent;
import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.adapter.repository.DeliverRepository;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import chat.platform.plus.domain.trade.service.deliver.DeliverService;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费者 - 接收订单支付完成队列的消息进行消费
 */
@Slf4j
@Component
public class OrderPaySuccessListener {

    @Resource
    private TradeRepository tradeRepository;

    @Resource
    private TradePort tradePort;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_order_pay_success.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_order_pay_success.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.consumer.topic_order_pay_success.routing_key}"
            )
    )
    public void listener(String message) throws Exception {
        try {
            log.info("浅度浏览AI服务 - 监听订单支付完成队列 - 接收到消息：{}，进行处理", message);
            // 将message序列化
            OrderPaySuccessMessageEvent.OrderPaySuccessMessage orderPaySuccessMessage = JSON.parseObject(message, OrderPaySuccessMessageEvent.OrderPaySuccessMessage.class);
            if (orderPaySuccessMessage.getOrderTypesEnum().equals(OrderTypesEnum.GROUPBUY)) {
                log.info("订单类型为拼团购买，进行结算，订单ID：{}", orderPaySuccessMessage.getOrderId());
                // 结算完成后 - 由拼团营销服务回调进行发货处理
                tradePort.settleOrder(orderPaySuccessMessage.getUserId(), orderPaySuccessMessage.getOrderId(), orderPaySuccessMessage.getOrderPayTime());
            } else {
                // 发货
                log.info("订单类型为直接购买，进行发货，订单ID：{}", orderPaySuccessMessage.getOrderId());
                tradeRepository.deliverGoods(orderPaySuccessMessage.getOrderId());
            }
        } catch (Exception e) {
            log.info("浅度浏览AI服务 - 监听订单支付完成队列 - 消息处理失败：{}", message, e);
            throw e;
        }
    }

}

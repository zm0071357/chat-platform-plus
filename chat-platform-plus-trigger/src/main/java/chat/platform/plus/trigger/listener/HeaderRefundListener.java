package chat.platform.plus.trigger.listener;

import chat.platform.plus.api.dto.HeaderRefundNotifyDTO;
import chat.platform.plus.domain.trade.adapter.event.OrderPaySuccessMessageEvent;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
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
 * 消费者 - 接收团长退单补偿队列的消息进行消费
 */
@Slf4j
@Component
public class HeaderRefundListener {

    @Resource
    private TradeRepository tradeRepository;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_header_refund.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_header_refund.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.consumer.topic_header_refund.routing_key}"
            )
    )
    public void listener(String message) throws Exception {
        try {
            log.info("浅度浏览AI服务 - 监听团长退单补偿队列 - 接收到消息：{}，进行处理", message);
            // 反序列化
            HeaderRefundNotifyDTO headerRefundNotifyDTO = JSON.parseObject(message, HeaderRefundNotifyDTO.class);
            // 创建团长退单补偿任务
            tradeRepository.saveHeaderRefundCompensateTask(headerRefundNotifyDTO.getUserId(), headerRefundNotifyDTO.getTeamId(), headerRefundNotifyDTO.getTeamStatus());
        } catch (Exception e) {
            log.info("浅度浏览AI服务 - 监听团长退单补偿队列 - 消息处理失败：{}", message, e);
            throw e;
        }
    }
}

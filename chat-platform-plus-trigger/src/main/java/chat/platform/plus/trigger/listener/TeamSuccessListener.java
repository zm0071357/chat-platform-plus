package chat.platform.plus.trigger.listener;

import chat.platform.plus.api.dto.GroupBuyNotifyDTO;
import chat.platform.plus.domain.trade.service.pay.TradeService;
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
 * 消费者 - 接收拼团完成队列的消息进行消费
 */
@Slf4j
@Component
public class TeamSuccessListener {

    @Resource
    private TradeService tradeService;

    /**
     * 消费消息
     * 消费者通过路由键绑定了交换机和队列的关系，那么只需要传递交换机和路由键，交换机就能根据路由键将消息投递到对应的队列里面，等待消费者进行消费
     *
     * 注解 @RabbitListener 的 @QueueBinding 实际上是在声明和创建绑定关系，项目启动时会执行如下步骤：
     * 1.创建队列（如果不存在）
     * 2.创建交换机（如果不存在）
     * 3.通过路由Key创建队列与交换机之间的绑定关系
     * 4.开始监听这个队列
     *
     * @param message 消息
     * @throws Exception
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_team_success.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_team_success.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.consumer.topic_team_success.routing_key}"
            )
    )
    public void listener(String message) throws Exception {
        try {
            log.info("浅度浏览AI服务 - 监听拼团完成消息队列 - 接收到消息：{}，进行结算", message);
            // 反序列化
            GroupBuyNotifyDTO groupBuyNotifyDTO = JSON.parseObject(message, GroupBuyNotifyDTO.class);
            // 结算
            tradeService.orderTeamComplete(groupBuyNotifyDTO.getTeamId(), groupBuyNotifyDTO.getOutTradeNoList());
        } catch (Exception e) {
            log.info("浅度浏览AI服务 - 监听拼团完成消息队列 - 消息结算失败：{}", message, e);
            throw e;
        }
    }

}

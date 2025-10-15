package chat.platform.plus.domain.trade.adapter.event;

import chat.platform.plus.domain.trade.model.valobj.OrderTypesEnum;
import chat.platform.plus.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 订单支付成功事件
 */
@Component
public class OrderPaySuccessMessageEvent extends BaseEvent<OrderPaySuccessMessageEvent.OrderPaySuccessMessage> {

    @Value("${spring.rabbitmq.config.producer.topic_order_pay_success.topic}")
    private String topic;

    @Override
    public EventMessage<OrderPaySuccessMessage> buildEventMessage(OrderPaySuccessMessage data) {
        return EventMessage.<OrderPaySuccessMessage>builder()
                .id("ORDER_PAY_SUCCESS_" + RandomStringUtils.randomNumeric(12))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

    /**
     * 订单支付成功消息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderPaySuccessMessage {

        /**
         * 用户ID
         */
        private String userId;

        /**
         * 订单ID
         */
        private String orderId;

        /**
         * 订单支付时间
         */
        private Date orderPayTime;

        /**
         * 订单类型枚举
         */
        private OrderTypesEnum orderTypesEnum;
    }

}

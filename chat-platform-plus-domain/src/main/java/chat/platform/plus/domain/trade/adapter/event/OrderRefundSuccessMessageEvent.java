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
 * 订单退款成功事件
 */
@Component
public class OrderRefundSuccessMessageEvent extends BaseEvent<OrderRefundSuccessMessageEvent.OrderRefundSuccessMessage>{

    @Value("${spring.rabbitmq.config.producer.topic_order_refund_success.topic}")
    private String topic;


    @Override
    public EventMessage<OrderRefundSuccessMessage> buildEventMessage(OrderRefundSuccessMessage data) {
        return EventMessage.<OrderRefundSuccessMessageEvent.OrderRefundSuccessMessage>builder()
                .id("ORDER_REFUND_SUCCESS_" + RandomStringUtils.randomNumeric(12))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

    /**
     * 订单退款成功消息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderRefundSuccessMessage {

        /**
         * 用户ID
         */
        private String userId;

        /**
         * 支付订单ID
         */
        private String payOrderId;

        /**
         * 退单订单ID
         */
        private String refundOrderId;

        /**
         * 订单退款完成时间
         */
        private Date refundTime;
    }

}

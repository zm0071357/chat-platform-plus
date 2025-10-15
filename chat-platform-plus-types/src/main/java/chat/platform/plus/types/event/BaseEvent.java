package chat.platform.plus.types.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 抽象事件类 - 定义事件消息的通用结构和行为
 * @param <T>
 */
@Data
public abstract class BaseEvent<T> {

    /**
     * 构建消息
     * @param data
     * @return
     */
    public abstract EventMessage<T> buildEventMessage(T data);

    /**
     * 消息主题
     * @return
     */
    public abstract String topic();

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventMessage<T> {

        /**
         * 消息唯一标识
         */
        private String id;

        /**
         * 时间戳
         */
        private Date timestamp;

        /**
         * 数据
         */
        private T data;
    }

}

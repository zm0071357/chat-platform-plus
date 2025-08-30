package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 调用大模型实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 文本
     */
    private String content;

    /**
     * 消息类型
     */
    private Integer messageType;

}

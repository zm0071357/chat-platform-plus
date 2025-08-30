package chat.platform.plus.api.dto;

import lombok.Getter;

/**
 * 对话请求体
 */
@Getter
public class ChatReqDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息类型
     */
    private Integer messageType;

}

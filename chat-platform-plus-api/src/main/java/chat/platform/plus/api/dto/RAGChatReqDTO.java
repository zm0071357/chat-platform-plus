package chat.platform.plus.api.dto;

import lombok.Getter;

/**
 * RAG聊天请求体
 */
@Getter
public class RAGChatReqDTO {

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 知识库标签
     */
    private String ragTag;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息类型 5
     */
    private Integer messageType;

}

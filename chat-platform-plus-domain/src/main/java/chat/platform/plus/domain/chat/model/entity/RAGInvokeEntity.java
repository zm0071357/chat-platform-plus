package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

/**
 * RAG调用大模型实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RAGInvokeEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 历史记录
     */
    private List<ChatRequest.Input.Message> historyMessage;

    /**
     * 知识库标签
     */
    private String ragTag;

    /**
     * 文本
     */
    private String content;

}

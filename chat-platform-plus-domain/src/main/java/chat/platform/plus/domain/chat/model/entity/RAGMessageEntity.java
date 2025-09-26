package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RAG消息实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RAGMessageEntity {

    /**
     * 用户ID
     */
    private String userId;

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
}

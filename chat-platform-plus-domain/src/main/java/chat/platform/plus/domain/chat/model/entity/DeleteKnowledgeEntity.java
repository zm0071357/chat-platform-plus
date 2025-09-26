package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除知识库结果实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteKnowledgeEntity {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 返回信息
     */
    private String message;
}

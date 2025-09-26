package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除历史记录结果实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteHistoryResEntity {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 返回信息
     */
    private String message;

}

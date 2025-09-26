package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解析Git仓库实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitResEntity {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 信息
     */
    private String message;

}

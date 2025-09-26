package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 解析Git仓库响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitResDTO {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 信息
     */
    private String message;

}

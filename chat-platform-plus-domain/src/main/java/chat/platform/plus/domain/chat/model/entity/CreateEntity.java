package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创作实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEntity {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 创作类型
     * 1 图片
     * 2 视频
     */
    private Integer type;

    /**
     * URL
     */
    private String url;

    /**
     * 文本信息
     */
    private String message;


}

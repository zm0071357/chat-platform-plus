package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成图片/视频响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateResDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 图片/视频URL
     */
    private String url;

    /**
     * 文本信息
     */
    private String message;

}

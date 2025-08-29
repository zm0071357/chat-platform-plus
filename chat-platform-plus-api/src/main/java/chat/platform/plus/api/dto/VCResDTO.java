package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送验证码响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VCResDTO {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 文本信息
     */
    private String message;

}

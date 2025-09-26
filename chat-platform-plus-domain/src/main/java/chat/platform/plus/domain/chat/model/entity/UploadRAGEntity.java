package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadRAGEntity {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 信息
     */
    private String message;

    /**
     * 上传成功个数
     */
    private Integer count;

}

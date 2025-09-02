package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件校验实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadFileEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 消息类型
     */
    private Integer messageType;

}

package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件结果实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpLoadFileResEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 信息
     */
    private String message;

    /**
     * 文件
     */
    private String url;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件类型
     */
    private Integer fileType;
}

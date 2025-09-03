package chat.platform.plus.api.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件请求体
 */
@Data
public class UpLoadFileReqDTO {

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 消息类型
     */
    private Integer messageType;

}

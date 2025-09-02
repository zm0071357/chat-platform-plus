package chat.platform.plus.api.dto;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 上传RAG知识库请求体
 */
@Getter
public class UpLoadRAGReqDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文件
     */
    private List<MultipartFile> file;

    /**
     * 知识库名称
     */
    private String ragName;

    /**
     * 消息类型
     */
    private Integer messageType;

}

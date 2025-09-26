package chat.platform.plus.api.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 上传RAG知识库请求体
 */
@Data
public class UpLoadRAGReqDTO {

    /**
     * 文件
     */
    private List<MultipartFile> fileList;

    /**
     * 知识库名称
     */
    private String ragName;

    /**
     * 消息类型
     */
    private Integer messageType;

}

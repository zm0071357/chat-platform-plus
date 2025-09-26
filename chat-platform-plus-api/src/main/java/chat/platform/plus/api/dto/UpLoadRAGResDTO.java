package chat.platform.plus.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 上传RAG知识库响应体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpLoadRAGResDTO {

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 信息
     */
    private String message;

    /**
     * 知识库名称
     */
    private String ragName;

    /**
     * 上传成功个数
     */
    private Integer count;
}

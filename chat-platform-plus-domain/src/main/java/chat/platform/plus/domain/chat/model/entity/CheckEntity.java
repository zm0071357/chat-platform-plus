package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import chat.platform.plus.types.common.File;

import java.util.List;

/**
 * 校验实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文本
     */
    private String content;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 是否开启联网搜索 如果有文件的话就不能开启
     */
    private Boolean isSearch;

    /**
     * 文件集合
     */
    private List<File> fileList;

    /**
     * 文件集合总字节
     */
    private Long fileListSize;

    /**
     * 文件
     */
    private MultipartFile file;

}

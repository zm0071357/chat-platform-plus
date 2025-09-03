package chat.platform.plus.domain.chat.model.entity;

import chat.platform.plus.types.common.File;
import lombok.*;

import java.util.List;

/**
 * 创作图片请求实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateImgEntity {

    /**
     * 账号
     */
    private String userId;

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 尺寸
     */
    private String size;

    /**
     * 命令
     */
    private String imgFunction;

    /**
     * 文件列表
     */
    private List<File> fileList;

    /**
     * 文件总大小
     */
    private Long fileListSize;

}

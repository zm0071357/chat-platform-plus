package chat.platform.plus.domain.chat.model.entity;

import chat.platform.plus.types.common.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创作视频请求实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateVidEntity {

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
     * 视频创作类型
     */
    private String vidFunction;

    /**
     * 文件列表
     */
    private List<File> fileList;

    /**
     * 文件总大小
     */
    private Long fileListSize;
}

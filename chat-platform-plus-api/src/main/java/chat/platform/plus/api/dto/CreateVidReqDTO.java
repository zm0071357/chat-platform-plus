package chat.platform.plus.api.dto;

import chat.platform.plus.types.common.File;
import lombok.Getter;

import java.util.List;

/**
 * 视频创作请求体
 */
@Getter
public class CreateVidReqDTO {

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息类型 4
     */
    private Integer messageType;

    /**
     * 视频创作类型
     */
    private Integer vidFunction;

    /**
     * 文件列表
     */
    private List<File> fileList;

    /**
     * 文件总大小
     */
    private Long fileListSize;
}

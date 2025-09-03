package chat.platform.plus.api.dto;

import chat.platform.plus.types.common.File;
import lombok.Getter;

import java.util.List;

/**
 * 图片创作请求体
 */
@Getter
public class CreateImgReqDTO {

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息类型 3
     */
    private Integer messageType;

    /**
     * 尺寸
     */
    private Integer sizeType;

    /**
     * 命令
     */
    private Integer functionType;

    /**
     * 文件列表
     */
    private List<File> fileList;

    /**
     * 文件总大小
     */
    private Long fileListSize;
}

package chat.platform.plus.api.dto;

import chat.platform.plus.types.common.File;
import lombok.Getter;

import java.util.List;

/**
 * 对话请求体
 */
@Getter
public class ChatReqDTO {

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息类型 默认为1
     */
    private Integer messageType;

    /**
     * 是否开启联网搜索 如果有文件的话就不能开启
     */
    private Boolean isSearch;

    /**
     * 上传 可为空
     */
    private UploadFile uploadFile;

    /**
     * 上传文件
     */
    @Getter
    public static class UploadFile {

        /**
         * 文件列表
         */
        private List<File> fileList;

        /**
         * 文件总大小
         */
        private Long fileListSize;

    }

}

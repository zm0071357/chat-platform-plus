package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import qwen.sdk.largemodel.chat.model.ChatRequest;
import chat.platform.plus.types.common.File;

import java.util.List;

/**
 * 调用大模型实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvokeEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 历史记录编码
     */
    private String historyCode;

    /**
     * 历史记录
     */
    private List<ChatRequest.Input.Message> historyMessages;


    /**
     * 历史请求记录
     */
    private List<ChatRequest.Input.Message> requestMessages;

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

}


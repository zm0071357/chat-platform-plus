package chat.platform.plus.domain.chat.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

/**
 * 历史记录实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistoryEntity {

    /**
     * 历史记录
     */
    private List<ChatRequest.Input.Message> historyMessages;


    /**
     * 历史请求记录
     */
    private List<ChatRequest.Input.Message> requestMessages;

}

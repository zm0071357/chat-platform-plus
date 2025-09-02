package chat.platform.plus.domain.chat.service;

import chat.platform.plus.domain.chat.model.entity.HistoryCodeEntity;
import chat.platform.plus.domain.chat.model.entity.MessageEntity;
import chat.platform.plus.domain.chat.model.entity.UpLoadFileResEntity;
import chat.platform.plus.domain.chat.model.entity.UploadFileEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

public interface LLMService {

    /**
     * 流式对话
     * @param messageEntity
     * @return
     * @throws Exception
     */
    ResponseBodyEmitter chat(MessageEntity messageEntity, ResponseBodyEmitter responseBodyEmitter) throws Exception;

    /**
     * 上传文件
     * @param uploadFileEntity
     * @return
     */
    UpLoadFileResEntity uploadFile(UploadFileEntity uploadFileEntity) throws Exception;

    /**
     * 获取历史记录编码集合
     * @param userId
     * @return
     */
    List<HistoryCodeEntity> getHistoryCodeList(String userId);

    /**
     * 获取历史记录
     * @param userId
     * @param historyCode
     * @return
     */
    List<ChatRequest.Input.Message> getHistory(String userId, String historyCode);
}

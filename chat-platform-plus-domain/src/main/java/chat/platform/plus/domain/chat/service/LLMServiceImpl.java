package chat.platform.plus.domain.chat.service;

import chat.platform.plus.domain.chat.adapter.port.LLMPort;
import chat.platform.plus.domain.chat.adapter.repository.LLMRepository;
import chat.platform.plus.domain.chat.model.entity.*;
import chat.platform.plus.domain.chat.model.valobj.FilterEnum;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLinkFactory;
import chat.platform.plus.types.design.framework.link.multition.chain.BusinessLinkedList;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;

@Slf4j
@Service
public class LLMServiceImpl implements LLMService {

    @Resource
    private BusinessLinkedList<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> llmLogicLink;

    @Resource
    private LLMRepository llmRepository;

    @Resource
    private LLMPort llmPort;

    @Override
    public ResponseBodyEmitter chat(MessageEntity messageEntity, ResponseBodyEmitter responseBodyEmitter) throws Exception {
        CheckEntity checkEntity = CheckEntity.builder()
                .userId(messageEntity.getUserId())
                .content(messageEntity.getContent())
                .messageType(messageEntity.getMessageType())
                .isSearch(messageEntity.getIsSearch())
                .fileList(messageEntity.getFileList())
                .fileListSize(messageEntity.getFileListSize())
                .build();
        // 责任链过滤
        HandleEntity handleEntity = llmLogicLink.apply(checkEntity, new DefaultLinkFactory.DynamicContext());
        if (handleEntity.getIsSuccess()) {
            HistoryEntity historyEntity = llmRepository.getHistory(messageEntity.getUserId(), messageEntity.getHistoryCode());
            InvokeEntity invokeEntity = InvokeEntity.builder()
                    .userId(messageEntity.getUserId())
                    .historyMessages(historyEntity.getHistoryMessages())
                    .requestMessages(historyEntity.getRequestMessages())
                    .content(messageEntity.getContent())
                    .messageType(messageEntity.getMessageType())
                    .isSearch(messageEntity.getIsSearch())
                    .fileList(messageEntity.getFileList())
                    .build();
            return llmPort.chat(invokeEntity, responseBodyEmitter);
        } else {
            return llmPort.fail(handleEntity.getResult(), responseBodyEmitter);
        }
    }

    @Override
    public UpLoadFileResEntity uploadFile(UploadFileEntity uploadFileEntity) throws Exception {
        CheckEntity checkEntity = CheckEntity.builder()
                .userId(uploadFileEntity.getUserId())
                .messageType(uploadFileEntity.getMessageType())
                .file(uploadFileEntity.getFile())
                .build();
        DefaultLinkFactory.DynamicContext dynamicContext = DefaultLinkFactory.DynamicContext.builder()
                .endFilter(FilterEnum.FILE.getPlace())
                .build();
        // 责任链过滤
        HandleEntity handleEntity = llmLogicLink.apply(checkEntity, dynamicContext);
        if (handleEntity.getIsSuccess()) {
            return llmRepository.uploadFile(uploadFileEntity);
        }
        return UpLoadFileResEntity.builder()
                .isSuccess(handleEntity.getIsSuccess())
                .message(handleEntity.getMessage())
                .build();
    }

    @Override
    public List<HistoryCodeEntity> getHistoryCodeList(String userId) {
        return llmRepository.getHistoryCodeList(userId);
    }

    @Override
    public List<ChatRequest.Input.Message> getHistory(String userId, String historyCode) {
        HistoryEntity historyEntity = llmRepository.getHistory(userId, historyCode);
        return historyEntity.getHistoryMessages();
    }

}

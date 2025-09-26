package chat.platform.plus.domain.chat.service;

import chat.platform.plus.domain.chat.adapter.port.RAGChatPort;
import chat.platform.plus.domain.chat.adapter.repository.LLMRepository;
import chat.platform.plus.domain.chat.adapter.repository.RAGChatRepository;
import chat.platform.plus.domain.chat.model.entity.*;
import chat.platform.plus.domain.chat.model.valobj.FilterEnum;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLinkFactory;
import chat.platform.plus.types.design.framework.link.multition.chain.BusinessLinkedList;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.List;


@Slf4j
@Service
public class RAGChatServiceImpl implements RAGChatService{

    @Resource
    private RAGChatRepository ragChatRepository;

    @Resource
    private LLMRepository llmRepository;

    @Resource
    private RAGChatPort ragChatPort;

    @Resource
    public BusinessLinkedList<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> llmLogicLink;

    @Override
    public UploadRAGEntity uploadRAG(String userId, List<MultipartFile> fileList, String ragName) throws Exception {
        HandleEntity handleEntity = this.filter(userId);
        if (handleEntity.getIsSuccess()) {
            return ragChatRepository.uploadRAG(userId, fileList, ragName);
        }
        return UploadRAGEntity.builder()
                .isSuccess(handleEntity.getIsSuccess())
                .message(handleEntity.getMessage())
                .count(0)
                .build();
    }

    @Override
    public GitResEntity analyzeGitRepository(String userId, String ragName, String repoUrl) throws Exception {
        HandleEntity handleEntity = this.filter(userId);
        if (handleEntity.getIsSuccess()) {
            return ragChatRepository.analyzeGitRepository(userId, ragName, repoUrl);
        }
        return GitResEntity.builder()
                .isSuccess(handleEntity.getIsSuccess())
                .message(handleEntity.getMessage())
                .build();
    }

    @Override
    public ResponseBodyEmitter chat(RAGMessageEntity ragMessageEntity, ResponseBodyEmitter responseBodyEmitter) throws Exception {
        HandleEntity handleEntity = this.filter(ragMessageEntity.getUserId());
        if (handleEntity.getIsSuccess()) {
            HistoryEntity historyEntity = llmRepository.getHistory(ragMessageEntity.getUserId(), ragMessageEntity.getHistoryCode());
            RAGInvokeEntity ragInvokeEntity = RAGInvokeEntity.builder()
                    .userId(ragMessageEntity.getUserId())
                    .historyCode(historyEntity.getHistoryCode())
                    .historyMessage(historyEntity.getHistoryMessages())
                    .ragTag(ragMessageEntity.getContent())
                    .content(ragMessageEntity.getContent())
                    .build();
            return ragChatPort.chat(ragInvokeEntity, responseBodyEmitter);
        } else {
            return ragChatPort.fail(handleEntity.getResult(), responseBodyEmitter);
        }
    }

    @Override
    public ResponseBodyEmitter fail(String result, ResponseBodyEmitter responseBodyEmitter) {
        return ragChatPort.fail(result, responseBodyEmitter);
    }

    @Override
    public DeleteKnowledgeEntity deleteKnowledge(String userId, String ragName) {
        return ragChatRepository.deleteKnowledge(userId, ragName);
    }

    /**
     * 责任链过滤
     * @param userId
     * @return
     * @throws Exception
     */
    private HandleEntity filter(String userId) throws Exception {
        CheckEntity checkEntity = CheckEntity.builder()
                .userId(userId)
                .build();
        DefaultLinkFactory.DynamicContext dynamicContext = DefaultLinkFactory.DynamicContext.builder()
                .endFilter(FilterEnum.USER.getPlace())
                .build();
        // 责任链过滤
        return llmLogicLink.apply(checkEntity, dynamicContext);
    }
}

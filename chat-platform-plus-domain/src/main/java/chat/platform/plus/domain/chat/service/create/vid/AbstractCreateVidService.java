package chat.platform.plus.domain.chat.service.create.vid;


import chat.platform.plus.domain.chat.adapter.repository.LLMRepository;
import chat.platform.plus.domain.chat.model.entity.*;
import chat.platform.plus.domain.chat.model.valobj.RoleEnum;
import chat.platform.plus.domain.chat.model.valobj.UserContentConstant;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLinkFactory;
import chat.platform.plus.types.design.framework.link.multition.chain.BusinessLinkedList;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频创作抽象类
 */
@Slf4j
@Service
public abstract class AbstractCreateVidService implements CreateVidService{

    @Resource
    private LLMRepository llmRepository;

    @Resource
    private BusinessLinkedList<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> createVidLogicLink;

    @Override
    public CreateEntity createVid(CreateVidEntity createVidEntity) throws Exception {
        CheckEntity checkEntity = CheckEntity.builder()
                .userId(createVidEntity.getUserId())
                .content(createVidEntity.getContent())
                .messageType(createVidEntity.getMessageType())
                .fileList(createVidEntity.getFileList())
                .fileListSize(createVidEntity.getFileListSize())
                .build();
        DefaultLinkFactory.DynamicContext dynamicContext = DefaultLinkFactory.DynamicContext.builder()
                .vidFunction(createVidEntity.getVidFunction())
                .build();
        // 责任链过滤
        HandleEntity handleEntity = createVidLogicLink.apply(checkEntity, dynamicContext);
        if (handleEntity.getIsSuccess()) {
            // 尝试生成视频
            CreateEntity createEntity = create(createVidEntity);
            if (createEntity.getIsSuccess()) {
                // 获取历史记录
                HistoryEntity historyEntity = llmRepository.getHistory(createVidEntity.getUserId(), createVidEntity.getHistoryCode());
                List<ChatRequest.Input.Message> historyMessages = historyEntity.getHistoryMessages();
                List<ChatRequest.Input.Message> requestMessages = historyEntity.getRequestMessages();
                // 写入历史记录
                List<ChatRequest.Input.Message.Content> userContent = new ArrayList<>();
                // 用户消息
                userContent.add(ChatRequest.Input.Message.Content.builder()
                        .text(UserContentConstant.Create_Vid + createVidEntity.getContent())
                        .build());
                historyMessages.add(ChatRequest.Input.Message.builder()
                        .role(RoleEnum.USER.getRole())
                        .content(userContent)
                        .build());
                requestMessages.add(ChatRequest.Input.Message.builder()
                        .role(RoleEnum.USER.getRole())
                        .content(userContent)
                        .build());
                List<ChatRequest.Input.Message.Content> systemContent = new ArrayList<>();
                // 系统消息
                systemContent.add(ChatRequest.Input.Message.Content.builder()
                        .video(String.valueOf(createEntity.getUrl()))
                        .build());
                historyMessages.add(ChatRequest.Input.Message.builder()
                        .role(RoleEnum.ASSISTANT.getRole())
                        .content(systemContent)
                        .build());
                requestMessages.add(ChatRequest.Input.Message.builder()
                        .role(RoleEnum.ASSISTANT.getRole())
                        .content(systemContent)
                        .build());
                // 更新
                historyEntity.setHistoryMessages(historyMessages);
                historyEntity.setRequestMessages(requestMessages);
                llmRepository.updateHistory(historyEntity);
            }
            return createEntity;
        }
        return CreateEntity.builder()
                .isSuccess(false)
                .message(handleEntity.getMessage())
                .build();
    }

    /**
     * 具体创作 - 由子类实现
     * @param createVidEntity
     * @return
     */
    protected abstract CreateEntity create(CreateVidEntity createVidEntity);

}

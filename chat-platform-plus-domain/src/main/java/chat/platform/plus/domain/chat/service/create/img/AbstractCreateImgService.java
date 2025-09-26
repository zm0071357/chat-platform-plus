package chat.platform.plus.domain.chat.service.create.img;

import chat.platform.plus.domain.chat.adapter.repository.LLMRepository;
import chat.platform.plus.domain.chat.model.entity.*;
import chat.platform.plus.domain.chat.model.valobj.ImgFunctionEnum;
import chat.platform.plus.domain.chat.model.valobj.ImgSizeEnum;
import chat.platform.plus.domain.chat.model.valobj.RoleEnum;
import chat.platform.plus.domain.chat.model.valobj.UserContentConstant;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLinkFactory;
import chat.platform.plus.types.design.framework.link.multition.chain.BusinessLinkedList;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import qwen.sdk.largemodel.chat.model.ChatRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片创作抽象类
 */
@Slf4j
public abstract class AbstractCreateImgService implements CreateImgService {

    @Resource
    private LLMRepository llmRepository;

    @Resource
    private BusinessLinkedList<CheckEntity, DefaultLinkFactory.DynamicContext, HandleEntity> createImgLogicLink;

    @Override
    public CreateEntity createImg(CreateImgEntity createImgEntity) throws Exception {
        CheckEntity checkEntity = CheckEntity.builder()
                .userId(createImgEntity.getUserId())
                .content(createImgEntity.getContent())
                .messageType(createImgEntity.getMessageType())
                .fileList(createImgEntity.getFileList())
                .fileListSize(createImgEntity.getFileListSize())
                .build();
        DefaultLinkFactory.DynamicContext dynamicContext = DefaultLinkFactory.DynamicContext.builder()
                .imgFunction(createImgEntity.getImgFunction())
                .build();
        // 责任链过滤
        HandleEntity handleEntity = createImgLogicLink.apply(checkEntity, dynamicContext);
        if (handleEntity.getIsSuccess()) {
            // 尝试生成图片
            CreateEntity createEntity = create(createImgEntity);
            if (createEntity.getIsSuccess()) {
                // 获取历史记录
                HistoryEntity historyEntity = llmRepository.getHistory(createImgEntity.getUserId(), createImgEntity.getHistoryCode());
                List<ChatRequest.Input.Message> historyMessages = historyEntity.getHistoryMessages();
                List<ChatRequest.Input.Message> requestMessages = historyEntity.getRequestMessages();
                // 写入历史记录
                List<ChatRequest.Input.Message.Content> userContent = new ArrayList<>();
                // 用户消息
                userContent.add(ChatRequest.Input.Message.Content.builder()
                        .text(createImgEntity.getImgFunction() + "：" + createImgEntity.getContent())
                        .build());
                if (!createImgEntity.getImgFunction().equals(ImgFunctionEnum.CREATE_IMAGE.getFunction())) {
                    userContent.add(ChatRequest.Input.Message.Content.builder()
                            .image(createImgEntity.getFileList().get(0).getUrl())
                            .build());
                }
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
                        .image(String.valueOf(createEntity.getUrl()))
                        .build());
                historyMessages.add(ChatRequest.Input.Message.builder()
                        .role(RoleEnum.ASSISTANT.getRole())
                        .content(systemContent)
                        .build());
                systemContent.add(ChatRequest.Input.Message.Content.builder()
                        .text("按照要求为用户生成图片")
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
     * @param createImgEntity
     * @return
     */
    protected abstract CreateEntity create(CreateImgEntity createImgEntity);
}

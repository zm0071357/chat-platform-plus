package chat.platform.plus.domain.chat.service;

import chat.platform.plus.domain.chat.model.entity.LLMHandleEntity;
import chat.platform.plus.domain.chat.model.entity.MessageEntity;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLLMFactory;
import chat.platform.plus.types.design.framework.link.LogicLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class LLMServiceImpl implements LLMService{

    @Resource
    private DefaultLLMFactory llmFactory;

    @Override
    public String chat(MessageEntity messageEntity) throws Exception {
        LogicLink<MessageEntity, DefaultLLMFactory.DynamicContext, LLMHandleEntity> logicLink = llmFactory.openLogicLink();
        LLMHandleEntity handleEntity = logicLink.apply(messageEntity, new DefaultLLMFactory.DynamicContext());
        return handleEntity.getIsSuccess().toString();
    }
}

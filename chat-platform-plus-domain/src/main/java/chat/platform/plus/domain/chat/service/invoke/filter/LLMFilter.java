package chat.platform.plus.domain.chat.service.invoke.filter;

import chat.platform.plus.domain.chat.model.entity.LLMHandleEntity;
import chat.platform.plus.domain.chat.model.entity.MessageEntity;
import chat.platform.plus.domain.chat.model.entity.UserEntity;
import chat.platform.plus.domain.chat.model.valobj.MessageConstant;
import chat.platform.plus.domain.chat.model.valobj.MessageTypeEnum;
import chat.platform.plus.domain.chat.service.invoke.factory.DefaultLLMFactory;
import chat.platform.plus.types.design.framework.link.AbstractLogicLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 大模型过滤节点 - 消息类型
 * 处理不同类型的信息
 */
@Slf4j
@Service
public class LLMFilter extends AbstractLogicLink<MessageEntity, DefaultLLMFactory.DynamicContext, LLMHandleEntity> {

    @Override
    public LLMHandleEntity apply(MessageEntity messageEntity, DefaultLLMFactory.DynamicContext dynamicContext) throws Exception {
        log.info("进入大模型过滤节点：{}", messageEntity.getUserId());
        if (MessageTypeEnum.get(messageEntity.getMessageType()).getType() == 5) {
            log.info("大模型过滤节点，无法处理的消息类型，结束：{}", messageEntity.getUserId());
            return LLMHandleEntity.builder()
                    .isSuccess(false)
                    .message(MessageConstant.Fail_IllegalParam)
                    .build();
        }
        return LLMHandleEntity.builder()
                .isSuccess(true)
                .message(MessageConstant.Success)
                .build();
    }

}